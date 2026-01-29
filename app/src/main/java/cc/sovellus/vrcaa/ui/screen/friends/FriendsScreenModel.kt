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

package cc.sovellus.vrcaa.ui.screen.friends

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.FriendManager
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel.FriendsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.icu.text.Transliterator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.FavoriteManager

class FriendsScreenModel : StateScreenModel<FriendsState>(FriendsState.Init) {

    sealed class FriendsState {
        data object Init : FriendsState()
        data object Loading : FriendsState()
        data object Result : FriendsState()
    }

    private var friendsStateFlow = MutableStateFlow(listOf<Friend>())
    private var friends = friendsStateFlow.asStateFlow()

    data class FriendsBuckets(
        val favoriteFriends: List<Friend> = emptyList(),
        val favoriteFriendsInInstances: List<Friend> = emptyList(),
        val favoriteFriendsOffline: List<Friend> = emptyList(),
        val friendsOnWebsite: List<Friend> = emptyList(),
        val friendsOnline: List<Friend> = emptyList(),
        val friendsInInstances: List<Friend> = emptyList(),
        val offlineFriends: List<Friend> = emptyList(),
    )

    private fun computeBuckets(all: List<Friend>): FriendsBuckets {
        val favorites = all.filter { FavoriteManager.isFavorite("friend", it.id) }
        val nonFavorites = all - favorites.toSet()

        return FriendsBuckets(
            favoriteFriends = favorites
                .filter { !it.location.contains("wrld_") && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            favoriteFriendsInInstances = favorites
                .filter { it.location.contains("wrld_") && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            favoriteFriendsOffline = favorites
                .filter { it.platform.isEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            friendsOnWebsite = nonFavorites
                .filter { it.platform == "web" }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            friendsOnline = nonFavorites
                .filter { !it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            friendsInInstances = nonFavorites
                .filter { it.location.contains("wrld_") && it.platform != "web" && it.platform.isNotEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) },

            offlineFriends = nonFavorites
                .filter { it.platform.isEmpty() }
                .sortedBy { StatusHelper.getStatusFromString(it.status) }
        )
    }

    @OptIn(FlowPreview::class)
    private val friendsBuckets = friends
        .debounce(150)
        .map { all ->
            withContext(Dispatchers.Default) {
                computeBuckets(all)
            }
        }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            FriendsBuckets()
        )

    val favoriteFriends get() = friendsBuckets.map { it.favoriteFriends }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val favoriteFriendsInInstances get() = friendsBuckets.map { it.favoriteFriendsInInstances }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val favoriteFriendsOffline get() = friendsBuckets.map { it.favoriteFriendsOffline }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val friendsOnWebsite get() = friendsBuckets.map { it.friendsOnWebsite }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val friendsOnline get() = friendsBuckets.map { it.friendsOnline }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val friendsInInstances get() = friendsBuckets.map { it.friendsInInstances }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())
    val offlineFriends get() = friendsBuckets.map { it.offlineFriends }.stateIn(screenModelScope, SharingStarted.Eagerly, listOf())

    var currentIndex = mutableIntStateOf(0)
    private var searchQueryFlow = MutableStateFlow("")
    var searchQuery = searchQueryFlow.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }
    
    data class GroupedFriend(
        val letter: String,
        val friend: Friend
    )
    
    data class FriendsGroup(
        val letter: String,
        val friends: List<Friend>
    )
    
    companion object {
        // 嘗試獲取中文拼音轉換器
        private val pinyinTransliterator = try {
            Transliterator.getInstance("Han-Latin/Names; Latin-ASCII; Upper")
        } catch (e: Exception) {
            null
        }
        
        // 嘗試獲取日文羅馬字轉換器（處理平假名和片假名）
        private val romajiTransliterator = try {
            Transliterator.getInstance("Hiragana-Latin; Katakana-Latin; Latin-ASCII; Upper")
        } catch (e: Exception) {
            null
        }
        
        // 統一的轉換器（優先使用日文轉換器，因為它也能處理中文）
        private val unifiedTransliterator = romajiTransliterator ?: pinyinTransliterator
    }
    
    /**
     * 獲取字符串的首字母分組
     * 規則：
     * 1. # - 數字和特殊字符（無法轉換的字符）
     * 2. A-Z - 英文字母、中文拼音首字母、日文羅馬字首字母
     */
    private fun getFirstLetter(name: String): String {
        if (name.isEmpty()) return "#"
        
        val firstChar = name.first()
        val charCode = firstChar.code
        
        // 英文字母 A-Z, a-z
        if (firstChar.isLetter() && charCode < 128) {
            return firstChar.uppercaseChar().toString()
        }
        
        // 數字或特殊字符（ASCII 範圍內）
        if (charCode < 128 && !firstChar.isLetter()) {
            return "#"
        }
        
        // 檢查是否為日文平假名（ひらがな）
        if (charCode in 0x3040..0x309F) {
            return getRomajiFirstLetter(firstChar.toString())
        }
        
        // 檢查是否為日文片假名（カタカナ）
        if (charCode in 0x30A0..0x30FF) {
            return getRomajiFirstLetter(firstChar.toString())
        }
        
        // 檢查是否為漢字（CJK統一漢字範圍，包括中文和日文漢字）
        if (charCode in 0x4E00..0x9FFF) {
            return getTransliteratedFirstLetter(firstChar.toString())
        }
        
        // 其他非英文字符，嘗試轉換
        return getTransliteratedFirstLetter(firstChar.toString())
    }
    
