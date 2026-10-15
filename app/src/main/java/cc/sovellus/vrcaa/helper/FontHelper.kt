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

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import cc.sovellus.vrcaa.BuildConfig
import cc.sovellus.vrcaa.extension.customFontIsVariable
import cc.sovellus.vrcaa.extension.customFontPath
import cc.sovellus.vrcaa.extension.fontFamily
import cc.sovellus.vrcaa.extension.setCustomFont
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

/**
 * Imports, loads and removes the user's custom font (TTF/OTF).
 *
 * The picked file is copied into [Context.getNoBackupFilesDir], so it keeps working after the
 * original is moved or deleted, and a large font can't push the app over the Auto Backup quota.
 */
object FontHelper {

    /** Values stored in the `fontFamily` preference. */
    const val FONT_SYSTEM_DEFAULT = 0
    const val FONT_CUSTOM = 4

    const val MAX_FONT_SIZE_MB = 50
    private const val MAX_FONT_SIZE_BYTES = MAX_FONT_SIZE_MB * 1024L * 1024L

    private const val FONTS_DIRECTORY = "fonts"
    private const val IMPORT_TEMP_FILE = "import.tmp"

    /**
     * MIME types offered to the system file picker. Many file managers report fonts as
     * `application/octet-stream`, so that is included too; files are validated by their contents.
     */
    val PICKER_MIME_TYPES = arrayOf(
        "font/ttf",
        "font/otf",
        "font/sfnt",
        "application/x-font-ttf",
        "application/x-font-truetype",
        "application/x-font-otf",
        "application/x-font-opentype",
        "application/font-sfnt",
        "application/vnd.ms-opentype",
        "application/octet-stream",
    )

    sealed class ImportResult {
        data class Success(val name: String) : ImportResult()
        data object TooLarge : ImportResult()
        data object Invalid : ImportResult()
        data object Failed : ImportResult()
    }

    private var cachedKey: String? = null
    private var cachedFontFamily: FontFamily? = null

    /** Whether this build flavor offers the custom font option (phone builds only). */
    fun isSupported(): Boolean = BuildConfig.CUSTOM_FONT_SUPPORTED

    /**
     * Copies the font at [uri] into app storage, validates it and selects it.
     * Performs blocking I/O, so call it off the main thread.
     */
    fun importFont(context: Context, uri: Uri, preferences: SharedPreferences): ImportResult {
        val (displayName, declaredSize) = queryNameAndSize(context, uri)
        if (declaredSize != null && declaredSize > MAX_FONT_SIZE_BYTES) {
            return ImportResult.TooLarge
        }

        val directory = fontsDirectory(context)
        val temp = File(directory, IMPORT_TEMP_FILE)

        try {
            if (!directory.isDirectory && !directory.mkdirs()) {
                return ImportResult.Failed
            }

            // The declared size can be missing or wrong, so the copy enforces the limit as well.
            val input = context.contentResolver.openInputStream(uri) ?: return ImportResult.Failed
            val withinLimit = input.use { source ->
                temp.outputStream().use { target -> copyWithLimit(source, target, MAX_FONT_SIZE_BYTES) }
            }
            if (!withinLimit) {
                temp.delete()
                return ImportResult.TooLarge
            }

            val metadata = readMetadata(temp)
            if (metadata == null || !canLoad(temp)) {
                temp.delete()
                return ImportResult.Invalid
            }

            // A new name for every import, so Compose never serves a cached typeface for a replaced file.
            val extension = if (metadata.isOpenTypeCff) "otf" else "ttf"
            val fontFile = File(directory, "custom_${System.currentTimeMillis()}.$extension")
            if (!temp.renameTo(fontFile)) {
                temp.delete()
                return ImportResult.Failed
            }

            val name = metadata.familyName
                ?: displayName?.takeIf { it.isNotBlank() }
                ?: fontFile.name

            preferences.setCustomFont(
                path = fontFile.absolutePath,
                name = name,
                isVariable = metadata.isVariable,
                selectedFont = FONT_CUSTOM
            )
            directory.listFiles()?.filter { it != fontFile }?.forEach { it.delete() }

            return ImportResult.Success(name)
        } catch (_: IOException) {
            temp.delete()
            return ImportResult.Failed
        } catch (_: SecurityException) {
            temp.delete()
            return ImportResult.Failed
        }
    }

