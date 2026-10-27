package com.example.tuan_4.bt02

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// BT2 - Hệ thống Quản lý Thư viện (có thanh điều hướng 3 tab)
@Composable
fun BT02App() {
    val thuVien = remember { ThuVien() }
    var hienTai by remember { mutableStateOf(thuVien.layDanhSachSinhVien()[0]) }
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = (tab == 0),
                    onClick = { tab = 0 },
                    icon = { Text("🏠") },
                    label = { Text("Quản lý") }
                )
                NavigationBarItem(
                    selected = (tab == 1),
                    onClick = { tab = 1 },
                    icon = { Text("📚") },
                    label = { Text("DS Sách") }
                )
                NavigationBarItem(
                    selected = (tab == 2),
                    onClick = { tab = 2 },
                    icon = { Text("👤") },
                    label = { Text("Sinh viên") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (tab == 0) {
                TabQuanLy(thuVien, hienTai, onDoiSinhVien = { hienTai = it })
            } else if (tab == 1) {
                TabDanhSachSach(thuVien)
            } else {
                TabSinhVien(thuVien)
            }
        }
    }
}

// Tab 1: chọn sinh viên, tích chọn để mượn / trả sách, thêm sách
@Composable
fun TabQuanLy(thuVien: ThuVien, hienTai: SinhVien, onDoiSinhVien: (SinhVien) -> Unit) {
    var tenNhap by remember { mutableStateOf(hienTai.ten) }
    var thongBao by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Hệ thống\nQuản lý Thư viện",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Sinh viên", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = tenNhap,
                onValueChange = { tenNhap = it },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onDoiSinhVien(thuVien.timHoacTaoSinhVien(tenNhap))
                    thongBao = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2653B5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Thay đổi")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Danh sách sách", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFFDDDDDD), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(thuVien.layDanhSachSach()) { sach ->
                    val nguoi = sach.nguoiMuon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tích = sinh viên hiện tại đang mượn cuốn này.
                        // Sách đã có người khác mượn thì không tích được.
                        Checkbox(
                            checked = (nguoi == hienTai),
                            enabled = (nguoi == null || nguoi == hienTai),
                            onCheckedChange = { chon ->
                                if (chon) {
                                    if (hienTai.muon(sach)) {
                                        thongBao = "Đã mượn " + sach.ten
                                    } else {
                                        thongBao = "Mỗi sinh viên chỉ được mượn tối đa 3 cuốn"
                                    }
                                } else {
                                    hienTai.tra(sach)
                                    thongBao = "Đã trả " + sach.ten
                                }
                            }
                        )
                        Text(sach.ten)
                        if (nguoi != null && nguoi != hienTai) {
                            Text("  (" + nguoi.ten + " đang mượn)", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { thuVien.themSach() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2653B5)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Thêm")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            thongBao,
            fontSize = 13.sp,
            color = Color(0xFFC0392B),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// Tab 2: thông tin từng cuốn sách (đa hình: mỗi loại sách in thông tin khác nhau)
@Composable
fun TabDanhSachSach(thuVien: ThuVien) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Danh sách sách", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(thuVien.layDanhSachSach()) { sach ->
                val nguoi = sach.nguoiMuon
                var trangThai = "Còn trong thư viện"
                var mau = Color(0xFF2E7D32)
                if (nguoi != null) {
                    trangThai = "Đang được " + nguoi.ten + " mượn"
                    mau = Color(0xFFC0392B)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(sach.thongTin(), fontWeight = FontWeight.Medium)
                    Text(trangThai, fontSize = 13.sp, color = mau)
                }
            }
        }
    }
}

// Tab 3: danh sách sinh viên và các sách đang mượn
@Composable
fun TabSinhVien(thuVien: ThuVien) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Danh sách sinh viên", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(thuVien.layDanhSachSinhVien()) { sv ->
                var dsMuon = ""
                for (sach in sv.dangMuon()) {
                    dsMuon = dsMuon + sach.ten + ", "
                }
                if (dsMuon == "") {
                    dsMuon = "(chưa mượn sách nào)"
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(sv.ten, fontWeight = FontWeight.Bold)
                    Text(sv.vaiTro() + " - " + sv.maSV, fontSize = 13.sp, color = Color.Gray)
                    Text("Đang mượn: " + dsMuon, fontSize = 13.sp)
                }
            }
        }
    }
}