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

package com.neoutils.neoregex.feature.matcher.action

import com.neoutils.neoregex.core.common.model.Field
import com.neoutils.neoregex.core.common.model.TextState

sealed class MatcherAction {

    data object Toggle : MatcherAction()

    data class TargetChange(
        val field: Field
    ) : MatcherAction()

    sealed class History : MatcherAction() {

        abstract val textState: Field?

        data class Undo(
            override val textState: Field? = null
        ) : History()

        data class Redo(
            override val textState: Field? = null
        ) : History()
    }
}