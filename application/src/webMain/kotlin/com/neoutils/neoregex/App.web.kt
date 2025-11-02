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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.neoutils.neoregex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.extension.toCss
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.resources.web_warning_text
import com.neoutils.neoregex.core.sharedui.component.Controller
import com.neoutils.neoregex.core.sharedui.component.Options
import com.neoutils.neoregex.core.sharedui.extension.surface
import kotlinx.browser.document
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun WebApp() {

    val preferencesDataSource = koinInject<PreferencesDataSource>()

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    NeoTheme(
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) {

        val background = colorScheme.background.toCss()

        LaunchedEffect(background) {
            val body = checkNotNull(document.body)
            body.style.backgroundColor = background
        }

        Scaffold(
            topBar = {
                TopLabel(
                    text = stringResource(Res.string.web_warning_text),
                    visible = preferences.showWebWarning,
                    onClose = {
                        preferencesDataSource.update {
                            it.copy(
                                showWebWarning = false
                            )
                        }
                    }
                ) {
                    Header()
                }
            },
        ) {
            App(Modifier.padding(it))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    shadowElevation: Dp = dimensions.nano.m
) = TopAppBar(
    modifier = modifier.surface(
        shape = RectangleShape,
        backgroundColor = colorScheme.surfaceContainer,
        shadowElevation = LocalDensity.current.run {
            shadowElevation.toPx()
        }
    ),
    title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.default.x)
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = typography.titleLarge.copy(
                    fontFamily = null,
                ),
            )

            Controller()
        }
    },
    actions = {
        Options(
            modifier = Modifier
                .padding(dimensions.small.x)
                .height(dimensions.large.x),
        )
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorScheme.surfaceContainer,
        titleContentColor = colorScheme.onSurface
    )
)

@Composable
private fun TopLabel(
    text: String,
    visible: Boolean = true,
    onClose: () -> Unit = {},
    content: @Composable () -> Unit
) = Column {

    AnimatedVisibility(visible) {
        Box(
            modifier = Modifier
                .background(Color.Yellow)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                style = typography.labelMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = dimensions.small.s),
            )

            Icon(
                imageVector = Icons.Rounded.Close,
                tint = Color.Black,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(dimensions.nano.m)
                    .size(dimensions.large.m)
                    .clip(CircleShape)
                    .clickable(
                        indication = ripple(color = Color.Black),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClose
                    )
                    .padding(dimensions.nano.s)
            )
        }
    }

    content()
}
