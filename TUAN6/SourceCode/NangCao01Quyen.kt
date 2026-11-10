package com.example.tuan_6

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

// NÂNG CAO 01: xử lý quyền (Location -> Notification -> Camera)
// Mỗi quyền là một thẻ hỏi. "Allow / Turn on" mở hộp thoại xin quyền thật của Android,
// "Skip for now" bỏ qua. Hỏi xong cả 3 thì hiện bảng kết quả.

class BuocQuyen(
    val bieuTuong: String,
    val tieuDe: String,
    val moTa: String,
    val nutChinh: String,
    val quyen: String
)

val mauCam = Color(0xFFE8743B)

@Composable
fun NangCao01Quyen() {
    val context = LocalContext.current
    var buoc by remember { mutableStateOf(0) }       // 0 = Location, 1 = Notification, 2 = Camera, 3 = xong

    val dsBuoc = remember {
        listOf(
            BuocQuyen(
                "📍", "Location",
                "Allow maps to access your location while you use the app?",
                "Allow", Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            BuocQuyen(
                "🔔", "Notification",
                "Please enable notifications to receive updates and reminders",
                "Turn on", Manifest.permission.POST_NOTIFICATIONS
            ),
            BuocQuyen(
                "📷", "Camera",
                "We need access to your camera to scan QR codes",
                "Turn on", Manifest.permission.CAMERA
            )
        )
    }

    // Kiểm tra quyền đã được cấp chưa (Android dưới 13 không cần xin quyền thông báo)
    fun daCap(quyen: String): Boolean {
        if (quyen == Manifest.permission.POST_NOTIFICATIONS && Build.VERSION.SDK_INT < 33) {
            return true
        }
        return ContextCompat.checkSelfPermission(context, quyen) == PackageManager.PERMISSION_GRANTED
    }

    // Hộp thoại xin quyền của hệ thống. Có kết quả thì chuyển sang thẻ tiếp theo.
    val xinQuyen = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { duocCap ->
        if (!duocCap) {
            Toast.makeText(context, "Bạn đã từ chối quyền này", Toast.LENGTH_SHORT).show()
        }
        buoc = buoc + 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A4A4A), Color(0xFF1C1C1C))))
            .navigationBarsPadding()
    ) {
        // Màn hình phía sau: Login và Continue without account
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Button(
                onClick = { Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mauCam)
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { Toast.makeText(context, "Continue without account", Toast.LENGTH_SHORT).show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continue without account", color = mauCam)
            }
        }

        // Lớp phủ tối + thẻ hỏi / bảng kết quả
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            if (buoc < dsBuoc.size) {
                val b = dsBuoc[buoc]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(Color(0xFFE6F6EE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(b.bieuTuong, fontSize = 44.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(b.tieuDe, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(b.moTa, fontSize = 14.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (daCap(b.quyen)) {
                                buoc = buoc + 1          // đã được cấp từ trước thì sang bước sau
                            } else {
                                xinQuyen.launch(b.quyen)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = mauCam)
                    ) {
                        Text(b.nutChinh)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { buoc = buoc + 1 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Skip for now", color = mauCam)
                    }
                }
            } else {
                // Bảng kết quả sau khi hỏi xong cả 3 quyền
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(28.dp))
                        .padding(24.dp)
                ) {
                    Text("Kết quả xin quyền", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    for (b in dsBuoc) {
                        var trangThai = "Chưa cấp"
                        var mau = Color(0xFFC0392B)
                        if (daCap(b.quyen)) {
                            trangThai = "Đã cấp"
                            mau = Color(0xFF2E7D32)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(b.bieuTuong + "  " + b.tieuDe)
                            Text(trangThai, color = mau, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Quyền bị từ chối nhiều lần thì Android không hiện hộp thoại nữa, hãy bật trong Cài đặt.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.fromParts("package", context.packageName, null)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = mauCam)
                    ) {
                        Text("Mở cài đặt ứng dụng")
                    }
                    OutlinedButton(
                        onClick = { buoc = 0 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Làm lại", color = mauCam)
                    }
                }
            }
        }
    }
}
