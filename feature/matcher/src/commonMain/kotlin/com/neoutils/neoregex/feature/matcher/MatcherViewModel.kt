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

@file:OptIn(ExperimentalTime::class, FlowPreview::class)

package com.neoutils.neoregex.feature.matcher

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neoregex.core.common.model.Field
import com.neoutils.neoregex.core.common.model.HistoryState
import com.neoutils.neoregex.core.common.model.Inputs
import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.repository.text.TextSampleRepository
import com.neoutils.neoregex.core.sharedui.event.FooterAction
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MatcherViewModel(
    private val patternStateRepository: PatternStateRepository,
    private val textSampleRepository: TextSampleRepository
) : ScreenModel {

    private val field = MutableStateFlow<Field?>(value = null)

    private val resultFlow = combine(
        textSampleRepository.flow.map { it.text.value }.distinctUntilChanged(),
        patternStateRepository.flow.map { it.text.value }.distinctUntilChanged()
    ) { text, pattern ->

        if (pattern.isEmpty()) {
            return@combine MatcherUiState.Result.Success()
        }

        val regex = try {
            Regex(pattern)
        } catch (t: Throwable) {
            return@combine MatcherUiState.Result.Failure(
                error = t.message ?: "Invalid regex pattern"
            )
        }

        val start = Clock.System.now()

        val result = regex.findAll(text)

        val end = Clock.System.now()

        val matches = buildList {
            result.forEachIndexed { index, match ->
                add(
                    Match(
                        text = match.value,
                        range = match.range,
                        groups = match.groupValues.drop(n = 1),
                        number = index.inc(),
                    )
                )
            }
        }

        MatcherUiState.Result.Success(
            matches = matches,
            performance = Performance(
                duration = end - start,
                matches = matches.size
            )
        )
    }

    private val historyFlow = combine(
        field,
        textSampleRepository.flow.map { it.history },
        patternStateRepository.flow.map { it.history }
    ) { target, textHistory, patternHistory ->
        when (target) {
            Field.TEXT -> textHistory
            Field.REGEX -> patternHistory

            null -> HistoryState()
        }
    }

    private val inputFlow = combine(
        field,
        textSampleRepository.flow.map { it.text },
        patternStateRepository.flow.map { it.text },
    ) { target, text, pattern ->
        Inputs(
            field = target,
            text = text,
            regex = pattern
        )
    }

    val uiState = combine(
        inputFlow,
        historyFlow,
        resultFlow.debounce {
            when (it) {
                is MatcherUiState.Result.Failure -> ERROR_DELAY
                is MatcherUiState.Result.Success -> 0L
            }
        }
    ) { inputs, history, result ->
        MatcherUiState(
            inputs = inputs,
            history = history,
            result = result,
            performance = when (result) {
                is MatcherUiState.Result.Failure -> Performance()
                is MatcherUiState.Result.Success -> result.performance
            }
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MatcherUiState()
    )

    fun onAction(action: FooterAction) {
        when (action) {
            is FooterAction.UpdateRegex -> {
                patternStateRepository.update(action.text)
            }

            is FooterAction.History.Redo -> {
                redo(
                    field = action.field
                        ?: field.value
                        ?: return
                )
            }

            is FooterAction.History.Undo -> {
                undo(
                    field = action.field
                        ?: field.value
                        ?: return
                )
            }
        }
    }

    fun onAction(action: MatcherAction) {
        when (action) {
            is MatcherAction.UpdateText -> {
                textSampleRepository.update(action.text)
            }

            is MatcherAction.TargetChange -> {
                field.value = action.field
            }

            is MatcherAction.History.Undo -> {
                undo(
                    field = action.textState
                        ?: field.value
                        ?: return
                )
            }

            is MatcherAction.History.Redo -> {
                redo(
                    field = action.textState
                        ?: field.value
                        ?: return
                )
            }

            MatcherAction.Toggle -> {
                field.value = when (field.value) {
                    Field.TEXT -> Field.REGEX
                    Field.REGEX -> Field.TEXT
                    null -> Field.TEXT
                }
            }
        }
    }

    private fun redo(field: Field) {
        when (field) {
            Field.TEXT -> {
                textSampleRepository.redo()
            }

            Field.REGEX -> {
                patternStateRepository.redo()
            }
        }
    }

    private fun undo(field: Field) {
        when (field) {
            Field.TEXT -> {
                textSampleRepository.undo()
            }

            Field.REGEX -> {
                patternStateRepository.undo()
            }
        }
    }

    companion object {
        private const val ERROR_DELAY = 500L
    }
}
