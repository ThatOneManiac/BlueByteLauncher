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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bluebyte.launcher.viewmodel.LauncherViewModel

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import com.bluebyte.launcher.ui.theme.CyanAccent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun SettingsDialog(
    viewModel: LauncherViewModel,
    onDismiss: () -> Unit
) {
    val wallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.backgroundUri = it.toString() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .border(width = 2.dp, color = CyanAccent),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Settings", fontSize = 24.sp, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = CyanAccent)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text(text = "Appearance", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(text = "Tile Size: ${viewModel.tileSize.value.toInt()}dp", color = Color.White)
                        Slider(
                            value = viewModel.tileSize.value,
                            onValueChange = { viewModel.tileSize = it.dp },
                            valueRange = 32f..80f,
                            colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                        )

                        Text(text = "Columns: ${viewModel.columns}", color = Color.White)
                        Slider(
                            value = viewModel.columns.toFloat(),
                            onValueChange = { viewModel.columns = it.toInt() },
                            valueRange = 3f..7f,
                            steps = 4,
                            colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Orientation Lock", color = Color.White, style = MaterialTheme.typography.labelLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("auto", "portrait", "landscape").forEach { mode ->
                                FilterChip(
                                    selected = viewModel.orientationMode == mode,
                                    onClick = { viewModel.orientationMode = mode },
                                    label = { Text(mode.replaceFirstChar { it.uppercase() }) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanAccent,
                                        selectedLabelColor = Color.Black,
                                        labelColor = Color.White,
                                        containerColor = Color.Transparent
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = viewModel.orientationMode == mode,
                                        borderColor = CyanAccent,
                                        selectedBorderColor = CyanAccent
                                    )
                                )
                            }
                        }
                    }

                    item {
                        HorizontalDivider(color = CyanAccent.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Background", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Button(
                            onClick = { wallpaperLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Text("Choose Wallpaper", color = Color.Black)
                        }
                        Button(
                            onClick = { viewModel.backgroundColor = Color.Black; viewModel.backgroundUri = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            modifier = Modifier.border(1.dp, CyanAccent, RoundedCornerShape(20.dp))
                        ) {
                            Text("Reset to Default", color = CyanAccent)
                        }
                    }

                    item {
                        HorizontalDivider(color = CyanAccent.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "About", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text(text = "BlueByte Launcher", fontSize = 18.sp, color = CyanAccent)
                        Text(text = "Version 1.0.0", color = Color.Gray)
                        Text(text = "Licensed under GNU GPLv2", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "A modern, bloat-free launcher for Android, inspired by simplicity and customization. Swipe Right to open Menu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
