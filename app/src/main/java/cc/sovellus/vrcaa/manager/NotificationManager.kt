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

package cc.sovellus.vrcaa.manager

import android.content.Intent
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationV2
import cc.sovellus.vrcaa.base.BaseManager

object NotificationManager : BaseManager<NotificationManager.NotificationListener>() {

    private const val ACTION_REFRESH_WIDGET = "cc.sovellus.vrcaa.REFRESH_NOTIFICATION_WIDGET"

    interface NotificationListener {
        fun onUpdateNotifications(notifications: List<Notification>) {}
        fun onUpdateNotificationsV2(notifications: List<NotificationV2>) {}
        fun onUpdateNotificationCount(count: Int) {}
    }

    private val notificationList = mutableListOf<Notification>()
    private val notificationV2List = mutableListOf<NotificationV2>()

    fun setNotifications(notifications: List<Notification>) {
        synchronized(notificationList) {
            notificationList.clear()
            notificationList.addAll(notifications)
        }
        notifyListeners()
        refreshWidget()
    }

    fun setNotificationsV2(notifications: List<NotificationV2>) {
        synchronized(notificationV2List) {
            notificationV2List.clear()
            notificationV2List.addAll(notifications)
        }
        notifyV2Listeners()
        refreshWidget()
    }

    fun addNotification(notification: Notification) {
        synchronized(notificationList) {
            notificationList.add(0, notification)
        }
        notifyListeners()
        refreshWidget()
    }

    fun addNotificationV2(notification: NotificationV2) {
        synchronized(notificationV2List) {
            notificationV2List.add(0, notification)
        }
        notifyV2Listeners()
        refreshWidget()
    }

    fun removeNotification(notificationId: String) {
        synchronized(notificationList) {
            notificationList.removeIf { it.id == notificationId }
        }
        notifyListeners()
        refreshWidget()
    }

    fun removeNotificationV2(notificationId: String) {
        synchronized(notificationV2List) {
            notificationV2List.removeIf { it.id == notificationId }
        }
        notifyV2Listeners()
        refreshWidget()
    }

    fun getNotifications(): List<Notification> {
        synchronized(notificationList) {
            return notificationList.toList()
        }
    }

    fun getNotificationsV2(): List<NotificationV2> {
        synchronized(notificationV2List) {
            return notificationV2List.toList()
        }
    }

    fun getTotalCount(): Int {
        return synchronized(notificationList) { notificationList.size } +
               synchronized(notificationV2List) { notificationV2List.size }
    }

    private fun notifyListeners() {
        val notifications = getNotifications()
        val count = getTotalCount()
        getListeners().forEach { listener ->
            listener.onUpdateNotifications(notifications)
            listener.onUpdateNotificationCount(count)
        }
    }

    private fun notifyV2Listeners() {
        val notifications = getNotificationsV2()
        val count = getTotalCount()
        getListeners().forEach { listener ->
            listener.onUpdateNotificationsV2(notifications)
            listener.onUpdateNotificationCount(count)
        }
    }

    private fun refreshWidget() {
        try {
            val context = App.getContext()
            val intent = Intent(ACTION_REFRESH_WIDGET).apply {
                setPackage(context.packageName)
            }
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            // Widget refresh failed, ignore
        }
    }
}