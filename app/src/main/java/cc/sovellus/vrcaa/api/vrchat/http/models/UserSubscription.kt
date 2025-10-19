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

package cc.sovellus.vrcaa.api.vrchat.http.models

import com.google.gson.annotations.SerializedName

data class UserSubscription(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("transactionId")
    var transactionId: String = "",
    @SerializedName("store")
    var store: String = "",
    @SerializedName("steamItemId")
    var steamItemId: String = "",
    @SerializedName("amount")
    var amount: Int = 0,
    @SerializedName("description")
    var description: String = "",
    @SerializedName("period")
    var period: String = "",
    @SerializedName("tier")
    var tier: Int = 0,
    @SerializedName("active")
    var active: Boolean = false,
    @SerializedName("status")
    var status: String = "",
    @SerializedName("starts")
    var starts: String = "",
    @SerializedName("expires")
    var expires: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("licenseGroups")
    var licenseGroups: List<String> = listOf(),
    @SerializedName("isGift")
    var isGift: Boolean = false,
    @SerializedName("isBulkGift")
    var isBulkGift: Boolean = false
)

