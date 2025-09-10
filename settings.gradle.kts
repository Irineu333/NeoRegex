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

@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(
    ":application",
    ":core:design-system",
    ":core:resources",
    ":core:shared-ui",
    ":core:common",
    ":core:datasource",
    ":core:database",
    ":core:repository",
    ":core:manager",
    ":core:crashreport",
    ":feature:matcher",
    ":feature:about",
    ":feature:validator",
    ":feature:saved",
)

rootProject.name = "NeoRegex"
