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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluebyte.launcher.model.AppInfo
import androidx.compose.foundation.border
import com.bluebyte.launcher.ui.theme.CyanAccent
import com.bluebyte.launcher.ui.theme.StartMenuBlack
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.graphics.ColorFilter
import com.bluebyte.launcher.ui.theme.CyanTint

@Composable
fun StartMenu(
    apps: List<AppInfo>,
    fortune: String,
    pinnedToTaskbar: Set<String>,
    pinnedToDesktop: Set<String>,
    onAppClick: (String) -> Unit,
    onPinTaskbar: (String) -> Unit,
    onPinDesktop: (String) -> Unit,
    onAppSettings: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isBlank()) apps
        else apps.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    // Auto-launch if exactly one app matches
    LaunchedEffect(filteredApps) {
        if (searchQuery.isNotBlank() && filteredApps.size == 1) {
            onAppClick(filteredApps[0].packageName)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(0.8f) // Cover most of the screen, leave space at right
            .fillMaxHeight() // Full height above taskbar
            .clip(RoundedCornerShape(topEnd = 16.dp))
            .background(StartMenuBlack)
            .border(width = 1.dp, color = CyanAccent, shape = RoundedCornerShape(topEnd = 16.dp))
            .padding(24.dp)
    ) {
        // Menu Label
        Text(
            text = "MENU",
            color = CyanAccent,
            fontSize = 18.sp, // Larger font
            style = androidx.compose.ui.text.TextStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Fortune above search bar
        Text(
            text = fortune,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp, // Readable font
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search apps...", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredApps) { app ->
                AppListItem(
                    app = app,
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
fun AppListItem(
    app: AppInfo,
    isPinnedToTaskbar: Boolean,
    isPinnedToDesktop: Boolean,
    onClick: () -> Unit,
    onPinTaskbar: () -> Unit,
    onPinDesktop: () -> Unit,
    onAppSettings: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp) // Larger icons
                .background(CyanTint, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            app.icon?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.7f),
                    colorFilter = ColorFilter.tint(CyanAccent.copy(alpha = 0.3f), androidx.compose.ui.graphics.BlendMode.SrcAtop)
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 18.sp, // Larger text
            modifier = Modifier.weight(1f)
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
