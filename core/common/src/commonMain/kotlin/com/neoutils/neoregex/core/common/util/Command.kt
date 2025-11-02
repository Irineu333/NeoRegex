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

package com.neoutils.neoregex.core.common.util

import androidx.compose.ui.input.key.*
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform

enum class Command(
    val modifies: List<Special>,
    val key: Key,
    val type: KeyEventType = KeyEventType.KeyDown
) {
    UNDO(
        modifies = when (platform) {
            Platform.Desktop.MacOS -> {
                listOf(Special.META)
            }

            else -> {
                listOf(Special.CTRL)
            }
        },
        key = Key.Z
    ),
    REDO(
        modifies = when (platform) {
            Platform.Desktop.MacOS -> {
                listOf(
                    Special.SHIFT,
                    Special.META
                )
            }

            else -> {
                listOf(
                    Special.CTRL,
                    Special.SHIFT
                )
            }
        },
        key = Key.Z
    ),
    SAVE(
        modifies = when (platform) {
            Platform.Desktop.MacOS -> {
                listOf(
                    Special.META
                )
            }

            else -> {
                listOf(
                    Special.CTRL
                )
            }
        },
        key = Key.S
    );

    companion object {
        fun from(keyEvent: KeyEvent): Command? {

            val modifies = Special.from(keyEvent)

            return entries.find {
                it.modifies == modifies &&
                        it.key == keyEvent.key &&
                        it.type == keyEvent.type
            }
        }
    }
}