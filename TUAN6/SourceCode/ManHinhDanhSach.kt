package com.example.tuan_6

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// FLOW DANH SÁCH (UI + API): tìm kiếm, lọc theo nhóm, chia "Cần làm" và "Đã xong"
@Composable
fun ManHinhDanhSach(vm: TaskViewModel, onMoChiTiet: (Int) -> Unit, onDi: (String) -> Unit) {
    var tuKhoa by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("all") }       // all, study, life
    var hienHopThoai by remember { mutableStateOf(false) }

    // Lọc theo nhóm và từ khóa
    val ketQua = vm.tasks.filter {
        (loc == "all" || it.category == loc) && it.title.contains(tuKhoa, ignoreCase = true)
    }
    val canLam = ketQua.filter { !it.done }.sortedBy { it.date + it.time }
    val daXong = ketQua.filter { it.done }.sortedBy { it.date + it.time }

    var chuaXong = 0
    for (cv in vm.tasks) {
        if (!cv.done) {
            chuaXong = chuaXong + 1
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { ThanhDieuHuong("danhsach", onDi) },
        floatingActionButton = { NutThem { hienHopThoai = true } },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->

        if (vm.dangTai && vm.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (vm.loi != "" && vm.tasks.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(vm.loi, color = Color(0xFFC0392B))
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { vm.taiDanhSach() },
                    colors = ButtonDefaults.buttonColors(containerColor = mauXanhTask)
                ) {
                    Text("Thử lại")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(mauNenTrang)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("Danh sách", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(chuaXong.toString() + " việc chưa xong", fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tuKhoa,
                    onValueChange = { tuKhoa = it },
                    placeholder = { Text("🔍  Tìm công việc") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = (loc == "all"),
                        onClick = { loc = "all" },
                        label = { Text("Tất cả") }
                    )
                    FilterChip(
                        selected = (loc == "study"),
                        onClick = { loc = "study" },
                        label = { Text("Học tập") }
                    )
                    FilterChip(
                        selected = (loc == "life"),
                        onClick = { loc = "life" },
                        label = { Text("Cuộc sống") }
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            "CẦN LÀM",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    if (canLam.isEmpty()) {
                        item { Text("Không có việc nào", color = Color.Gray, fontSize = 13.sp) }
                    }
                    items(canLam, key = { it.id }) { cv ->
                        HangCongViec(
                            cv = cv,
                            hienNgay = true,
                            onTick = { vm.doiTrangThai(cv.id) },
                            onMo = { onMoChiTiet(cv.id) }
                        )
                    }

                    item {
                        Text(
                            "ĐÃ XONG",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                    }
                    if (daXong.isEmpty()) {
                        item { Text("Chưa có việc nào xong", color = Color.Gray, fontSize = 13.sp) }
                    }
                    items(daXong, key = { it.id }) { cv ->
                        HangCongViec(
                            cv = cv,
                            hienNgay = false,
                            onTick = { vm.doiTrangThai(cv.id) },
                            onMo = { onMoChiTiet(cv.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(96.dp)) }
                }
            }
        }
    }

    if (hienHopThoai) {
        HopThoaiThemCongViec(
            onHuy = { hienHopThoai = false },
            onThem = { ten, nhom ->
                vm.themCongViec(ten, nhom)
                hienHopThoai = false
            }
        )
    }
}
