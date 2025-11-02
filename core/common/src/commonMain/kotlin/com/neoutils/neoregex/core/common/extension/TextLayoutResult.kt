/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neoutils.neoregex.core.common.extension

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult

fun TextLayoutResult.getBoundingBoxes(start: Int, end: Int): List<Rect> {

    val boxes = mutableListOf<Rect>()

    var lastRect: Rect? = null
    var lastLine: Int? = null

    if (start >= layoutInput.text.length) return listOf()
    if (end >= layoutInput.text.length) return listOf()

    for (offset in start..end) {

        var rect = getBoundingBox(offset)
        val line = getLineForOffset(offset)

        if (lastRect != null && lastLine == line) {
            boxes.remove(lastRect)
            rect = lastRect.union(rect)
        }

        if (lastRect != null && lastLine != line) {
            boxes.remove(lastRect)
            boxes.add(
                lastRect.copy(
                    right = size.width.toFloat(),
                )
            )
        }

        lastLine = line
        lastRect = rect
        boxes.add(rect)
    }
    return boxes
}
