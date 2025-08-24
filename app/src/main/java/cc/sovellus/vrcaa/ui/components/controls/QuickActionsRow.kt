/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.components.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.ui.screen.avatars.AvatarsScreen
import cc.sovellus.vrcaa.ui.screen.emojis.EmojisScreen
import cc.sovellus.vrcaa.ui.screen.gallery.GalleryScreen
import cc.sovellus.vrcaa.ui.screen.gallery.IconGalleryScreen
import cc.sovellus.vrcaa.ui.screen.group.UserGroupsScreen
import cc.sovellus.vrcaa.ui.screen.items.ItemsScreen
import cc.sovellus.vrcaa.ui.screen.prints.PrintsScreen
import cc.sovellus.vrcaa.ui.screen.stickers.StickersScreen
import cc.sovellus.vrcaa.ui.screen.worlds.WorldsScreen

@Composable
fun QuickActionsRow(
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.currentOrThrow

    val me = CacheManager.getProfile()
    val actions = buildList {
        // Prioritize most used on profile
        add(Triple(Icons.Filled.Person, "Avatars") { navigator.push(AvatarsScreen()) })
        if (me != null) {
            add(Triple(Icons.Filled.Public, "Worlds") { navigator.push(WorldsScreen(me.displayName, me.id, private = false)) })
        }
        add(Triple(Icons.Filled.Collections, "Gallery") { navigator.push(GalleryScreen()) })
        add(Triple(Icons.Filled.Face, "User Icons") { navigator.push(IconGalleryScreen()) })
        add(Triple(Icons.Filled.EmojiEmotions, "Emojis") { navigator.push(EmojisScreen()) })
        add(Triple(Icons.AutoMirrored.Filled.Label, "Stickers") { navigator.push(StickersScreen()) })
        if (me != null) {
            add(Triple(Icons.Filled.Print, "Prints") { navigator.push(PrintsScreen(me.id)) })
        }
        add(Triple(Icons.Filled.Inventory, "Items") { navigator.push(ItemsScreen()) })
    }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(actions) { (icon, label, onClick) ->
            AssistChip(
                onClick = onClick,
                leadingIcon = { Icon(icon, contentDescription = null) },
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            )
        }
    }
}


