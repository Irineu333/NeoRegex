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

package extension

import model.Config
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

val config = Config(
    version = Config.Version(
        major = 3,
        minor = 3,
        patch = 0,
        stage = Config.Stage.DEVELOP
    ),
    android = Config.Android(
        compileSdk = 34,
        minSdk = 24,
        targetSdk = 34
    ),
    basePackage = "com.neoutils.neoregex",
    name = "NeoRegex"
)

val Project.catalog
    get() = the<LibrariesForLibs>()