    /** Deletes the imported font. If it was selected, switches back to the system default font. */
    fun removeCustomFont(context: Context, preferences: SharedPreferences) {
        val selectedFont = preferences.fontFamily.takeIf { it != FONT_CUSTOM } ?: FONT_SYSTEM_DEFAULT
        preferences.setCustomFont(path = "", name = "", isVariable = false, selectedFont = selectedFont)
        fontsDirectory(context).listFiles()?.forEach { it.delete() }
        synchronized(this) {
            cachedKey = null
            cachedFontFamily = null
        }
    }

    /**
     * Called at startup, before the theme reads the font. Forgets the custom font when its file is
     * gone (e.g. after a cloud restore, since fonts aren't backed up), when the selected font can't
     * be loaded, or when this flavor doesn't support custom fonts. The crash screen uses the same
     * theme, so a broken font must never reach it.
     */
    fun sanitize(context: Context, preferences: SharedPreferences) {
        val path = preferences.customFontPath
        val isSelected = preferences.fontFamily == FONT_CUSTOM
        if (path.isEmpty() && !isSelected) return

        val isUsable = isSupported() && path.isNotEmpty() && File(path).isFile &&
            (!isSelected || customFontFamily(path, preferences.customFontIsVariable) != null)
        if (!isUsable) {
            removeCustomFont(context, preferences)
        }
    }

    /**
     * Returns the [FontFamily] for the imported font, or null if the file is missing or unreadable.
     * The result is cached per file, so this is cheap to call during composition.
     */
    @Synchronized
    fun customFontFamily(path: String, isVariable: Boolean): FontFamily? {
        if (path.isEmpty()) return null

        val key = "$path|$isVariable"
        if (key != cachedKey) {
            val file = File(path)
            cachedFontFamily = if (file.isFile && canLoad(file)) buildFontFamily(file, isVariable) else null
            cachedKey = key
        }
        return cachedFontFamily
    }

    private fun buildFontFamily(file: File, isVariable: Boolean): FontFamily {
        return if (isVariable) {
            // Variable font: use its real weights by setting the 'wght' axis for each one.
            FontFamily(
                listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold).map { weight ->
                    Font(
                        file = file,
                        weight = weight,
                        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
                    )
                }
            )
        } else {
            // Static font: register it as Regular only, so Compose synthesizes bold for heavier text.
            FontFamily(Font(file = file, weight = FontWeight.Normal))
        }
    }

    private fun fontsDirectory(context: Context): File = File(context.noBackupFilesDir, FONTS_DIRECTORY)

    private fun canLoad(file: File): Boolean {
        return try {
            // Returns null (rather than a fallback) when the file isn't a usable font.
            Typeface.Builder(file).build() != null
        } catch (_: Exception) {
            false
        }
    }

    private fun readMetadata(file: File): FontMetadataParser.Metadata? {
        return try {
            RandomAccessFile(file, "r").use { raf ->
                val buffer = raf.channel.map(FileChannel.MapMode.READ_ONLY, 0, raf.length())
                FontMetadataParser.parse(buffer)
            }
        } catch (_: Exception) {
            // I/O errors or malformed data the parser couldn't handle: not a usable font.
            null
        }
    }

    private fun queryNameAndSize(context: Context, uri: Uri): Pair<String?, Long?> {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                null,
                null,
                null
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return@use null to null

                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val name = if (nameIndex >= 0 && !cursor.isNull(nameIndex)) cursor.getString(nameIndex) else null
                val size = if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) cursor.getLong(sizeIndex) else null
                name to size
            } ?: (null to null)
        } catch (_: Exception) {
            null to null
        }
    }

    /** Copies [source] into [target]; returns false as soon as more than [limit] bytes were read. */
    private fun copyWithLimit(source: InputStream, target: OutputStream, limit: Long): Boolean {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0L
        while (true) {
            val read = source.read(buffer)
            if (read < 0) return true
            total += read
            if (total > limit) return false
            target.write(buffer, 0, read)
        }
    }
}
