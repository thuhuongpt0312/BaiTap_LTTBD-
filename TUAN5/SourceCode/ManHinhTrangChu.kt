package com.example.tuan_5

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Trang chủ: chào người dùng, tiến độ, 2 nhóm việc, danh sách việc hôm nay (Realtime Database)
@Composable
fun ManHinhTrangChu(onMoHoSo: () -> Unit) {
    val context = LocalContext.current
    val user = DichVuXacThuc.nguoiDungHienTai()
    val ten = tenHienThi(user)

    val kho = remember { if (user != null) KhoCongViec(user.uid) else null }
    val danhSach = dungDanhSachCongViec(kho)
    var hienHopThoai by remember { mutableStateOf(false) }

    // Xin quyền thông báo (Android 13+), đăng ký nhận thông báo theo chủ đề, gắn id cho Crashlytics
    val xinQuyen = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            xinQuyen.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        FirebaseMessaging.getInstance().subscribeToTopic("uth_todo")
        if (user != null) {
            FirebaseCrashlytics.getInstance().setUserId(user.uid)
        }
    }

    // Đếm số việc
    val tong = danhSach.size
    var xong = 0
    var conHoc = 0
    var conSong = 0
    for (cv in danhSach) {
        if (cv.daXong) {
            xong = xong + 1
        } else if (cv.nhom == "Học tập") {
            conHoc = conHoc + 1
        } else {
            conSong = conSong + 1
        }
    }
    var phanTram = 0f
    if (tong > 0) {
        phanTram = xong.toFloat() / tong
    }

    val ngayHomNay = SimpleDateFormat("EEEE, d 'tháng' M", Locale.forLanguageTag("vi-VN"))
        .format(Date())
        .replaceFirstChar { it.uppercase() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("🏠") },
                    label = { Text("Trang chủ") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { Toast.makeText(context, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show() },
                    icon = { Text("📋") },
                    label = { Text("Danh sách") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { Toast.makeText(context, "Bạn chưa có thông báo mới", Toast.LENGTH_SHORT).show() },
                    icon = { Text("🔔") },
                    label = { Text("Thông báo") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onMoHoSo() },
                    icon = { Text("⚙️") },
                    label = { Text("Cài đặt") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { hienHopThoai = true },
                containerColor = mauXanhApp,
                contentColor = Color.White
            ) {
                Text("+", fontSize = 26.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))
                .padding(horizontal = 20.dp)
        ) {
            // Đầu trang: ảnh đại diện (bấm vào để mở hồ sơ), ngày, lời chào, chuông
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE3E9FF), CircleShape)
                            .clickable { onMoHoSo() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(chuCaiDau(ten), color = mauXanhApp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ngayHomNay, fontSize = 12.sp, color = Color.Gray)
                        Text("Chào " + ten + "!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .clickable {
                                Toast.makeText(context, "Bạn chưa có thông báo mới", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔔")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Thẻ tiến độ (lời động viên lấy từ Remote Config)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(mauXanhApp, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tiến độ hôm nay", fontSize = 12.sp, color = Color.White)
                        Text(
                            xong.toString() + "/" + tong + " việc",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(CauHinhTuXa.loiDongVien, fontSize = 12.sp, color = Color.White)
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { phanTram },
                            modifier = Modifier.size(60.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            strokeWidth = 5.dp
                        )
                        Text(
                            (phanTram * 100).toInt().toString() + "%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2 nhóm việc
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("🎓")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Học tập", fontWeight = FontWeight.Bold)
                        Text(conHoc.toString() + " việc còn lại", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("🏠")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cuộc sống", fontWeight = FontWeight.Bold)
                        Text(conSong.toString() + " việc còn lại", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Việc hôm nay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Xem tất cả", fontSize = 12.sp, color = mauXanhApp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (danhSach.isEmpty()) {
                item {
                    Text(
                        "Chưa có việc nào. Bấm nút + để thêm.",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            items(danhSach) { cv ->
                var mauChu = mauXanhApp
                var mauNen = Color(0xFFE3E9FF)
                if (cv.nhom != "Học tập") {
                    mauChu = Color(0xFFD9822B)
                    mauNen = Color(0xFFFDEBD6)
                }
                var kieuChu = TextDecoration.None
                if (cv.daXong) {
                    kieuChu = TextDecoration.LineThrough
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = cv.daXong,
                        onCheckedChange = { kho?.doiTrangThai(cv) }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cv.ten, textDecoration = kieuChu, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                cv.nhom,
                                fontSize = 11.sp,
                                color = mauChu,
                                modifier = Modifier
                                    .background(mauNen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cv.gio, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Text(
                        "✕",
                        color = Color.Gray,
                        modifier = Modifier
                            .clickable { kho?.xoa(cv) }
                            .padding(12.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(96.dp)) }
        }
    }

    // Hộp thoại thêm việc mới
    if (hienHopThoai) {
        HopThoaiThemViec(
            onHuy = { hienHopThoai = false },
            onThem = { tenViec, nhom, gio ->
                kho?.them(tenViec, nhom, gio)
                PhanTich.ghi(context, "them_cong_viec")
                hienHopThoai = false
            }
        )
    }
}

@Composable
fun HopThoaiThemViec(onHuy: () -> Unit, onThem: (String, String, String) -> Unit) {
    var tenViec by remember { mutableStateOf("") }
    var nhom by remember { mutableStateOf("Học tập") }
    var gio by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onHuy,
        title = { Text("Thêm việc mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = tenViec,
                    onValueChange = { tenViec = it },
                    label = { Text("Tên việc") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (nhom == "Học tập"), onClick = { nhom = "Học tập" })
                    Text("Học tập")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = (nhom == "Cuộc sống"), onClick = { nhom = "Cuộc sống" })
                    Text("Cuộc sống")
                }
                OutlinedTextField(
                    value = gio,
                    onValueChange = { gio = it },
                    label = { Text("Giờ (VD: 14:00)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (tenViec.isNotBlank()) {
                    onThem(tenViec.trim(), nhom, gio.trim())
                }
            }) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onHuy) {
                Text("Hủy")
            }
        }
    )
}

// ----- Hàm dùng chung cho trang chủ và hồ sơ -----

// Tên hiển thị: tên tài khoản, hoặc phần trước @ của email, hoặc số điện thoại
fun tenHienThi(user: FirebaseUser?): String {
    if (user == null) {
        return "bạn"
    }
    val ten = user.displayName
    if (ten != null && ten != "") {
        return ten
    }
    val email = user.email
    if (email != null && email != "") {
        return email.substringBefore("@")
    }
    val sdt = user.phoneNumber
    if (sdt != null && sdt != "") {
        return sdt
    }
    return "Khách"
}

// Lấy 1-2 chữ cái đầu để làm ảnh đại diện, ví dụ "Minh Anh" -> "MA"
fun chuCaiDau(ten: String): String {
    val cacTu = ten.trim().split(" ")
    var kq = ""
    for (tu in cacTu) {
        if (tu != "" && kq.length < 2) {
            kq = kq + tu.substring(0, 1).uppercase()
        }
    }
    if (kq == "") {
        kq = "?"
    }
    return kq
}