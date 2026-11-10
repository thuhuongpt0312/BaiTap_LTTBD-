package com.example.tuan_6

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// FLOW TRANG CHỦ (UI + API): chào, tiến độ hôm nay, 2 nhóm việc, danh sách việc hôm nay
@Composable
fun ManHinhTrangChu(vm: TaskViewModel, onMoChiTiet: (Int) -> Unit, onDi: (String) -> Unit) {
    var hienHopThoai by remember { mutableStateOf(false) }

    // Thống kê tiến độ của các việc hôm nay
    val homNay = vm.tasks.filter { it.date == NGAY_HOM_NAY }
    var xong = 0
    for (cv in homNay) {
        if (cv.done) {
            xong = xong + 1
        }
    }
    val tong = homNay.size
    var phanTram = 0f
    if (tong > 0) {
        phanTram = xong.toFloat() / tong
    }

    var loiDongVien = "Bắt đầu thôi nào!"
    if (tong == 0) {
        loiDongVien = "Hôm nay chưa có việc nào"
    } else if (phanTram >= 1f) {
        loiDongVien = "Tuyệt vời, bạn đã xong hết!"
    } else if (phanTram >= 0.5f) {
        loiDongVien = "Đã hơn nửa chặng, cố lên!"
    }

    // Số việc còn lại của từng nhóm (tính trên tất cả các ngày)
    var conHoc = 0
    var conSong = 0
    for (cv in vm.tasks) {
        if (!cv.done) {
            if (cv.category == "study") {
                conHoc = conHoc + 1
            } else {
                conSong = conSong + 1
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { ThanhDieuHuong("trangchu", onDi) },
        floatingActionButton = { NutThem { hienHopThoai = true } },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->

        if (vm.dangTai && vm.tasks.isEmpty()) {
            // Đang gọi API
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (vm.loi != "" && vm.tasks.isEmpty()) {
            // API lỗi: báo lỗi và cho thử lại
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
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(mauNenTrang)
                    .padding(horizontal = 20.dp)
            ) {
                // Đầu trang
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE3E9FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("K", color = mauXanhTask, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ngayHomNayDayDu(), fontSize = 12.sp, color = Color.Gray)
                            Text("Chào bạn!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                                .clickable { onDi("thongbao") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔔")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Thẻ tiến độ hôm nay
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(mauXanhTask, RoundedCornerShape(20.dp))
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
                            Text(loiDongVien, fontSize = 12.sp, color = Color.White)
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
                                .clickable { onDi("danhsach") }
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
                                .clickable { onDi("danhsach") }
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
                        Text(
                            "Xem tất cả",
                            fontSize = 12.sp,
                            color = mauXanhTask,
                            modifier = Modifier.clickable { onDi("danhsach") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (homNay.isEmpty()) {
                    item {
                        Text(
                            "Chưa có việc nào. Bấm nút + để thêm.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }

                items(homNay, key = { it.id }) { cv ->
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
