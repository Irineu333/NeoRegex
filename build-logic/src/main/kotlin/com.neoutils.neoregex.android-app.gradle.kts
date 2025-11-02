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

import extension.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
}

kotlin {

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
            vendor.set(JvmVendorSpec.JETBRAINS)
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {

            // activity
            implementation(catalog.androidx.activity.compose)
        }
    }
}

android {
    namespace = config.basePackage
    compileSdk = catalog.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.neo.regex" // Legacy package

        minSdk = catalog.versions.android.minSdk.get().toInt()
        targetSdk = catalog.versions.android.targetSdk.get().toInt()

        versionCode = config.version.code()
        versionName = config.version.name()
    }

    buildFeatures {
        compose = true
    }

    signingConfigs {
        create("release") {
            rootDir
                .resolve("keystore.properties")
                .keystore()?.let {
                    storeFile = it.storeFile
                    storePassword = it.storePassword
                    keyAlias = it.keyAlias
                    keyPassword = it.keyPassword
                }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
