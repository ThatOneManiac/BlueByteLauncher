/*
 * BlueByte Launcher - A modern, lightweight Android Home Launcher.
 * Copyright (C) 2026 BlueByte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.bluebyte.launcher

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bluebyte.launcher.ui.components.DesktopGrid
import com.bluebyte.launcher.ui.components.SettingsDialog
import com.bluebyte.launcher.ui.components.StartMenu
import com.bluebyte.launcher.ui.components.Taskbar
import com.bluebyte.launcher.ui.theme.BlueByteLauncherTheme
import com.bluebyte.launcher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlueByteLauncherTheme {
                LauncherScreen()
            }
        }
    }
}

@Composable
fun LauncherScreen(viewModel: LauncherViewModel = viewModel()) {
    val context = LocalContext.current
    val apps by viewModel.appsList.collectAsState()
    val pinnedToTaskbar by viewModel.pinnedToTaskbar.collectAsState()
    val pinnedToDesktop by viewModel.pinnedToDesktop.collectAsState()

    // Handle Orientation Lock
    LaunchedEffect(viewModel.orientationMode) {
        val activity = context as? ComponentActivity
        activity?.requestedOrientation = when (viewModel.orientationMode) {
            "portrait" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "landscape" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    
    var isStartMenuOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadApps(context)
    }

    BackHandler(enabled = isStartMenuOpen || isSettingsOpen) {
        if (isStartMenuOpen) isStartMenuOpen = false
        else if (isSettingsOpen) isSettingsOpen = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color.Black, // Explicitly set to Black
        bottomBar = {
            Taskbar(
                apps = apps,
                pinnedToTaskbar = pinnedToTaskbar,
                onStartClick = {
                    viewModel.nextFortune()
                    isStartMenuOpen = !isStartMenuOpen
                },
                onSettingsClick = { isSettingsOpen = true },
                onAppClick = { viewModel.launchApp(context, it) },
                tileSize = viewModel.tileSize
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Desktop
            DesktopGrid(
                apps = apps,
                pinnedToTaskbar = pinnedToTaskbar,
                pinnedToDesktop = pinnedToDesktop,
                onAppClick = { packageName ->
                    viewModel.launchApp(context, packageName)
                },
                onPinTaskbar = { viewModel.togglePinToTaskbar(it) },
                onPinDesktop = { viewModel.togglePinToDesktop(it) },
                onAppSettings = { viewModel.openAppSettings(context, it) },
                tileSize = viewModel.tileSize,
                columns = viewModel.columns,
                backgroundColor = viewModel.backgroundColor,
                backgroundUri = viewModel.backgroundUri
            )

            // Start Menu Overlay with Fast Slide Animation (Left to Right)
            AnimatedVisibility(
                visible = isStartMenuOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(150)),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(150)),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                StartMenu(
                    apps = apps,
                    fortune = viewModel.currentFortune,
                    pinnedToTaskbar = pinnedToTaskbar,
                    pinnedToDesktop = pinnedToDesktop,
                    onAppClick = { packageName ->
                        viewModel.launchApp(context, packageName)
                        isStartMenuOpen = false
                    },
                    onPinTaskbar = { viewModel.togglePinToTaskbar(it) },
                    onPinDesktop = { viewModel.togglePinToDesktop(it) },
                    onAppSettings = { viewModel.openAppSettings(context, it) }
                )
            }

            // Settings Dialog
            if (isSettingsOpen) {
                SettingsDialog(
                    viewModel = viewModel,
                    onDismiss = { isSettingsOpen = false }
                )
            }
        }
    }
}
