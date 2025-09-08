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

package com.neoutils.neoregex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.platform.isMacOS
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.Command
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.WindowStateDataSource
import com.neoutils.neoregex.core.datasource.extension.observe
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.datasource.remember.rememberWindowState
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.*
import com.neoutils.neoregex.core.sharedui.event.SalvageAction
import com.neoutils.neoregex.core.sharedui.extension.WindowFocus
import com.neoutils.neoregex.core.sharedui.extension.rememberWindowFocus
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.DesktopApp() {

    val preferencesDataSource = koinInject<PreferencesDataSource>()
    val windowStateDataSource = koinInject<WindowStateDataSource>()

    val salvageManager = koinInject<SalvageManager>()

    val preferences by preferencesDataSource.flow.collectAsState()
    val windowState by windowStateDataSource.flow.collectAsState()

    val coroutine = rememberCoroutineScope()

    NeoTheme(
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) {
        NeoWindow(
            header = { HeaderImpl() },
            windowState = rememberWindowState(windowState)
        ) {
            windowStateDataSource.observe(window)

            App(
                modifier = Modifier.onKeyEvent {
                    when (Command.from(it)) {
                        Command.Save -> {
                            coroutine.launch {
                                salvageManager.update()
                            }
                            true
                        }

                        else -> false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrameWindowScope.HeaderImpl(
    modifier: Modifier = Modifier,
    preferencesDataSource: PreferencesDataSource = koinInject(),
    salvageManager: SalvageManager = koinInject(),
) {

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    NeoHeader(
        modifier = modifier,
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) { padding ->

        val direction = LocalLayoutDirection.current

        val focus = rememberWindowFocus()

        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = when (focus) {
                    WindowFocus.FOCUSED -> colorScheme.surfaceVariant
                    WindowFocus.UNFOCUSED -> colorScheme.surfaceBright
                },
            ),
            navigationIcon = {
                val startPadding = padding.calculateStartPadding(direction)

                if (platform.isMacOS) {
                    ControllerSystemBar()
                } else {
                    Controller(
                        modifier = Modifier.padding(
                            start = startPadding + dimensions.nano.m
                        ).height(dimensions.large.x)
                    )
                }
            },
            title = {
                AnimatedContent(
                    modifier = modifier,
                    targetState = salvageManager
                        .flow
                        .collectAsStateWithLifecycle(
                            initialValue = null
                        ).value,
                    contentKey = { it != null },
                    transitionSpec = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down
                        ) togetherWith slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down
                        )
                    },
                    contentAlignment = Alignment.Center,
                ) { salvage ->
                    if (salvage == null) {
                        Text(
                            text = stringResource(Res.string.app_name),
                            style = typography.titleLarge.copy(
                                fontFamily = null
                            )
                        )
                    } else {
                        val coroutine = rememberCoroutineScope()

                        Salvage(
                            modifier = Modifier
                                .height(dimensions.large.x)
                                .background(
                                    color = colorScheme.onSurface.copy(
                                        alpha = 0.05f
                                    ).compositeOver(
                                        colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(dimensions.nano.m)
                                ).padding(dimensions.nano.m),
                            opened = salvage,
                            onAction = { action ->
                                when (action) {
                                    SalvageAction.Close -> {
                                        coroutine.launch {
                                            salvageManager.close()
                                        }
                                    }

                                    SalvageAction.Update -> {
                                        coroutine.launch {
                                            salvageManager.update()
                                        }
                                    }

                                    is SalvageAction.ChangeName -> {
                                        coroutine.launch {
                                            salvageManager.update {
                                                it.copy(
                                                    title = action.name
                                                )
                                            }
                                        }
                                    }

                                    SalvageAction.Reset -> {
                                        coroutine.launch {
                                            salvageManager.sync()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            },
            actions = {

                val endPadding = padding.calculateEndPadding(direction)

                Options(
                    modifier = Modifier.padding(
                        end = endPadding + dimensions.nano.m
                    ).height(dimensions.large.m)
                )
            }
        )
    }
}
