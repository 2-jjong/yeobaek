package com.ssafy.yeobaeg

import android.app.Application
import com.ssafy.yeobaeg.data.datasource.remote.ChatWebSocketService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
class YeobaegApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(this, WebSocketEntryPoint::class.java)
        entryPoint.chatWebSocketService().initClient()
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WebSocketEntryPoint {
    fun chatWebSocketService(): ChatWebSocketService
}
