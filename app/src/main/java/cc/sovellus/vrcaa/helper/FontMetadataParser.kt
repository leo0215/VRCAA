/*
 * Copyright (C) 2026. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.helper

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

/**
 * Minimal reader for the sfnt container used by TrueType (.ttf) and OpenType (.otf) fonts.
 *
 * It only reads what the custom font feature needs: whether the data is an sfnt font at all,
 * whether it is a variable font (has an `fvar` table) and its family name from the `name` table.
 * Font collections (.ttc) and web fonts (.woff/.woff2) are rejected.
 */
internal object FontMetadataParser {

    data class Metadata(
        /** Family name from the `name` table, or null if the font has no readable one. */
        val familyName: String?,
        /** True when the font has an `fvar` table, i.e. it exposes variation axes such as `wght`. */
        val isVariable: Boolean,
        /** True for OpenType fonts with CFF outlines (`OTTO`), which are normally shipped as .otf. */
        val isOpenTypeCff: Boolean,
    )

    private const val SFNT_VERSION_TRUETYPE = 0x00010000
    private const val SFNT_VERSION_APPLE_TRUETYPE = 0x74727565 // 'true'
    private const val SFNT_VERSION_OPENTYPE_CFF = 0x4F54544F // 'OTTO'

    private const val TAG_FVAR = 0x66766172 // 'fvar'
    private const val TAG_NAME = 0x6E616D65 // 'name'

    private const val TABLE_DIRECTORY_OFFSET = 12
    private const val TABLE_RECORD_SIZE = 16
    private const val NAME_HEADER_SIZE = 6
    private const val NAME_RECORD_SIZE = 12

    private const val NAME_ID_FAMILY = 1
    private const val NAME_ID_TYPOGRAPHIC_FAMILY = 16

    private const val PLATFORM_UNICODE = 0
    private const val PLATFORM_MACINTOSH = 1
    private const val PLATFORM_WINDOWS = 3

    private const val ENCODING_MACINTOSH_ROMAN = 0
    private val WINDOWS_UNICODE_ENCODINGS = setOf(0, 1, 10) // Symbol, Unicode BMP, Unicode full repertoire

    private const val LANGUAGE_MACINTOSH_ENGLISH = 0
    private const val LANGUAGE_WINDOWS_ENGLISH_US = 0x0409

    /** Returns the font's metadata, or null if [buffer] doesn't contain a TrueType/OpenType font. */
    fun parse(buffer: ByteBuffer): Metadata? {
        val data = buffer.slice().order(ByteOrder.BIG_ENDIAN)
        val size = data.limit().toLong()
        if (size < TABLE_DIRECTORY_OFFSET) return null

        val version = data.getInt(0)
        if (version != SFNT_VERSION_TRUETYPE &&
            version != SFNT_VERSION_APPLE_TRUETYPE &&
            version != SFNT_VERSION_OPENTYPE_CFF
        ) {
            return null
        }

        val numTables = data.u16(4)
        if (numTables == 0 || TABLE_DIRECTORY_OFFSET + numTables.toLong() * TABLE_RECORD_SIZE > size) {
            return null
        }

        var hasFvar = false
        var familyName: String? = null
        for (i in 0 until numTables) {
            val record = TABLE_DIRECTORY_OFFSET + i * TABLE_RECORD_SIZE
            when (data.getInt(record)) {
                TAG_FVAR -> hasFvar = true
                TAG_NAME -> familyName = readFamilyName(data, data.u32(record + 8), data.u32(record + 12))
            }
        }

        return Metadata(
            familyName = familyName,
            isVariable = hasFvar,
            isOpenTypeCff = version == SFNT_VERSION_OPENTYPE_CFF
        )
    }

    /**
     * Picks the best family name: the typographic family (name ID 16) over the legacy family (ID 1),
     * then US English Windows names, other Windows names, Unicode names and finally Mac Roman names.
     */
    private fun readFamilyName(data: ByteBuffer, tableOffset: Long, tableLength: Long): String? {
        val size = data.limit().toLong()
        if (tableLength < NAME_HEADER_SIZE || tableOffset + tableLength > size) return null

        val table = tableOffset.toInt()
        val count = data.u16(table + 2)
        val storage = table.toLong() + data.u16(table + 4)
        if (NAME_HEADER_SIZE + count.toLong() * NAME_RECORD_SIZE > tableLength) return null

        var bestName: String? = null
        var bestScore = -1
        for (i in 0 until count) {
            val record = table + NAME_HEADER_SIZE + i * NAME_RECORD_SIZE
            val nameId = data.u16(record + 6)
            if (nameId != NAME_ID_FAMILY && nameId != NAME_ID_TYPOGRAPHIC_FAMILY) continue

            val platformId = data.u16(record)
            val encodingId = data.u16(record + 2)
            val languageId = data.u16(record + 4)
            val charset = charsetFor(platformId, encodingId) ?: continue

            val score = (if (nameId == NAME_ID_TYPOGRAPHIC_FAMILY) 100 else 0) + platformScore(platformId, languageId)
            if (score <= bestScore) continue

            val length = data.u16(record + 8)
            val start = storage + data.u16(record + 10)
            if (length == 0 || start + length > size) continue

            val bytes = ByteArray(length)
            data.duplicate().apply { position(start.toInt()) }.get(bytes)
            val name = String(bytes, charset).trim { it <= ' ' }
            if (name.isEmpty()) continue

            bestName = name
            bestScore = score
        }
        return bestName
    }

    private fun charsetFor(platformId: Int, encodingId: Int): Charset? = when (platformId) {
        PLATFORM_UNICODE -> Charsets.UTF_16BE
        PLATFORM_WINDOWS -> if (encodingId in WINDOWS_UNICODE_ENCODINGS) Charsets.UTF_16BE else null
        // Mac Roman matches ASCII, which covers practically every family name stored this way.
        PLATFORM_MACINTOSH -> if (encodingId == ENCODING_MACINTOSH_ROMAN) Charsets.ISO_8859_1 else null
        else -> null
    }

    private fun platformScore(platformId: Int, languageId: Int): Int = when (platformId) {
        PLATFORM_WINDOWS -> if (languageId == LANGUAGE_WINDOWS_ENGLISH_US) 40 else 30
        PLATFORM_UNICODE -> 20
        PLATFORM_MACINTOSH -> if (languageId == LANGUAGE_MACINTOSH_ENGLISH) 10 else 5
        else -> 0
    }

    private fun ByteBuffer.u16(index: Int): Int = getShort(index).toInt() and 0xFFFF

    private fun ByteBuffer.u32(index: Int): Long = getInt(index).toLong() and 0xFFFFFFFFL
}
