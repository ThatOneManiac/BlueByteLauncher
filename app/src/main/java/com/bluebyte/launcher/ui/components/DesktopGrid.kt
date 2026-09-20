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

package com.bluebyte.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bluebyte.launcher.model.AppInfo
import com.bluebyte.launcher.ui.theme.CyanAccent
import com.bluebyte.launcher.ui.theme.CyanTint
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*

@Composable
fun DesktopGrid(
    apps: List<AppInfo>,
    pinnedToTaskbar: Set<String>,
    pinnedToDesktop: Set<String>,
    onAppClick: (String) -> Unit,
    onPinTaskbar: (String) -> Unit,
    onPinDesktop: (String) -> Unit,
    onAppSettings: (String) -> Unit,
    tileSize: Dp,
    columns: Int,
    backgroundColor: Color,
    backgroundUri: String?,
    modifier: Modifier = Modifier
) {
    val desktopApps = remember(apps, pinnedToDesktop) {
        if (pinnedToDesktop.isEmpty()) apps.take(12)
        else apps.filter { pinnedToDesktop.contains(it.packageName) }
    }

    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {
        if (backgroundUri != null) {
            AsyncImage(
                model = backgroundUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(desktopApps) { app ->
                AppTile(
                    app = app,
                    tileSize = tileSize,
                    isPinnedToTaskbar = pinnedToTaskbar.contains(app.packageName),
                    isPinnedToDesktop = pinnedToDesktop.contains(app.packageName),
                    onClick = { onAppClick(app.packageName) },
                    onPinTaskbar = { onPinTaskbar(app.packageName) },
                    onPinDesktop = { onPinDesktop(app.packageName) },
                    onAppSettings = { onAppSettings(app.packageName) }
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AppTile(
    app: AppInfo,
    tileSize: Dp,
    isPinnedToTaskbar: Boolean,
    isPinnedToDesktop: Boolean,
    onClick: () -> Unit,
    onPinTaskbar: () -> Unit,
    onPinDesktop: () -> Unit,
    onAppSettings: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(tileSize + 24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(tileSize)
                .background(CyanTint, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            app.icon?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.8f),
                    colorFilter = ColorFilter.tint(CyanAccent.copy(alpha = 0.3f), androidx.compose.ui.graphics.BlendMode.SrcAtop)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (isPinnedToTaskbar) "Unpin from Taskbar" else "Pin to Taskbar") },
                onClick = { onPinTaskbar(); showMenu = false }
            )
            DropdownMenuItem(
                text = { Text(if (isPinnedToDesktop) "Unpin from Screen" else "Pin to Screen") },
                onClick = { onPinDesktop(); showMenu = false }
            )
            DropdownMenuItem(
                text = { Text("App Settings") },
                onClick = { onAppSettings(); showMenu = false }
            )
        }
    }
}
