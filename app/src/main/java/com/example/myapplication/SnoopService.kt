package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class SnoopService: Service(), ISensorStorage {
    private val NOTIFICATION_ID = 293815484
    private var channelId: Int? = null
    private lateinit var appName: String

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var acc = ""

    override fun set() {

    }

    override fun get(): Int {
        return 4
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        appName = getString(R.string.app_name)
        // establish notification in top bar
        val notificationBuilder = NotificationCompat.Builder(this, appName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setOngoing(true)
            .setContentTitle("Snooper")
            .setContentText("I'm doing some snooping!")
            .setChannelId(getNotificationChannelId())
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        // init sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(object: SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                event?.apply {
                    acc = "${values[0]},${values[1]},${values[2]}"
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        var n = 0
        ticker(100.milliseconds).onEach {
            updateUser("Run ${n++} times. Current acceleration -> $acc")
        }.launchIn(CoroutineScope(Dispatchers.IO))

        return START_STICKY
    }

    private fun listSensors(): List<String> {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getSensorList(Sensor.TYPE_ALL).map { it.name }
    }

    private fun updateUser(msg: String) {
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, NotificationCompat.Builder(this, appName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setOngoing(true)
            .setContentTitle("Snooper")
            .setChannelId(getNotificationChannelId())
            .setContentText(msg).build())
    }

    private fun ticker(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun getNotificationChannelId(): String {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = "snooper_channel"

        if (channelId == null) {
            val name = "snooper notification channel"
            val description = ""

            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setShowBadge(false)

            notificationManager.createNotificationChannel(channel)
        }

        return id
    }

//    private fun takeAcceloro

//    private fun takeScreenshot() {
//        MediaProjection.createScreenCaptureIntent()
//    }

//    private fun takeScreenshot() {
//        val now = Date()
//        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
//
//        // image naming and path  to include sd card  appending name you choose for file
//        val mPath: String =
//            Environment.getExternalStorageDirectory().toString() + "/capture/" + now + ".jpg"
//
//        // create bitmap screen capture
//        val bitmap: Bitmap
//        val v1: View = chatHead.getRootView()
//        v1.setDrawingCacheEnabled(true)
//        bitmap = Bitmap.createBitmap(v1.getDrawingCache())
//        v1.setDrawingCacheEnabled(false)
//        var fout: OutputStream? = null
//        val imageFile = File(mPath)
//        try {
//            fout = FileOutputStream(imageFile)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout)
//            fout.flush()
//            fout.close()
//        } catch (e: FileNotFoundException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        } catch (e: IOException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        }
//    }
}