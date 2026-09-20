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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bluebyte.launcher.model.AppInfo
import com.bluebyte.launcher.ui.theme.CyanAccent
import com.bluebyte.launcher.ui.theme.CyanTint
import com.bluebyte.launcher.ui.theme.TaskbarBlack

@Composable
fun Taskbar(
    apps: List<AppInfo>,
    pinnedToTaskbar: Set<String>,
    onStartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAppClick: (String) -> Unit,
    tileSize: Dp,
    modifier: Modifier = Modifier
) {
    val pinnedApps = remember(apps, pinnedToTaskbar) {
        apps.filter { pinnedToTaskbar.contains(it.packageName) }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(72.dp)
            .background(TaskbarBlack)
            .border(width = 1.dp, color = CyanAccent)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Start Button (Mascot) - Sized to match desktop app size
        Box(
            modifier = Modifier
                .size(tileSize + 12.dp)
                .clickable { onStartClick() }
                .border(1.dp, CyanAccent, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Mascot(modifier = Modifier.size(tileSize))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Pinned Apps
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pinnedApps.forEach { app ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onAppClick(app.packageName) }
                        .background(CyanTint, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    app.icon?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            colorFilter = ColorFilter.tint(CyanAccent.copy(alpha = 0.3f), androidx.compose.ui.graphics.BlendMode.SrcAtop)
                        )
                    }
                }
            }
        }

        // Gear Icon (Settings)
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
