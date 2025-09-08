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

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import com.neoutils.neoregex.core.manager.model.Navigation
import com.neoutils.neoregex.core.manager.navigator.NavigationManager
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.salvage_save_dialog_save_btn
import com.neoutils.neoregex.core.resources.salvage_save_dialog_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FrameWindowScope.ControllerSystemBar() {

    val navigation = koinInject<NavigationManager>()
    val salvageManager = koinInject<SalvageManager>()

    var showSavePatternDialog by remember { mutableStateOf(false) }

    val coroutine = rememberCoroutineScope()

    MenuBar {
        Menu(
            text = "Regex",
        ) {
            Item(
                text = "New",
                onClick = {
                    coroutine.launch {
                        salvageManager.close()
                    }
                }
            )

            Item(
                text = "Open",
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
                text = "Save",
                onClick = {
                    showSavePatternDialog = true
                }
            )
        }

        Menu(
            text = "Screen",
        ) {
            Item(
                text = "Matcher",
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
                text = "Validator",
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

            Item(
                text = "About",
                onClick = {
                    coroutine.launch {
                        navigation.emit(
                            Navigation.Event.Navigate(
                                screen = Navigation.Screen.About
                            )
                        )
                    }
                }
            )
        }
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
                Text(text = stringResource(Res.string.salvage_save_dialog_save_btn))
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