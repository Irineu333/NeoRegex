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

import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.datasource.model.Pattern
import com.neoutils.neoregex.core.manager.model.Navigation
import com.neoutils.neoregex.core.manager.model.Opened
import com.neoutils.neoregex.core.manager.navigator.NavigationManager
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.repository.patterns.PatternsRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import com.neoutils.neoregex.core.repository.text.TextSampleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SalvageManagerImpl(
    private val patternsRepository: PatternsRepository,
    private val patternStateRepository: PatternStateRepository,
    private val testCasesRepository: TestCasesRepository,
    private val textStateRepository: TextSampleRepository,
    private val navigationManager: NavigationManager,
    coroutineScope: CoroutineScope
) : SalvageManager {

    private val opened = MutableStateFlow<Long?>(null)

    override val flow = combine(
        opened,
        patternStateRepository.flow,
        textStateRepository.flow,
        testCasesRepository.flow,
        patternsRepository.flow,
    ) { openedPatternId, pattern, sample, testCases, patterns ->
        openedPatternId?.let {
            Opened(
                id = it,
                patternState = pattern,
                sampleState = sample,
                testCases = testCases,
                patterns = patterns
            )
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    override val canSave = opened.combine(
        patternStateRepository.flow
    ) { opened, pattern ->
        opened == null && pattern.isValid
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false,
    )

    override suspend fun open(id: Long) {
        opened.value = id

        val pattern = patternsRepository.get(id) ?: return

        textStateRepository.clear(TextState(pattern.sample))
        patternStateRepository.clear(TextState(pattern.pattern))
        testCasesRepository.clear()

        sync()
    }

    override suspend fun close() {
        opened.value = null

        textStateRepository.clear()
        patternStateRepository.clear()
        testCasesRepository.clear()

        navigationManager.emit(Navigation.Event.Invalidate())
    }

    override suspend fun update(block: (Pattern) -> Pattern) {
        val id = opened.value ?: return

        patternsRepository.update(id, block)
    }

    override suspend fun update() {
        val id = opened.value ?: return

        patternsRepository.update(
            id = id
        ) { pattern ->
            pattern.copy(
                sample = textStateRepository.sample.text.value,
                pattern = patternStateRepository.pattern.text.value,
                testCases = testCasesRepository.all.map { testCase ->
                    TestCase(
                        uuid = testCase.uuid,
                        title = testCase.title,
                        text = testCase.text,
                        case = testCase.case
                    )
                }
            )
        }
    }

    override suspend fun sync() {
        val id = opened.value ?: return

        val pattern = patternsRepository.get(id) ?: return

        textStateRepository.update(TextState(pattern.sample))
        patternStateRepository.update(TextState(pattern.pattern))
        testCasesRepository.setAll(pattern.testCases)
    }

    override suspend fun delete(id: Long) {

        if (id == opened.value) close()

        patternsRepository.delete(id)
    }

    override suspend fun save(name: String) {

        if (!canSave.value) return

        val pattern = patternsRepository.save(
            Pattern(
                title = name,
                sample = textStateRepository.sample.text.value,
                pattern = patternStateRepository.pattern.text.value,
                testCases = testCasesRepository.all.map {
                    TestCase(
                        title = it.title,
                        text = it.text,
                        case = it.case,
                        uuid = it.uuid
                    )
                }
            )
        )

        opened.value = pattern.id
    }
}
