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

package com.bluebyte.launcher.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluebyte.launcher.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel : ViewModel() {

    private val _appsList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appsList: StateFlow<List<AppInfo>> = _appsList.asStateFlow()

    private val _pinnedToTaskbar = MutableStateFlow<Set<String>>(emptySet())
    val pinnedToTaskbar = _pinnedToTaskbar.asStateFlow()

    private val _pinnedToDesktop = MutableStateFlow<Set<String>>(emptySet())
    val pinnedToDesktop = _pinnedToDesktop.asStateFlow()

    // Settings States
    var tileSize by mutableStateOf(65.dp)
    var columns by mutableStateOf(3)
    var backgroundColor by mutableStateOf(Color.Black)
    var backgroundUri by mutableStateOf<String?>(null)
    var orientationMode by mutableStateOf("auto") // "auto", "portrait", "landscape"

    private fun getPrefs(context: Context) = context.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    fun saveSettings(context: Context) {
        getPrefs(context).edit().apply {
            putFloat("tile_size", tileSize.value)
            putInt("columns", columns)
            putLong("bg_color", backgroundColor.value.toLong())
            putString("bg_uri", backgroundUri)
            putString("orientation", orientationMode)
            putStringSet("pinned_taskbar", _pinnedToTaskbar.value)
            putStringSet("pinned_desktop", _pinnedToDesktop.value)
            commit() // Use commit() for guaranteed persistence
        }
    }

    fun loadSettings(context: Context) {
        val prefs = getPrefs(context)
        tileSize = prefs.getFloat("tile_size", 65f).dp
        columns = prefs.getInt("columns", 3)
        val colorLong = prefs.getLong("bg_color", Color.Black.value.toLong())
        backgroundColor = Color(colorLong.toULong())
        backgroundUri = prefs.getString("bg_uri", null)
        orientationMode = prefs.getString("orientation", "auto") ?: "auto"
        _pinnedToTaskbar.value = prefs.getStringSet("pinned_taskbar", emptySet())?.toSet() ?: emptySet()
        _pinnedToDesktop.value = prefs.getStringSet("pinned_desktop", emptySet())?.toSet() ?: emptySet()
    }

    val fortunes = listOf(
        "\"Anakin is dead, I killed him.\" ― Darth Vader",
        "\"An eye for an eye makes the whole world blind.\" ― Mahatma Gandhi",
        "\"The only thing we have to fear is fear itself.\" ― Franklin D. Roosevelt",
        "\"I think, therefore I am.\" ― René Descartes",
        "\"To be or not to be, that is the question.\" ― William Shakespeare",
        "\"That's one small step for man, one giant leap for mankind.\" ― Neil Armstrong",
        "\"The journey of a thousand miles begins with one step.\" ― Lao Tzu",
        "\"... the educated person is not the person who can answer the questions, but the person who can question the answers.\" ― Theodore Schick Jr.",
        "\"A fanatic is a person who can't change his mind and won't change the subject.\" ― Winston Churchill",
        "\"Beware of the man who works hard to learn something, learns it, and finds himself no wiser than before.\" ― Kurt Vonnegut",
        "\"Contrariwise,\" continued Tweedledee, \"if it was so, it might be; and if it were so, it would be; but as it isn't, it ain't. That's logic!\" ― Lewis Carroll",
        "\"I have yet to see any problem, however complicated, which, when looked at in the right way, did not become still more complicated.\" ― Paul Anderson",
        "\"If you go on with this nuclear arms race, all you are going to do is make the rubble bounce.\" ― Winston Churchill",
        "\"Laughter is the closest distance between two people.\" ― Victor Borge",
        "\"Man invented language to satisfy his deep need to complain.\" ― Lily Tomlin",
        "\"The society which scorns excellence in plumbing as a humble activity and tolerates shoddiness in philosophy because it is an exaulted activity will have neither good plumbing nor good philosophy.\" ― John Gardner",
        "\"To YOU I'm an atheist; to God, I'm the Loyal Opposition.\" ― Woody Allen",
        "\"Under capitalism, man exploits man. Under Communism, it's just the opposite.\" ― John Kenneth Galbraith",
        "\"Where shall I begin, please your Majesty?\" he asked. \"Begin at the beginning,\" the King said, gravesly, \"and go on till you come to the end: then stop.\" ― Lewis Carroll",
        "\"A great many people think they are thinking when they are merely rearranging their prejudices.\" ― William James",
        "\"A person with one watch knows what time it is; a person with two watches is never sure.\" ― Proverb",
        "\"A programmer is a person who passes as an exacting expert on the basis of being able to turn out, after innumerable punching, an infinite series of incomprehensive answers...\" ― IEEE Grid",
        "\"One does not simply walk into Mordor.\" ― Boromir",
        "\"Shut up and take my money!\" ― Fry",
        "\"I had fun once. It was awful.\" ― Grumpy Cat",
        "\"Not sure if serious or just trolling.\" ― Fry",
        "\"But that's none of my business.\" ― Kermit",
        "\"Change my mind.\" ― Steven Crowder",
        "\"This is fine.\" ― Gunshow",
        "\"All your base are belong to us.\" ― Zero Wing",
        "\"It's a trap!\" ― Admiral Ackbar",
        "\"Ancient Aliens: I'm not saying it was aliens, but it was aliens.\"",
        "\"Brace yourselves, winter is coming.\" ― Ned Stark",
        "\"Distracted Boyfriend: [Object A] > [Object B].\"",
        "\"Mocking Spongebob: mOcKiNg sPoNgEbOb.\"",
        "\"Woman Yelling at a Cat.\"",
        "\"Success Kid: Went to bed early, woke up before my alarm.\"",
        "\"Hide the Pain Harold.\"",
        "\"Is this a pigeon?\"",
        "\"Modern problems require modern solutions.\" ― Dave Chappelle",
        "\"Am I a joke to you?\"",
        "\"Doge: Much wow. So fortune. Very text.\"",
        "\"Rickroll: Never gonna give you up.\"",
        "\"Roll Safe: You can't be late if you never show up.\"",
        "\"So anyway, I started blasting.\" ― Danny DeVito",
        "\"First time?\" ― James Franco",
        "\"Outstanding move.\"",
        "\"Wait, that's illegal.\"",
        "\"They're the same picture.\" ― Pam Beesly",
        "\"Sneak 100.\"",
        "\"Visible confusion.\"",
        "\"I see this as an absolute win!\" ― Hulk",
        "\"Aight, Imma head out.\" ― Spongebob",
        "\"Ok boomer.\"",
        "\"I am once again asking for your financial support.\" ― Bernie Sanders",
        "\"Always has been.\" ― Astronaut",
        "\"Think, Mark, think!\" ― Omni-Man",
        "\"Emotional Damage!\" ― Steven He",
        "\"Why are you running?\"",
        "\"Do you know the way?\" ― Ugandan Knuckles",
        "\"Netflix: Are you still watching? Someone's daughter: [Chaos].\"",
        "\"I'm about to end this man's whole career.\" ― Supa Hot Fire",
        "\"Checkmate, atheists.\"",
        "\"Loss: I II II L.\"",
        "\"Trolley Problem: Multi-track drifting!\"",
        "\"Pepe the Frog: Feels good man.\"",
        "\"Trollface: Problem?\"",
        "\"Rage Comics: FUUUUUUU—\"",
        "\"Leroy Jenkins: LEROYYYYYYY JENKINNNNNNS!\"",
        "\"Double Rainbow: What does it mean?!\"",
        "\"Chocolate Rain: Some stay dry and others feel the pain.\"",
        "\"What does the fox say?\"",
        "\"Harlem Shake.\"",
        "\"Planking.\"",
        "\"Tide Pods: Forbidden snacks.\"",
        "\"Storm Area 51: They can't stop all of us.\"",
        "\"Bernie Sanders in mittens.\"",
        "\"Sigma Grindset: Up at 3 AM to stare at a wall.\"",
        "\"Skibidi Toilet.\"",
        "\"Grimace Shake: Happy Birthday, Grimace!\"",
        "\"Barbenheimer.\"",
        "\"This could be us but you playin'.\"",
        "\"Nailed it.\"",
        "\"Expectation vs. Reality.\"",
        "\"Keep Calm and Carry On.\"",
        "\"YOLO: You Only Live Once.\"",
        "\"Weird flex but okay.\"",
        "\"Bye Felicia.\"",
        "\"Savage.\"",
        "\"I don't know who needs to hear this, but...\"",
        "\"Main Character Energy.\"",
        "\"Side-eye Chloe.\"",
        "\"Disaster Girl.\"",
        "\"Bad Luck Brian: Gets a fortune cookie, it's empty.\"",
        "\"Overly Attached Girlfriend.\"",
        "\"Scumbag Steve.\"",
        "\"Ermahgerd: Gersberms!\"",
        "\"Keyboard Cat: Play him off!\"",
        "\"Nyan Cat: Poptart cat in space.\"",
        "\"Ceiling Cat is watching you.\"",
        "\"I need dis.\"",
        "\"Arthur's Fist.\"",
        "\"Confused Nick Young.\"",
        "\"Let me in! LET ME INNNNN!\" ― Eric Andre",
        "\"He's literally me.\" ― Ryan Gosling",
        "\"Mr. Incredible becoming uncanny.\"",
        "\"Anakin and Padme: For the better, right?\"",
        "\"Doge vs. Cheems.\"",
        "\"Wide Putin walking.\"",
        "\"Coffin Dance.\"",
        "\"Gabagool? Over here!\" ― Tony Soprano",
        "\"It's Wednesday, my dudes.\"",
        "\"Look at all these chickens!\"",
        "\"Is this real life? Or is this just fantasy?\"",
        "\"I’m not a cat.\" ― Zoom lawyer",
        "\"You had one job.\"",
        "\"Thanks, Obama.\"",
        "\"Do a barrel roll!\"",
        "\"Harambe: Never forget.\"",
        "\"Press F to pay respects.\"",
        "\"No, this is Patrick!\" ― Spongebob",
        "\"Error 404: Fortune not found.\"",
        "\"... the educated person is not the person who can answer the questions, but the person who can question the answers.\" ― Theodore Schick Jr.",
        "\"A fanatic is a person who can't change his mind and won't change the subject.\" ― Winston Churchill",
        "\"Beware of the man who works hard to learn something, learns it, and finds himself no wiser than before.\" ― Kurt Vonnegut",
        "\"Contrariwise,\" continued Tweedledee, \"if it was so, it might be; and if it were so, it would be; but as it isn't, it ain't. That's logic!\" ― Lewis Carroll",
        "\"I have yet to see any problem, however complicated, which, when looked at in the right way, did not become still more complicated.\" ― Paul Anderson",
        "\"If you go on with this nuclear arms race, all you are going to do is make the rubble bounce.\" ― Winston Churchill",
        "\"Laughter is the closest distance between two people.\" ― Victor Borge",
        "\"Man invented language to satisfy his deep need to complain.\" ― Lily Tomlin",
        "\"The society which scorns excellence in plumbing as a humble activity and tolerates shoddiness in philosophy because it is an exaulted activity will have neither good plumbing nor good philosophy.\" ― John Gardner",
        "\"To YOU I'm an atheist; to God, I'm the Loyal Opposition.\" ― Woody Allen",
        "\"Under capitalism, man exploits man. Under Communism, it's just the opposite.\" ― John Kenneth Galbraith",
        "\"Where shall I begin, please your Majesty?\" he asked. \"Begin at the beginning,\" the King said, gravesly, \"and go on till you come to the end: then stop.\" ― Lewis Carroll",
        "\"A great many people think they are thinking when they are merely rearranging their prejudices.\" ― William James",
        "\"A person with one watch knows what time it is; a person with two watches is never sure.\" ― Proverb",
        "\"A programmer is a person who passes as an exacting expert on the basis of being able to turn out, after innumerable punching, an infinite series of incomprehensive answers...\" ― IEEE Grid"
    )
    var currentFortune by mutableStateOf(fortunes.random())

    fun nextFortune() {
        currentFortune = fortunes.random()
    }

    fun loadApps(context: Context) {
        nextFortune()
        loadSettings(context)
        viewModelScope.launch(Dispatchers.IO) {
            val packageManager = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            
            val apps = packageManager.queryIntentActivities(mainIntent, 0)
                .filter { it.activityInfo.packageName != context.packageName } // Don't show the launcher itself
                .map { resolveInfo ->
                    val iconDrawable = resolveInfo.loadIcon(packageManager)
                    AppInfo(
                        label = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = resolveInfo.activityInfo.packageName,
                        icon = iconDrawable.toBitmap().asImageBitmap()
                    )
                }
                .sortedBy { it.label.lowercase() }
            
            withContext(Dispatchers.Main) {
                _appsList.value = apps
            }
        }
    }

    fun togglePinToTaskbar(context: Context, packageName: String) {
        val current = _pinnedToTaskbar.value
        _pinnedToTaskbar.value = if (current.contains(packageName)) {
            current - packageName
        } else {
            current + packageName
        }
        saveSettings(context)
    }

    fun togglePinToDesktop(context: Context, packageName: String) {
        val current = _pinnedToDesktop.value
        _pinnedToDesktop.value = if (current.contains(packageName)) {
            current - packageName
        } else {
            current + packageName
        }
        saveSettings(context)
    }

    fun openAppSettings(context: Context, packageName: String) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun launchApp(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            context.startActivity(it)
        }
    }
}
