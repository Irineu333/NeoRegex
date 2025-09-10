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

import com.codingfeline.buildkonfig.compiler.FieldSpec
import extension.*

plugins {
    alias(libs.plugins.neoutils.neoregex.core)
    alias(libs.plugins.buildkonfig)
}

group = config.module(name = "core")

kotlin {
    sourceSets {

        commonMain.dependencies {

            // NeoUtils
            implementation(libs.neoutils.highlight.compose)
        }

        webTest.dependencies {

            // junit
            implementation(catalog.kotlin.test)
        }

        desktopMain.dependencies {
            implementation(libs.dbus.java.core)
            implementation(libs.dbus.java.transport.native.unixsocket)
            implementation(libs.slf4j.nop)
        }
    }
}

buildkonfig {
    packageName = config.basePackage
    exposeObjectWithName = "NeoConfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, name = "version", value = config.version.name())
        buildConfigField(FieldSpec.Type.INT, name = "code", value = config.version.code().toString())
    }
}