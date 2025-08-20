// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.gallery.service.util

import android.database.Cursor
import android.provider.MediaStore
import java.nio.charset.StandardCharsets
import timber.log.Timber

object MediaStoreDebugUtils {

  // Debug method to dump the cursor to logcat as CSV
  // It is the caller's responsibility to open/close the cursor
  fun dumpCursorToCsv(cursor: Cursor) {
    Timber.i("Dumping cursor to logcat as CSV...")
    val csvTableRows = emptyList<String>().toMutableList()
    // Header
    var csvHeader = ""
    for (i in 0 until cursor.columnCount) {
      csvHeader += "${cursor.getColumnName(i)},"
    }
    csvTableRows.add(csvHeader)
    // Entries
    cursor.moveToFirst()
    while (!cursor.isAfterLast) {
      var csvRow = ""
      for (i in 0 until cursor.columnCount) {
        csvRow +=
            try {
              if (cursor.getColumnName(i) == MediaStore.Files.FileColumns.XMP) {
                val xmpBlob = cursor.getBlob(i)
                val xmpString =
                    String(xmpBlob, StandardCharsets.UTF_8)
                        .replace("\"", "\'") // Replace double quotes with single quotes
                "\"$xmpString\","
              } else {
                "${cursor.getString(i)},"
              }
            } catch (e: Exception) {
              Timber.w(
                  "ENTRY #${cursor.position}: Failed to get column ${
                            cursor.getColumnName(i)
                        }. Reason: ${e.message}"
              )
              "null,"
            }
      }
      csvTableRows.add(csvRow)
      cursor.moveToNext()
    }
    Timber.i(csvTableRows.joinToString("\n"))
  }
}
