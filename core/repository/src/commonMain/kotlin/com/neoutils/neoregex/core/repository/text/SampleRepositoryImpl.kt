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

@file:OptIn(ExperimentalFoundationApi::class)

package com.neoutils.neoregex.core.repository.text

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import com.neoutils.neoregex.core.common.model.HistoryState

class SampleRepositoryImpl : SampleRepository {

    override val field = TextFieldState()

    override val textFlow = snapshotFlow { field.text }
    override val historyFlow = snapshotFlow { HistoryState(field.undoState) }

    override fun update(input: CharSequence) {
        field.edit {
            replace(0, length, input)
        }
    }

    override fun clear() {
        field.clearText()
        field.undoState.clearHistory()
    }

    override fun cleanUpdate(input: CharSequence) {
        field.edit {
            replace(0, length, input)
        }

        field.undoState.clearHistory()
    }

    override fun undo() = field.undoState.undo()

    override fun redo() = field.undoState.redo()
}
