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
import extension.config
import extension.environment
import extension.module
import model.Config

plugins {
    alias(libs.plugins.neoutils.neoregex.core)
    alias(libs.plugins.buildkonfig)
}

group = config.module(name = "core")

kotlin {
    sourceSets {

        commonMain.dependencies {
            // modules
            api(projects.core.common)
        }

        androidMain.dependencies {

            // bugsnag
            implementation(libs.bugsnag.android)
            implementation(libs.bugsnag.android.performance)
        }

        desktopMain.dependencies {

            // bugsnag
            implementation(libs.bugsnag.desktop)
        }

        webMain.dependencies {
            implementation(npm("@bugsnag/js", "8.2.0"))
            implementation(npm("@bugsnag/browser-performance", "2.12.0"))
        }
    }
}

buildkonfig {
    packageName = config.basePackage

    defaultConfigs {
        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "STAGE",
            value = when (config.version.stage) {
                Config.Stage.DEVELOP -> "development"
                Config.Stage.ALPHA -> "alpha"
                Config.Stage.BETA -> "beta"
                Config.Stage.RELEASE_CANDIDATE -> "release-candidate"
                Config.Stage.RELEASE -> "production"
            },
        )
    }

    targetConfigs {

        val environment = rootDir
            .resolve("environment.properties")
            .environment()

        create("android") {
            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "BUGSNAG_API_KEY",
                value = environment?.bugsnagAndroidApiKey,
                nullable = true
            )
        }

        create("desktop") {
            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "BUGSNAG_API_KEY",
                value = environment?.bugsnagDesktopApiKey,
                nullable = true
            )
        }

        create("web") {
            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = "BUGSNAG_API_KEY",
                value = environment?.bugsnagWebApikey,
                nullable = true
            )
        }
    }
}