    /**
     * 獲取日文平假名/片假名的羅馬字首字母
     */
    private fun getRomajiFirstLetter(char: String): String {
        return unifiedTransliterator?.let { transliterator ->
            try {
                val transliterated = transliterator.transliterate(char)
                if (transliterated.isNotEmpty() && transliterated.first().isLetter()) {
                    transliterated.first().uppercaseChar().toString()
                } else {
                    "#"
                }
            } catch (e: Exception) {
                "#"
            }
        } ?: "#"
    }
    
    /**
     * 獲取漢字或其他字符的轉換後首字母（拼音或羅馬字）
     */
    private fun getTransliteratedFirstLetter(char: String): String {
        // 對於漢字，優先使用中文拼音轉換器
        val charCode = char.first().code
        val isCJK = charCode in 0x4E00..0x9FFF
        
        if (isCJK && pinyinTransliterator != null) {
            return try {
                val transliterated = pinyinTransliterator.transliterate(char)
                if (transliterated.isNotEmpty() && transliterated.first().isLetter()) {
                    transliterated.first().uppercaseChar().toString()
                } else {
                    // 如果中文轉換失敗，嘗試統一轉換器
                    tryUnifiedTransliterator(char)
                }
            } catch (e: Exception) {
                tryUnifiedTransliterator(char)
            }
        }
        
        // 其他情況使用統一轉換器
        return tryUnifiedTransliterator(char)
    }
    
    /**
     * 嘗試使用統一轉換器轉換字符
     */
    private fun tryUnifiedTransliterator(char: String): String {
        return unifiedTransliterator?.let { transliterator ->
            try {
                val transliterated = transliterator.transliterate(char)
                if (transliterated.isNotEmpty() && transliterated.first().isLetter()) {
                    transliterated.first().uppercaseChar().toString()
                } else {
                    "#"
                }
            } catch (e: Exception) {
                "#"
            }
        } ?: "#"
    }
    
    /**
     * 獲取用於排序的字符串（將中文轉換成拼音，日文轉換成羅馬字）
     */
    private fun getSortKey(name: String): String {
        if (name.isEmpty()) return name
        
        // 檢查是否包含漢字，優先使用中文拼音轉換器
        val hasCJK = name.any { it.code in 0x4E00..0x9FFF }
        
        if (hasCJK && pinyinTransliterator != null) {
            return try {
                pinyinTransliterator.transliterate(name).uppercase()
            } catch (e: Exception) {
                // 如果中文轉換失敗，嘗試統一轉換器
                unifiedTransliterator?.let { transliterator ->
                    try {
                        transliterator.transliterate(name).uppercase()
                    } catch (e: Exception) {
                        name.uppercase()
                    }
                } ?: name.uppercase()
            }
        }
        
        // 其他情況使用統一轉換器
        return unifiedTransliterator?.let { transliterator ->
            try {
                transliterator.transliterate(name).uppercase()
            } catch (e: Exception) {
                name.uppercase()
            }
        } ?: name.uppercase()
    }
    
    /**
     * 使用字母順序進行排序和分組（中文按拼音排序）
     */
    private fun filterAndGroupFriends(friends: List<Friend>, query: String): List<GroupedFriend> {
        if (friends.isEmpty()) return emptyList()
        
        val filtered = if (query.isBlank()) {
            friends
        } else {
            val lowerQuery = query.lowercase()
            friends.filter { 
                it.displayName.lowercase().contains(lowerQuery)
            }
        }
        
        if (filtered.isEmpty()) return emptyList()
        
        // 預計算首字母和排序鍵，避免重複計算
        val friendData = filtered.map { friend ->
            val name = friend.displayName
            Triple(friend, getFirstLetter(name), getSortKey(name))
        }
        
        // 先按首字母類型排序（A-Z 在前，# 在後），然後在每個組內按轉換後的排序鍵排序
        val sorted = friendData.sortedWith { a, b ->
            val (_, aLetter, aSortKey) = a
            val (_, bLetter, bSortKey) = b
            
            // 如果首字母類型不同
            if (aLetter != bLetter) {
                // # 組排在最後
                if (aLetter == "#") return@sortedWith 1
                if (bLetter == "#") return@sortedWith -1
                // A-Z 之間按字母順序
                aLetter.compareTo(bLetter)
            } else {
                // 同一組內按轉換後的排序鍵排序（中文會轉換成拼音）
                aSortKey.compareTo(bSortKey)
            }
        }
        
        return sorted.mapIndexed { index, (friend, firstLetter, _) ->
            val prevLetter = if (index > 0) {
                sorted[index - 1].second
            } else {
                ""
            }
            val showLetter = firstLetter != prevLetter
            GroupedFriend(
                letter = if (showLetter) firstLetter else "",
                friend = friend
            )
        }
    }
    
