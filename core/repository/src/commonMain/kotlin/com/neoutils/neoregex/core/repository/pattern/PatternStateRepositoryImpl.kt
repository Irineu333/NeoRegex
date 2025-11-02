/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
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

package com.neoutils.neoregex.core.repository.pattern

import com.neoutils.neoregex.core.common.manager.TextHistoryManager
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.repository.model.PatternState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class PatternStateRepositoryImpl(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val textHistoryManager: TextHistoryManager = TextHistoryManager()
) : PatternStateRepository {

    private val text = MutableStateFlow(TextState())

    override val flow = text.combine(
        textHistoryManager.flow
    ) { text, history ->
        PatternState(
            text = text,
            history = history
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PatternState(
            text = text.value,
            history = textHistoryManager.flow.value
        )
    )

    init {
        coroutineScope.launch {
            text.collect {
                textHistoryManager.push(it)
            }
        }
    }

    override fun update(input: TextState) {
        text.value = input.copy(
            value = input.value.substringBefore(
                delimiter = "\n"
            )
        )

        if (input.value == "(crash-test-123)") {
            throw RuntimeException("test")
        }
    }

    override fun undo() {
        text.value = textHistoryManager.undo() ?: return
    }

    override fun redo() {
        text.value = textHistoryManager.redo() ?: return
    }

    override fun clear() {
        textHistoryManager.clear()
        text.value = TextState()
    }

    override fun cleanUpdate(initial: TextState) {
        textHistoryManager.clear()
        text.value = initial
    }
}