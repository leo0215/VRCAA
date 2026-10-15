package cc.sovellus.vrcaa.helper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FontMetadataParserTest {

    private data class NameRecord(
        val platformId: Int,
        val encodingId: Int,
        val languageId: Int,
        val nameId: Int,
        val value: String
    )

    private fun windowsName(nameId: Int, value: String, languageId: Int = 0x0409) =
        NameRecord(platformId = 3, encodingId = 1, languageId = languageId, nameId = nameId, value = value)

    private fun macName(nameId: Int, value: String) =
        NameRecord(platformId = 1, encodingId = 0, languageId = 0, nameId = nameId, value = value)

    private fun ByteBuffer.putU16(value: Int): ByteBuffer = putShort(value.toShort())

    /** Builds a format 0 `name` table. */
    private fun nameTable(vararg records: NameRecord): ByteArray {
        val strings = records.map { record ->
            if (record.platformId == 1) {
                record.value.toByteArray(Charsets.ISO_8859_1)
            } else {
                record.value.toByteArray(Charsets.UTF_16BE)
            }
        }
        val storageOffset = 6 + records.size * 12
        val buffer = ByteBuffer.allocate(storageOffset + strings.sumOf { it.size }).order(ByteOrder.BIG_ENDIAN)
        buffer.putU16(0) // format
        buffer.putU16(records.size)
        buffer.putU16(storageOffset)

        var offset = 0
        records.forEachIndexed { index, record ->
            buffer.putU16(record.platformId)
            buffer.putU16(record.encodingId)
            buffer.putU16(record.languageId)
            buffer.putU16(record.nameId)
            buffer.putU16(strings[index].size)
            buffer.putU16(offset)
            offset += strings[index].size
        }
        strings.forEach { buffer.put(it) }
        return buffer.array()
    }

    /** Builds an sfnt file with the given [version] and tables (tag to contents). */
    private fun sfnt(version: Int, tables: Map<String, ByteArray>): ByteBuffer {
        val headerSize = 12 + tables.size * 16
        val buffer = ByteBuffer.allocate(headerSize + tables.values.sumOf { it.size }).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(version)
        buffer.putU16(tables.size)
        buffer.putU16(0) // searchRange
        buffer.putU16(0) // entrySelector
        buffer.putU16(0) // rangeShift

        var offset = headerSize
        tables.forEach { (tag, contents) ->
            buffer.put(tag.toByteArray(Charsets.US_ASCII))
            buffer.putInt(0) // checksum
            buffer.putInt(offset)
            buffer.putInt(contents.size)
            offset += contents.size
        }
        tables.values.forEach { buffer.put(it) }
        buffer.flip()
        return buffer
    }

    private fun parseNames(vararg records: NameRecord): String? =
        FontMetadataParser.parse(sfnt(TRUETYPE, mapOf("name" to nameTable(*records))))?.familyName

    @Test
    fun parsesStaticTrueTypeFont() {
        val metadata = FontMetadataParser.parse(
            sfnt(TRUETYPE, mapOf("cmap" to ByteArray(4), "name" to nameTable(windowsName(1, "Test Sans"))))
        )

        assertNotNull(metadata)
        assertEquals("Test Sans", metadata!!.familyName)
        assertFalse(metadata.isVariable)
        assertFalse(metadata.isOpenTypeCff)
    }

    @Test
    fun detectsVariableFontFromFvarTable() {
        val metadata = FontMetadataParser.parse(
            sfnt(TRUETYPE, mapOf("fvar" to ByteArray(16), "name" to nameTable(windowsName(1, "Test Sans"))))
        )

        assertTrue(metadata!!.isVariable)
    }

    @Test
    fun recognizesOpenTypeCffFont() {
        val metadata = FontMetadataParser.parse(sfnt(OPENTYPE_CFF, mapOf("CFF " to ByteArray(8))))

        assertNotNull(metadata)
        assertTrue(metadata!!.isOpenTypeCff)
        assertNull(metadata.familyName)
    }

    @Test
    fun prefersTypographicFamilyName() {
        assertEquals(
            "Test Sans",
            parseNames(windowsName(1, "Test Sans Medium"), windowsName(16, "Test Sans"))
        )
    }

    @Test
    fun prefersUsEnglishWindowsName() {
        assertEquals(
            "Test Sans",
            parseNames(windowsName(1, "測試黑體", languageId = 0x0404), windowsName(1, "Test Sans"))
        )
    }

    @Test
    fun usesLocalizedNameWhenNoEnglishNameExists() {
        assertEquals("測試黑體", parseNames(windowsName(1, "測試黑體", languageId = 0x0404)))
    }

    @Test
    fun fallsBackToMacintoshRomanName() {
        assertEquals("Test Sans", parseNames(macName(1, "Test Sans")))
    }

    @Test
    fun skipsBlankNames() {
        assertEquals("Test Sans", parseNames(windowsName(16, "  "), windowsName(1, "Test Sans")))
    }

    @Test
    fun rejectsNonFontData() {
        assertNull(FontMetadataParser.parse(ByteBuffer.wrap("PK\u0003\u0004 not a font at all".toByteArray())))
        assertNull(FontMetadataParser.parse(ByteBuffer.wrap(ByteArray(0))))
    }

    @Test
    fun rejectsFontCollectionsAndWebFonts() {
        assertNull(FontMetadataParser.parse(sfnt(0x74746366 /* 'ttcf' */, mapOf("name" to ByteArray(6)))))
        assertNull(FontMetadataParser.parse(sfnt(0x774F4646 /* 'wOFF' */, mapOf("name" to ByteArray(6)))))
    }

    @Test
    fun rejectsTruncatedTableDirectory() {
        // The header claims 10 tables, but the data ends right after it.
        val buffer = ByteBuffer.allocate(12).apply {
            putInt(0, TRUETYPE)
            putShort(4, 10.toShort())
        }

        assertNull(FontMetadataParser.parse(buffer))
    }

    @Test
    fun toleratesNameTableOutsideTheFile() {
        val buffer = sfnt(TRUETYPE, mapOf("name" to nameTable(windowsName(1, "Test Sans"))))
        buffer.putInt(12 + 8, 0x7FFFFFF0) // offset field of the first table record

        val metadata = FontMetadataParser.parse(buffer)

        assertNotNull(metadata)
        assertNull(metadata!!.familyName)
    }

    @Test
    fun readsBundledGoogleSans() {
        val metadata = parseBundledFont("googlesans.ttf")

        assertEquals("Google Sans", metadata.familyName)
        assertTrue(metadata.isVariable)
        assertFalse(metadata.isOpenTypeCff)
    }

    @Test
    fun readsBundledGoogleSansFlex() {
        val metadata = parseBundledFont("googlesansflex.ttf")

        assertEquals("Google Sans Flex", metadata.familyName)
        assertTrue(metadata.isVariable)
        assertFalse(metadata.isOpenTypeCff)
    }

    private fun parseBundledFont(name: String): FontMetadataParser.Metadata {
        // Unit tests run with the module directory as the working directory.
        val file = File("src/main/res/font/$name")
        assumeTrue("Bundled font $name not found", file.isFile)

        val metadata = FontMetadataParser.parse(ByteBuffer.wrap(file.readBytes()))
        assertNotNull(metadata)
        return metadata!!
    }

    private companion object {
        const val TRUETYPE = 0x00010000
        const val OPENTYPE_CFF = 0x4F54544F // 'OTTO'
    }
}
