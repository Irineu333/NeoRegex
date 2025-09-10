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

package com.neoutils.neoregex.core.crashreport.impl

import com.neoutils.neoregex.BuildKonfig
import com.neoutils.neoregex.NeoConfig
import com.neoutils.neoregex.core.crashreport.CrashReportService
import kotlin.js.json

@JsModule("@bugsnag/js")
@JsNonModule
private external val Bugsnag: dynamic

@JsModule("@bugsnag/browser-performance")
@JsNonModule
private external val BugsnagPerformance: dynamic

internal class CrashReportServiceImpl : CrashReportService {

    override fun setup() {
        val apiKey = BuildKonfig.BUGSNAG_API_KEY ?: return

        val config = json(
            "apiKey" to apiKey,
            "appVersion" to NeoConfig.version,
            "releaseStage" to BuildKonfig.STAGE
        )

        Bugsnag.start(config)
        BugsnagPerformance.default.start(config)
    }

    override fun report(t: Throwable) {
        Bugsnag.notify(t)
    }
}