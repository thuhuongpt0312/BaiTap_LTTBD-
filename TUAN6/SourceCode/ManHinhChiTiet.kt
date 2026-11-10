package com.example.tuan_6

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// FLOW CHI TIẾT (UI + API): xem, sửa, thêm/sửa/xóa việc nhỏ, đánh dấu hoàn thành, xóa công việc
@Composable
fun ManHinhChiTiet(vm: TaskViewModel, id: Int, onBack: () -> Unit) {
    // Mở màn hình thì gọi API chi tiết
    LaunchedEffect(id) {
        vm.taiChiTiet(id)
    }
    val cv = vm.layCongViec(id)

    var hienSua by remember { mutableStateOf(false) }
    var hienXoa by remember { mutableStateOf(false) }
    var viTriSua by remember { mutableStateOf(-1) }           // việc nhỏ đang sửa
    var viecNhoMoi by remember { mutableStateOf("") }

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->

        if (cv == null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Không tìm thấy công việc", color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = mauXanhTask)
                ) {
                    Text("Quay lại")
                }
            }
        } else {
            val dsViecNho = cv.subtasks ?: emptyList()
            var soXong = 0
            for (v in dsViecNho) {
                if (v.done) {
                    soXong = soXong + 1
                }
            }
            var tiLe = 0f
            if (dsViecNho.size > 0) {
                tiLe = soXong.toFloat() / dsViecNho.size
            }

            var nhacNho = "Tắt"
            if (cv.remind) {
                nhacNho = "Trước 15 phút"
            }
            var mauUuTien = Color(0xFF6B7280)
            if (cv.priority == "high") {
                mauUuTien = Color(0xFFC0392B)
            }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(mauNenTrang)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Thanh trên: quay lại, Sửa
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "‹  Quay lại",
                        color = mauXanhTask,
                        modifier = Modifier.clickable { onBack() }
                    )
                    Text(
                        "Sửa",
                        color = mauXanhTask,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { hienSua = true }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Nhóm và mức ưu tiên
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChipNhom(cv.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        tenUuTien(cv.priority),
                        fontSize = 11.sp,
                        color = mauUuTien,
                        modifier = Modifier
                            .background(Color(0xFFE9EAEE), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(cv.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Hạn chót và nhắc nhở
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
                        Text("📅  Hạn chót", color = Color.Gray)
                        Text(nhanNgay(cv.date, cv.time), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🔔  Nhắc nhở", color = Color.Gray)
                        Text(nhacNho, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Ghi chú
                Text("Ghi chú", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                var ghiChu = cv.note ?: ""
                if (ghiChu == "") {
                    ghiChu = "Chưa có ghi chú"
                }
                Text(
                    ghiChu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Việc nhỏ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Việc nhỏ", fontSize = 12.sp, color = Color.Gray)
                    Text(soXong.toString() + "/" + dsViecNho.size, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { tiLe },
                    modifier = Modifier.fillMaxWidth(),
                    color = mauXanhTask,
                    trackColor = Color(0xFFDDE2F5)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    for (i in dsViecNho.indices) {
                        val v = dsViecNho[i]
                        var kieuChu = TextDecoration.None
                        var mauChu = Color.Black
                        if (v.done) {
                            kieuChu = TextDecoration.LineThrough
                            mauChu = Color.Gray
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = v.done,
                                onCheckedChange = { vm.doiViecNho(id, i) }
                            )
                            Text(
                                v.title,
                                textDecoration = kieuChu,
                                color = mauChu,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "✏️",
                                modifier = Modifier
                                    .clickable { viTriSua = i }
                                    .padding(8.dp)
                            )
                            Text(
                                "🗑️",
                                modifier = Modifier
                                    .clickable { vm.xoaViecNho(id, i) }
                                    .padding(8.dp)
                            )
                        }
                    }

                    // Thêm việc nhỏ
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = viecNhoMoi,
                            onValueChange = { viecNhoMoi = it },
                            placeholder = { Text("+  Thêm việc nhỏ") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        TextButton(onClick = {
                            vm.themViecNho(id, viecNhoMoi)
                            viecNhoMoi = ""
                        }) {
                            Text("Thêm")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Đánh dấu hoàn thành
                var chuNut = "Đánh dấu hoàn thành"
                if (cv.done) {
                    chuNut = "Đánh dấu chưa xong"
                }
                Button(
                    onClick = { vm.doiTrangThai(id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mauXanhTask)
                ) {
                    Text(chuNut)
                }
                TextButton(
                    onClick = { hienXoa = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Xóa công việc", color = Color(0xFFC0392B))
                }
            }
        }
    }

    // Hộp thoại sửa tiêu đề và ghi chú
    if (cv != null && hienSua) {
        HopThoaiSuaCongViec(
            tieuDeCu = cv.title,
            ghiChuCu = cv.note ?: "",
            onHuy = { hienSua = false },
            onLuu = { tieuDe, ghiChu ->
                vm.suaCongViec(id, tieuDe, ghiChu)
                hienSua = false
            }
        )
    }

    // Hộp thoại sửa việc nhỏ
    if (cv != null && viTriSua >= 0) {
        val dsViecNho = cv.subtasks ?: emptyList()
        if (viTriSua < dsViecNho.size) {
            HopThoaiNhapChu(
                tieuDe = "Sửa việc nhỏ",
                giaTriCu = dsViecNho[viTriSua].title,
                onHuy = { viTriSua = -1 },
                onLuu = { tenMoi ->
                    vm.suaViecNho(id, viTriSua, tenMoi)
                    viTriSua = -1
                }
            )
        }
    }

    // Hộp thoại xác nhận xóa
    if (hienXoa) {
        AlertDialog(
            onDismissRequest = { hienXoa = false },
            title = { Text("Xóa công việc?") },
            text = { Text("Công việc này sẽ bị xóa.") },
            confirmButton = {
                TextButton(onClick = {
                    hienXoa = false
                    vm.xoaCongViec(id)
                    onBack()
                }) {
                    Text("Xóa", color = Color(0xFFC0392B))
                }
            },
            dismissButton = {
                TextButton(onClick = { hienXoa = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun HopThoaiSuaCongViec(
    tieuDeCu: String,
    ghiChuCu: String,
    onHuy: () -> Unit,
    onLuu: (String, String) -> Unit
) {
    var tieuDe by remember { mutableStateOf(tieuDeCu) }
    var ghiChu by remember { mutableStateOf(ghiChuCu) }

    AlertDialog(
        onDismissRequest = onHuy,
        title = { Text("Sửa công việc") },
        text = {
            Column {
                OutlinedTextField(
                    value = tieuDe,
                    onValueChange = { tieuDe = it },
                    label = { Text("Tiêu đề") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    label = { Text("Ghi chú") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onLuu(tieuDe, ghiChu) }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onHuy) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun HopThoaiNhapChu(tieuDe: String, giaTriCu: String, onHuy: () -> Unit, onLuu: (String) -> Unit) {
    var chu by remember { mutableStateOf(giaTriCu) }

    AlertDialog(
        onDismissRequest = onHuy,
        title = { Text(tieuDe) },
        text = {
            OutlinedTextField(
                value = chu,
                onValueChange = { chu = it },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onLuu(chu) }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onHuy) {
                Text("Hủy")
            }
        }
    )
}
