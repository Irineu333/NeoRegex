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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.manager.model.Navigation
import com.neoutils.neoregex.core.manager.navigator.NavigationManager
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.about_libraries_btn
import com.neoutils.neoregex.core.resources.about_license_btn
import com.neoutils.neoregex.core.resources.about_source_code_btn
import com.neoutils.neoregex.core.resources.menu_about_btn
import com.neoutils.neoregex.core.resources.menu_new_btn
import com.neoutils.neoregex.core.resources.menu_open_btn
import com.neoutils.neoregex.core.resources.menu_pattern_btn
import com.neoutils.neoregex.core.resources.menu_save_btn
import com.neoutils.neoregex.core.resources.salvage_save_dialog_save_btn
import com.neoutils.neoregex.core.resources.salvage_save_dialog_title
import com.neoutils.neoregex.core.resources.screen_matcher
import com.neoutils.neoregex.core.resources.screen_validator
import com.neoutils.neoregex.core.resources.theme_dark
import com.neoutils.neoregex.core.resources.theme_light
import com.neoutils.neoregex.core.resources.theme_system
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FrameWindowScope.ControllerSystemBar() = MenuBar {

    val navigation = koinInject<NavigationManager>()
    val salvageManager = koinInject<SalvageManager>()
    val preferencesDataSource = koinInject<PreferencesDataSource>()

    var showSavePatternDialog by remember { mutableStateOf(false) }
    val canSave by salvageManager.canSave.collectAsStateWithLifecycle(initialValue = false)
    val screen by navigation.screen.collectAsState()
    val preferences by preferencesDataSource.flow.collectAsState()

    val coroutine = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    Menu(text = stringResource(Res.string.menu_pattern_btn)) {
        Item(
            text = stringResource(Res.string.menu_new_btn),
            onClick = {
                coroutine.launch {
                    salvageManager.close()
                }
            }
        )

        Item(
            text = stringResource(Res.string.menu_open_btn),
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Saved
                        )
                    )
                }
            }
        )

        Item(
            text = stringResource(Res.string.menu_save_btn),
            enabled = canSave,
            onClick = {
                showSavePatternDialog = true
            }
        )
    }

    Menu(text = stringResource(screen.title)) {
        Item(
            text = stringResource(Res.string.screen_matcher),
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Matcher
                        )
                    )
                }
            }
        )

        Item(
            text = stringResource(Res.string.screen_validator),
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Validator
                        )
                    )
                }
            }
        )
    }

    Menu(
        text = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> stringResource(Res.string.theme_system)
            Preferences.ColorTheme.LIGHT -> stringResource(Res.string.theme_light)
            Preferences.ColorTheme.DARK -> stringResource(Res.string.theme_dark)
        }
    ) {
        Item(
            text = stringResource(Res.string.theme_dark),
            onClick = {
                preferencesDataSource.update {
                    it.copy(
                        colorTheme = Preferences.ColorTheme.DARK,
                    )
                }
            }
        )

        Item(
            text = stringResource(Res.string.theme_light),
            onClick = {
                preferencesDataSource.update {
                    it.copy(
                        colorTheme = Preferences.ColorTheme.LIGHT,
                    )
                }
            }
        )

        Item(
            text = stringResource(Res.string.theme_system),
            onClick = {
                preferencesDataSource.update {
                    it.copy(
                        colorTheme = Preferences.ColorTheme.SYSTEM,
                    )
                }
            }
        )
    }

    Menu(text = stringResource(Res.string.menu_about_btn)) {
        Item(
            text = stringResource(Res.string.about_libraries_btn),
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Libraries
                        )
                    )
                }
            }
        )

        Item(
            text = stringResource(Res.string.about_source_code_btn),
            onClick = {
                uriHandler.openUri(
                    uri = "https://github.com/NeoUtils/NeoRegex"
                )
            }
        )

        Item(
            text = stringResource(Res.string.about_license_btn),
            onClick = {
                uriHandler.openUri(
                    uri = "https://github.com/NeoUtils/NeoRegex#GPL-3.0-1-ov-file"
                )
            }
        )
    }

    if (showSavePatternDialog) {
        PatternNameDialog(
            onDismissRequest = {
                showSavePatternDialog = false
            },
            onConfirm = {
                coroutine.launch {
                    salvageManager.save(it)
                }
            },
            confirmLabel = {
                Text(stringResource(Res.string.salvage_save_dialog_save_btn))
            },
            title = {
                Text(
                    text = stringResource(Res.string.salvage_save_dialog_title),
                    color = colorScheme.onSurfaceVariant,
                    style = typography.titleMedium.copy(
                        fontFamily = null,
                    )
                )
            }
        )
    }
}