    /**
     * 將 GroupedFriend 列表轉換成 FriendsGroup 列表
     */
    private fun groupFriendsByLetter(groupedFriends: List<GroupedFriend>): List<FriendsGroup> {
        if (groupedFriends.isEmpty()) return emptyList()
        
        val groups = mutableListOf<FriendsGroup>()
        var currentLetter = ""
        var currentFriends = mutableListOf<Friend>()
        
        groupedFriends.forEach { groupedFriend ->
            if (groupedFriend.letter.isNotEmpty() && groupedFriend.letter != currentLetter) {
                // 保存前一個組
                if (currentFriends.isNotEmpty()) {
                    groups.add(FriendsGroup(currentLetter, currentFriends))
                }
                // 開始新組
                currentLetter = groupedFriend.letter
                currentFriends = mutableListOf(groupedFriend.friend)
            } else {
                // 添加到當前組
                currentFriends.add(groupedFriend.friend)
            }
        }
        
        // 保存最後一個組
        if (currentFriends.isNotEmpty()) {
            groups.add(FriendsGroup(currentLetter, currentFriends))
        }
        
        return groups
    }
    
    @OptIn(FlowPreview::class)
    val groupedFavoriteFriends = combine(
        friendsBuckets.map { it.favoriteFriends },
        friendsBuckets.map { it.favoriteFriendsInInstances },
        friendsBuckets.map { it.favoriteFriendsOffline },
        searchQueryFlow.debounce(200)
    ) { favorites, favoritesInInstances, favoritesOffline, query ->
        withContext(Dispatchers.Default) {
            val all = favorites + favoritesInInstances + favoritesOffline
            val grouped = filterAndGroupFriends(all, query)
            groupFriendsByLetter(grouped)
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), listOf())
    
    @OptIn(FlowPreview::class)
    val groupedFriends = combine(
        friendsBuckets.map { it.friendsOnline },
        friendsBuckets.map { it.friendsInInstances },
        searchQueryFlow.debounce(200)
    ) { online, inInstances, query ->
        withContext(Dispatchers.Default) {
            val all = inInstances + online
            val grouped = filterAndGroupFriends(all, query)
            groupFriendsByLetter(grouped)
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), listOf())
    
    @OptIn(FlowPreview::class)
    val groupedFriendsOnWebsite = combine(
        friendsBuckets.map { it.friendsOnWebsite },
        searchQueryFlow.debounce(200)
    ) { friends, query ->
        withContext(Dispatchers.Default) {
            val grouped = filterAndGroupFriends(friends, query)
            groupFriendsByLetter(grouped)
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), listOf())
    
    @OptIn(FlowPreview::class)
    val groupedOfflineFriends = combine(
        friendsBuckets.map { it.offlineFriends },
        searchQueryFlow.debounce(200)
    ) { friends, query ->
        withContext(Dispatchers.Default) {
            val grouped = filterAndGroupFriends(friends, query)
            groupFriendsByLetter(grouped)
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), listOf())
    
    /**
     * 合併所有朋友，收藏的在最上面
     */
    @OptIn(FlowPreview::class)
    val groupedAllFriends = combine(
        groupedFavoriteFriends,
        groupedFriends,
        groupedFriendsOnWebsite,
        groupedOfflineFriends,
        searchQueryFlow.debounce(200)
    ) { favorites, online, website, offline, _ ->
        withContext(Dispatchers.Default) {
            // 合併所有分組，收藏的在最前面
            val allGroups = favorites + online + website + offline
            
            // 合併相同字母的分組
            val mergedGroups = mutableMapOf<String, MutableList<Friend>>()
            allGroups.forEach { group ->
                mergedGroups.getOrPut(group.letter) { mutableListOf() }.addAll(group.friends)
            }
            
            // 轉換回 FriendsGroup 列表，保持字母順序
            mergedGroups.map { (letter, friends) ->
                FriendsGroup(letter, friends)
            }.sortedBy { it.letter }
        }
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    private val listener = object : FriendManager.FriendListener {
        override fun onUpdateFriends(friends: List<Friend>) {
            friendsStateFlow.update { friends }
        }
    }

    private val cacheListener = object : CacheManager.CacheListener {
        override fun startCacheRefresh() {
            mutableState.value = FriendsState.Loading
        }

        override fun endCacheRefresh() {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        }
    }

    init {
        mutableState.value = FriendsState.Loading
        FriendManager.addListener(listener)
        CacheManager.addListener(cacheListener)

        if (CacheManager.isBuilt()) {
            friendsStateFlow.value = FriendManager.getFriends().toMutableStateList()
            mutableState.value = FriendsState.Result
        } else {
            mutableState.value = FriendsState.Loading
        }
    }

    fun refreshCache() {
        screenModelScope.launch {
            CacheManager.buildCache()
        }
    }
}