package com.example.tuan_5

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

// Màn hình hồ sơ: thông tin tài khoản, thống kê, biểu đồ tuần, ảnh đại diện (Storage),
// mã FCM (Cloud Messaging), thử Crashlytics, đăng xuất
@Composable
fun ManHinhHoSo(onBack: () -> Unit, onDangXuat: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = DichVuXacThuc.nguoiDungHienTai()
    val ten = tenHienThi(user)

    val kho = remember { if (user != null) KhoCongViec(user.uid) else null }
    val danhSach = dungDanhSachCongViec(kho)

    var anh by remember { mutableStateOf<Uri?>(user?.photoUrl) }
    var maFcm by remember { mutableStateOf("") }

    // Lấy mã FCM để gửi thông báo thử từ Firebase console
    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                maFcm = t.result
            }
        }
    }

    // Chọn ảnh từ máy rồi tải lên Firebase Storage
    val chonAnh = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null && user != null) {
            Toast.makeText(context, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show()
            DichVuAnh.taiLen(user.uid, uri) { thanhCong, thongBao, link ->
                Toast.makeText(context, thongBao, Toast.LENGTH_LONG).show()
                if (thanhCong) {
                    anh = link
                    PhanTich.ghi(context, "doi_anh_dai_dien")
                }
            }
        }
    }

    // Thống kê
    val tong = danhSach.size
    var xong = 0
    for (cv in danhSach) {
        if (cv.daXong) {
            xong = xong + 1
        }
    }
    val dangCho = tong - xong
    var phanTram = 0
    if (tong > 0) {
        phanTram = xong * 100 / tong
    }

    // Biểu đồ tuần: đếm số việc xong theo từng thứ
    val nhanThu = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val maThu = listOf(2, 3, 4, 5, 6, 7, 1)      // Calendar.DAY_OF_WEEK tương ứng
    val demTheoThu = mutableListOf<Int>()
    for (ma in maThu) {
        var dem = 0
        for (cv in danhSach) {
            if (cv.daXong && cv.ngayXong == ma) {
                dem = dem + 1
            }
        }
        demTheoThu.add(dem)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thanh trên: < Cài đặt | Hồ sơ
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "<  Cài đặt",
                    color = mauXanhApp,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                )
                Text("Hồ sơ", fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Ảnh đại diện: có ảnh thì hiện ảnh, chưa có thì hiện chữ cái đầu
            if (anh != null) {
                AsyncImage(
                    model = anh,
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFE3E9FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(chuCaiDau(ten), fontSize = 26.sp, color = mauXanhApp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(ten, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(thongTinLienHe(user), fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                nhaCungCap(user),
                fontSize = 11.sp,
                color = Color(0xFF2E7D32),
                modifier = Modifier
                    .background(Color(0xFFDDF3DF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
            TextButton(onClick = {
                chonAnh.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Text("Đổi ảnh đại diện", fontSize = 12.sp)
            }

            // 3 ô thống kê
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in 0 until 3) {
                    var so = xong.toString()
                    var nhan = "Việc đã xong"
                    if (i == 1) {
                        so = dangCho.toString()
                        nhan = "Đang chờ"
                    } else if (i == 2) {
                        so = phanTram.toString() + "%"
                        nhan = "Hoàn thành"
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(so, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(nhan, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Biểu đồ cột "Tuần này"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tuần này", fontWeight = FontWeight.Medium)
                    Text("Việc xong mỗi ngày", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    for (i in 0 until 7) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height((4 + demTheoThu[i] * 20).dp)
                                    .background(mauXanhApp, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(nhanThu[i], fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Cloud Messaging: mã thiết bị để gửi thông báo thử
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text("Cloud Messaging", fontWeight = FontWeight.Medium)
                var hienMa = "Đang lấy mã..."
                if (maFcm != "") {
                    hienMa = maFcm.take(28) + "..."
                }
                Text(hienMa, fontSize = 11.sp, color = Color.Gray)
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("fcm", maFcm))
                    Toast.makeText(context, "Đã sao chép mã FCM", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Sao chép mã FCM", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Crashlytics: gây lỗi có chủ ý để thấy báo cáo trên Firebase console
            OutlinedButton(
                onClick = {
                    FirebaseCrashlytics.getInstance().log("Người dùng bấm nút thử Crashlytics")
                    throw RuntimeException("Test Crash")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gây lỗi thử (Crashlytics)", color = Color(0xFFC0392B))
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Đăng xuất
            Button(
                onClick = {
                    scope.launch {
                        DichVuXacThuc.dangXuat(context)
                        onDangXuat()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Đăng xuất", color = mauXanhApp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Email hoặc số điện thoại để hiện dưới tên
fun thongTinLienHe(user: FirebaseUser?): String {
    if (user == null) {
        return ""
    }
    val email = user.email
    if (email != null && email != "") {
        return email
    }
    val sdt = user.phoneNumber
    if (sdt != null && sdt != "") {
        return sdt
    }
    return "Tài khoản dùng thử"
}

// Cho biết tài khoản đăng nhập bằng cách nào
fun nhaCungCap(user: FirebaseUser?): String {
    if (user == null) {
        return ""
    }
    if (user.isAnonymous) {
        return "Tài khoản dùng thử (ẩn danh)"
    }
    var kq = ""
    for (p in user.providerData) {
        var tenCach = ""
        if (p.providerId == "google.com") {
            tenCach = "Đã liên kết tài khoản Google"
        } else if (p.providerId == "facebook.com") {
            tenCach = "Đã liên kết tài khoản Facebook"
        } else if (p.providerId == "password") {
            tenCach = "Đăng nhập bằng Email"
        } else if (p.providerId == "phone") {
            tenCach = "Đăng nhập bằng số điện thoại"
        }
        if (tenCach != "") {
            if (kq != "") {
                kq = kq + " · "
            }
            kq = kq + tenCach
        }
    }
    return kq
}