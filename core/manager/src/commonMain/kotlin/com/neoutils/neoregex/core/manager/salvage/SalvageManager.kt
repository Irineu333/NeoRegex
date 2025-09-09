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

package com.neoutils.neoregex.core.manager.salvage

import com.neoutils.neoregex.core.manager.model.Opened
import com.neoutils.neoregex.core.datasource.model.Pattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SalvageManager {

    val flow: StateFlow<Opened?>
    val canSave: StateFlow<Boolean>

    suspend fun open(id: Long)
    suspend fun close()
    suspend fun update(block: (Pattern) -> Pattern)

    suspend fun save(name: String)
    suspend fun update()
    suspend fun sync()
    suspend fun delete(id: Long)
}
