package com.example.tuan_5

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Cloud Messaging: nhận thông báo đẩy từ Firebase console.
// Khi app đang mở, thông báo đi qua onMessageReceived nên phải tự hiện ra.
// Khi app đang đóng, hệ thống tự hiện thông báo.
class ThongBaoService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM", "Mã FCM mới: " + token)
    }

    override fun onMessageReceived(tin: RemoteMessage) {
        val tieuDe = tin.notification?.title ?: "UTH Todo"
        val noiDung = tin.notification?.body ?: ""
        hienThongBao(tieuDe, noiDung)
    }

    private fun hienThongBao(tieuDe: String, noiDung: String) {
        // Android 13 trở lên cần được cấp quyền thông báo
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val kenh = "uth_todo"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(kenh, "Nhắc việc", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        val intent = Intent(this, MainActivity::class.java)
        val moApp = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val thongBao = NotificationCompat.Builder(this, kenh)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(tieuDe)
            .setContentText(noiDung)
            .setContentIntent(moApp)
            .setAutoCancel(true)
            .build()
        manager.notify(1, thongBao)
    }
}