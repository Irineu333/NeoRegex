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

package com.neoutils.neoregex.core.repository.di

import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepositoryImpl
import com.neoutils.neoregex.core.repository.patterns.PatternsRepository
import com.neoutils.neoregex.core.repository.patterns.PatternsRepositoryImpl
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepositoryImpl
import com.neoutils.neoregex.core.repository.text.SampleRepository
import com.neoutils.neoregex.core.repository.text.SampleRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { PatternStateRepositoryImpl() } bind PatternStateRepository::class
    single { TestCasesRepositoryImpl() } bind TestCasesRepository::class
    single { SampleRepositoryImpl() } bind SampleRepository::class
    singleOf(::PatternsRepositoryImpl) bind PatternsRepository::class
}