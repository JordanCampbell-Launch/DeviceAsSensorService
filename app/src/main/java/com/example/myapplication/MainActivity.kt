package com.example.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class MainActivity : AppCompatActivity(), ServiceConnection {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceIntent = Intent(this, SnoopService::class.java)
//        serviceIntent.putExtra(EXTRA_NOTIFICATION_SET, notificationSet)
        startForegroundService(serviceIntent)
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        // this will not be true if the notification service is forced into a separate process, this will be a BinderProxy type
        (binder as ISensorStorage).apply {
            Log.i("FOO", "Retrieved ${get()}")
        }
//        if (binder is NotificationForegroundService.LocalBinder) {
//            binder.service.let { notificationService ->
//                Logger.d(OceanLogTags.NOTIFICATIONS, "Application has connected with client-side notification service")
//
//                notificationForegroundService = notificationService
//                notificationService.connectApp(this, loginManager)
//                onSyncControllerAvailable(notificationService)
//
//                webSocketConnectionSubscription.set(notificationService.connectionState.subscribe(this::onSyncConnectionChanged) {})
//            }
//        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }
}