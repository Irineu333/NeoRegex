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

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.core.common.model.TextState

@Composable
expect fun TextEditor(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    onFocusChange: (FocusState) -> Unit = {},
    matches: List<Match> = listOf(),
    textStyle: TextStyle = TextStyle(),
    config: Config = Config(colorScheme)
)

data class Config(
    val matchColor: Color,
    val selectedMatchColor: Color,
    val tooltipBackgroundColor: Color,
    val tooltipTextColor: Color
) {
    constructor(colorScheme: ColorScheme) : this(
        selectedMatchColor = colorScheme.onSurface,
        matchColor = colorScheme.secondary,
        tooltipBackgroundColor = colorScheme.secondaryContainer,
        tooltipTextColor = colorScheme.onSecondaryContainer
    )
}
