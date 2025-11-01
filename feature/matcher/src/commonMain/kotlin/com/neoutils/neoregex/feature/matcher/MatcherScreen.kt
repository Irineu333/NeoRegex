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

package com.neoutils.neoregex.feature.matcher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.neoutils.neoregex.core.common.model.Field
import com.neoutils.neoregex.core.common.util.Command
import com.neoutils.neoregex.core.designsystem.component.ErrorTooltip
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.component.Footer
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.core.sharedui.component.TextEditor
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MatcherScreen : Screen {

    override val key = Uuid.random().toString()

    @Composable
    override fun Content() = Column(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {

        val viewModel = koinScreenModel<MatcherViewModel>()

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        BoxWithConstraints(Modifier.weight(weight = 1f)) {

            TextEditor(
                state = viewModel.sampleField,
                onFocusChange = {
                    if (it.isFocused) {
                        viewModel.onAction(
                            MatcherAction.TargetChange(Field.TEXT)
                        )
                    }
                },
                matches = remember(uiState.result) {
                    when (val result = uiState.result) {
                        is MatcherUiState.Result.Failure -> listOf()
                        is MatcherUiState.Result.Success -> result.matches
                    }
                },
            )

            Performance(
                performance = uiState.performance,
            )
        }

        Footer(
            pattern = uiState.inputs.regex,
            history = uiState.history,
            onAction = viewModel::onAction,
            tooling = {
                AnimatedContent(
                    targetState = uiState.result,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { result ->
                    Box(Modifier.size(dimensions.large.m)) {
                        if (result is MatcherUiState.Result.Failure) {
                            ErrorTooltip(result.error)
                        }
                    }
                }
            },
            onFocus = {
                if (it.isFocused) {
                    viewModel.onAction(
                        MatcherAction.TargetChange(Field.REGEX)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
