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

package com.neoutils.neoregex.core.manager.model

import com.neoutils.neoregex.core.common.extension.deepEquals
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.datasource.model.Pattern
import com.neoutils.neoregex.core.repository.model.PatternState
import com.neoutils.neoregex.core.repository.model.SampleState

data class Opened(
    val id: Long,
    val name: String,
    val updated: Boolean,
    val canUpdate: Boolean,
)

fun Opened(
    id: Long,
    patternState: PatternState,
    sample: CharSequence,
    testCases: List<TestCase>,
    patterns: List<Pattern>
): Opened? {

    return patterns.find {
        it.id == id
    }?.let { savedPattern ->
        val updated =
            sample == savedPattern.sample &&
                    patternState.text.value == savedPattern.pattern &&
                    testCases deepEquals savedPattern.testCases
        Opened(
            id = checkNotNull(savedPattern.id),
            name = savedPattern.title,
            updated = updated,
            canUpdate = !updated && patternState.isValid,
        )
    }
}