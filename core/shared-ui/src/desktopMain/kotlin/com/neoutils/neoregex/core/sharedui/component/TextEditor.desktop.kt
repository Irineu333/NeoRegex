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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.neoutils.neoregex.core.common.extension.getBoundingBoxes
import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.core.common.model.DrawMatch
import com.neoutils.neoregex.core.designsystem.theme.LocalDimensions
import com.neoutils.neoregex.core.sharedui.extension.toText
import com.neoutils.neoregex.core.sharedui.extension.tooltip
import com.neoutils.neoregex.core.sharedui.extension.verticalOffset

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun TextEditor(
    state: TextFieldState,
    modifier: Modifier,
    onFocusChange: (FocusState) -> Unit,
    matches: List<Match>,
    textStyle: TextStyle,
    config: Config
) {

    val mergedTextStyle = typography.bodyMedium.copy(
        letterSpacing = 1.sp,
    ).merge(textStyle)

    val scrollState = rememberScrollState()

    val scrollbarAdapter = rememberScrollbarAdapter(scrollState)

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    var hoverOffset by remember { mutableStateOf<Offset?>(null) }

    val textMeasurer = rememberTextMeasurer()

    val dimensions = LocalDimensions.current

    Row(modifier) {

        LineNumbers(
            count = textLayout?.lineCount ?: 1,
            offset = scrollState.value,
            textStyle = TextStyle(
                lineHeight = mergedTextStyle.lineHeight,
                fontSize = mergedTextStyle.fontSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                ),
                color = colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .background(colorScheme.surfaceVariant)
                .fillMaxHeight()
        )

        // TODO(improve): it's not performant for large text
        BasicTextField(
            state = state,
            scrollState = scrollState,
            textStyle = mergedTextStyle.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                ),
                color = colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(colorScheme.onSurface),
            modifier = Modifier
                .background(colorScheme.surface)
                .onFocusChanged(onFocusChange)
                .padding(start = dimensions.nano.m)
                .weight(weight = 1f, fill = false)
                .fillMaxSize()
                .onPointerEvent(PointerEventType.Move) { event ->
                    hoverOffset = event.changes.first().position
                }
                .onPointerEvent(PointerEventType.Exit) {
                    hoverOffset = null
                }
                .drawWithContent {
                    val drawMatches = textLayout?.let { textLayout ->
                        matches.map { match ->
                            DrawMatch(
                                match = match,
                                rects = textLayout.getBoundingBoxes(
                                    match.range.first,
                                    match.range.last
                                ).map {
                                    it.translate(-scrollState.verticalOffset)
                                }
                            )
                        }
                    }.orEmpty()

                    drawMatches.forEach { (_, rects) ->
                        rects.forEach { rect ->
                            drawRect(
                                color = config.matchColor,
                                topLeft = Offset(
                                    x = rect.left,
                                    y = rect.top,
                                ),
                                size = rect.size * 0.95f,
                            )
                        }
                    }

                    drawContent()

                    hoverOffset?.let { offset ->
                        val drawMatch = drawMatches.find { (_, rects) ->
                            rects.any { it.contains(offset) }
                        }

                        drawMatch?.let { (match, rects) ->
                            rects.forEach { rect ->
                                drawRect(
                                    color = config.selectedMatchColor,
                                    topLeft = Offset(
                                        x = rect.left,
                                        y = rect.top,
                                    ),
                                    size = rect.size * 0.95f,
                                    style = Stroke(width = 1f)
                                )
                            }

                            tooltip(
                                anchorRect = rects.first {
                                    it.contains(offset)
                                }.let {
                                    Rect(
                                        left = offset.x,
                                        top = it.top,
                                        right = offset.x,
                                        bottom = it.bottom
                                    )
                                },
                                measure = textMeasurer.measure(
                                    text = match.toText(),
                                    style = mergedTextStyle.copy(
                                        color = config.tooltipTextColor,
                                    )
                                ),
                                backgroundColor = config.tooltipBackgroundColor,
                                dimensions = dimensions
                            )
                        }
                    }
                },
            onTextLayout = {
                textLayout = it()
            },
        )

        VerticalScrollbar(scrollbarAdapter)
    }
}
