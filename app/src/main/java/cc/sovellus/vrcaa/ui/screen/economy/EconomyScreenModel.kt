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

package cc.sovellus.vrcaa.ui.screen.economy

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.api.vrchat.http.models.UserBalance
import cc.sovellus.vrcaa.api.vrchat.http.models.UserSubscription
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import kotlinx.coroutines.launch

class EconomyScreenModel : StateScreenModel<EconomyScreenModel.EconomyState>(EconomyState.Init) {

    sealed class EconomyState {
        data object Init : EconomyState()
        data object Loading : EconomyState()
        data object Error : EconomyState()
        data class Result(
            val balance: UserBalance?,
            val subscriptions: List<UserSubscription>
        ) : EconomyState()
    }

    private val context: Context = App.getContext()

    init {
        fetchEconomyData()
    }

    fun fetchEconomyData() {
        mutableState.value = EconomyState.Loading
        
        screenModelScope.launch {
            try {
                val currentUser = CacheManager.getProfile()
                val balance = currentUser?.id?.let { api.economy.fetchUserBalance(it) }
                val subscriptions = api.economy.fetchCurrentSubscriptions()
                
                mutableState.value = EconomyState.Result(
                    balance = balance,
                    subscriptions = subscriptions
                )
            } catch (e: Exception) {
                mutableState.value = EconomyState.Error
            }
        }
    }

    fun refresh() {
        fetchEconomyData()
    }
}

