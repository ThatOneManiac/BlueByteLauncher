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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    val fortunes = listOf(
        "\"Anakin is dead, I killed him.\" ― Darth Vader",
        "\"An eye for an eye makes the whole world blind.\" ― Mahatma Gandhi",
        "\"Frankly, my dear, I don't give a damn.\" ― Gone with the Wind",
        "\"I'm going to make him an offer he can't refuse.\" ― The Godfather",
        "\"Toto, I've a feeling we're not in Kansas anymore.\" ― The Wizard of Oz",
        "\"Here's looking at you, kid.\" ― Casablanca",
        "\"Go ahead, make my day.\" ― Sudden Impact",
        "\"May the Force be with you.\" ― Star Wars",
        "\"Fasten your seatbelts. It's going to be a bumpy night.\" ― All About Eve",
        "\"You talkin' to me?\" ― Taxi Driver",
        "\"What we've got here is failure to communicate.\" ― Cool Hand Luke",
        "\"I love the smell of napalm in the morning.\" ― Apocalypse Now",
        "\"E.T. phone home.\" ― E.T. the Extra-Terrestrial",
        "\"Bond. James Bond.\" ― Dr. No",
        "\"There's no place like home.\" ― The Wizard of Oz",
        "\"Show me the money!\" ― Jerry Maguire",
        "\"You can't handle the truth!\" ― A Few Good Men",
        "\"I'll be back.\" ― The Terminator",
        "\"If you build it, he will come.\" ― Field of Dreams",
        "\"Mama always said life was like a box of chocolates.\" ― Forrest Gump",
        "\"We rob banks.\" ― Bonnie and Clyde",
        "\"I see dead people.\" ― The Sixth Sense",
        "\"Houston, we have a problem.\" ― Apollo 13",
        "\"Keep your friends close, but your enemies closer.\" ― The Godfather Part II",
        "\"Say 'hello' to my little friend!\" ― Scarface",
        "\"Elementary, my dear Watson.\" ― Sherlock Holmes",
        "\"Get your stinking paws off me, you damned dirty ape.\" ― Planet of the Apes",
        "\"Here's Johnny!\" ― The Shining",
        "\"They're here!\" ― Poltergeist",
        "\"Hasta la vista, baby.\" ― Terminator 2",
        "\"My precious.\" ― The Two Towers",
        "\"Wax on, wax off.\" ― The Karate Kid",
        "\"You're gonna need a bigger boat.\" ― Jaws",
        "\"To infinity and beyond!\" ― Toy Story",
        "\"I'll have what she's having.\" ― When Harry Met Sally...",
        "\"Why so serious?\" ― The Dark Knight",
        "\"Just keep swimming.\" ― Finding Nemo",
        "\"This is Sparta!\" ― 300",
        "\"I am your father.\" ― The Empire Strikes Back",
        "\"Roads? Where we're going we don't need roads.\" ― Back to the Future",
        "\"Hello. My name is Inigo Montoya. You killed my father. Prepare to die.\" ― The Princess Bride",
        "\"I'm the king of the world!\" ― Titanic",
        "\"Keep the change, ya filthy animal.\" ― Home Alone",
        "\"Good morning, Vietnam!\" ― Good Morning, Vietnam",
        "\"Chewie, we're home.\" ― The Force Awakens",
        "\"Great Scott!\" ― Back to the Future",
        "\"There's no crying in baseball!\" ― A League of Their Own",
        "\"Life moves pretty fast. If you don't stop and look around once in a while, you could miss it.\" ― Ferris Bueller",
        "\"Stay gold, Ponyboy.\" ― The Outsiders",
        "\"I drink your milkshake!\" ― There Will Be Blood",
        "\"Do, or do not. There is no try.\" ― The Empire Strikes Back",
        "\"Look at me. I'm the captain now.\" ― Captain Phillips",
        "\"Are you not entertained?\" ― Gladiator",
        "\"It's alive! It's alive!\" ― Frankenstein",
        "\"I'm as mad as hell, and I'm not going to take this anymore!\" ― Network",
        "\"Help me, Obi-Wan Kenobi. You're my only hope.\" ― Star Wars",
        "\"Snap out of it!\" ― Moonstruck",
        "\"You had me at 'hello'.\" ― Jerry Maguire",
        "\"Badges? We ain't got no badges! We don't need no badges!\" ― The Treasure of the Sierra Madre",
        "\"Greed, for lack of a better word, is good.\" ― Wall Street",
        "\"Round up the usual suspects.\" ― Casablanca",
        "\"I'm walkin' here! I'm walkin' here!\" ― Midnight Cowboy",
        "\"Louis, I think this is the beginning of a beautiful friendship.\" ― Casablanca",
        "\"A census taker once tried to test me. I ate his liver with some fava beans and a nice Chianti.\" ― The Silence of the Lambs",
        "\"Magic Mirror on the wall, who is the fairest one of all?\" ― Snow White",
        "\"You've got to ask yourself one question: 'Do I feel lucky?' Well, do ya, punk?\" ― Dirty Harry",
        "\"Listen to them. Children of the night. What music they make.\" ― Dracula",
        "\"Oh, Jerry, don't let's ask for the moon. We have the stars.\" ― Now, Voyager",
        "\"Twas beauty killed the beast.\" ― King Kong",
        "\"Forget it, Jake, it's Chinatown.\" ― Chinatown",
        "\"I have always depended on the kindness of strangers.\" ― A Streetcar Named Desire",
        "\"Big things have small beginnings.\" ― Prometheus",
        "\"The first rule of Fight Club is: You do not talk about Fight Club.\" ― Fight Club",
        "\"Get out!\" ― Get Out",
        "\"Dread it. Run from it. Destiny arrives all the same.\" ― Avengers: Infinity War",
        "\"Avengers... assemble.\" ― Avengers: Endgame",
        "\"That'll do, pig. That'll do.\" ― Babe",
        "\"I am Iron Man.\" ― Iron Man",
        "\"Wilsooooon!\" ― Cast Away",
        "\"Inconceivable!\" ― The Princess Bride",
        "\"Wait a minute, wait a minute. You ain't heard nothin' yet!\" ― The Jazz Singer",
        "\"A boy's best friend is his mother.\" ― Psycho",
        "\"Gentlemen, you can't fight in here! This is the War Room!\" ― Dr. Strangelove",
        "\"Open the pod bay doors, HAL.\" ― 2001: A Space Odyssey",
        "\"Attica! Attica!\" ― Dog Day Afternoon",
        "\"We'll always have Paris.\" ― Casablanca",
        "\"You complete me.\" ― Jerry Maguire",
        "\"Rosebud.\" ― Citizen Kane",
        "\"Nobody puts Baby in a corner.\" ― Dirty Dancing",
        "\"What a dump.\" ― Beyond the Forest",
        "\"I'm not bad. I'm just drawn that way.\" ― Who Framed Roger Rabbit",
        "\"Shall we play a game?\" ― WarGames",
        "\"Life is a banquet, and most poor suckers are starving to death!\" ― Auntie Mame",
        "\"Game over, man! Game over!\" ― Aliens",
        "\"Whatever you do, don't fall asleep.\" ― A Nightmare on Elm Street",
        "\"I'm having a friend for dinner.\" ― The Silence of the Lambs",
        "\"They call me Mister Tibbs!\" ― In the Heat of the Night",
        "\"Stella! Hey, Stella!\" ― A Streetcar Named Desire",
        "\"Shane. Shane. Come back!\" ― Shane",
        "\"Well, nobody's perfect.\" ― Some Like It Hot",
        "\"Made it, Ma! Top of the world!\" ― White Heat",
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
        viewModelScope.launch {
            val packageManager = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            
            val apps = packageManager.queryIntentActivities(mainIntent, 0)
                .map { resolveInfo ->
                    val iconDrawable = resolveInfo.loadIcon(packageManager)
                    AppInfo(
                        label = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = resolveInfo.activityInfo.packageName,
                        icon = iconDrawable.toBitmap().asImageBitmap()
                    )
                }
                .sortedBy { it.label.lowercase() }
            
            _appsList.value = apps
        }
    }

    fun togglePinToTaskbar(packageName: String) {
        _pinnedToTaskbar.value = if (_pinnedToTaskbar.value.contains(packageName)) {
            _pinnedToTaskbar.value - packageName
        } else {
            _pinnedToTaskbar.value + packageName
        }
    }

    fun togglePinToDesktop(packageName: String) {
        _pinnedToDesktop.value = if (_pinnedToDesktop.value.contains(packageName)) {
            _pinnedToDesktop.value - packageName
        } else {
            _pinnedToDesktop.value + packageName
        }
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
