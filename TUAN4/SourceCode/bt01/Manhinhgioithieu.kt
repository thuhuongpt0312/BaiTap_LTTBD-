package com.example.tuan_4.bt01

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Màn hình giới thiệu (bước 1, 2, 3). Nội dung lấy từ đối tượng BuocGioiThieu.
@Composable
fun ManHinhGioiThieu(buoc: Int, onTiepTuc: () -> Unit, onBoQua: () -> Unit) {
    val b = DuLieuTodo.dsBuoc[buoc]

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Đầu trang: logo + tên (trái), "Bỏ qua" (phải)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LogoTodo(28.dp, mauXanhTodo, Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UTH ", fontWeight = FontWeight.Bold)
                    Text("Todo", color = mauXanhTodo, fontWeight = FontWeight.Medium)
                }
                Text(
                    "Bỏ qua",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onBoQua() }
                )
            }

            // Hình minh họa ở giữa
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MinhHoa(buoc)
            }

            Text(b.tieuDe, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(b.moTa, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            // Chấm tròn báo đang ở bước nào
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 0 until DuLieuTodo.dsBuoc.size) {
                    if (i == buoc) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(6.dp)
                                .background(mauXanhTodo, RoundedCornerShape(3.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.LightGray, CircleShape)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTiepTuc,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mauXanhTodo)
            ) {
                Text(b.tenNut)
            }
        }
    }
}

// Hình minh họa đơn giản cho từng bước
@Composable
fun MinhHoa(buoc: Int) {
    Box(
        modifier = Modifier
            .size(width = 200.dp, height = 230.dp)
            .background(Color(0xFFECEFFB), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (buoc == 0) {
            // Bước 1: 3 dòng việc, dòng đầu đã tích
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (i in 0 until 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (i == 0) {
                            Box(modifier = Modifier.size(16.dp).background(mauXanhTodo, CircleShape))
                        } else {
                            Box(modifier = Modifier.size(16.dp).border(1.5.dp, Color.LightGray, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(6.dp)
                                .background(Color(0xFFDDE2F5), RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        } else if (buoc == 1) {
            // Bước 2: 2 thẻ nhóm "Học tập" và "Cuộc sống"
            val dsNhom = listOf("Học tập", "Cuộc sống")
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 0 until dsNhom.size) {
                    var mauChu = mauXanhTodo
                    var mauNenNhan = Color(0xFFE3E9FF)
                    if (i == 1) {
                        mauChu = Color(0xFFD9822B)
                        mauNenNhan = Color(0xFFFDEBD6)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            dsNhom[i],
                            fontSize = 11.sp,
                            color = mauChu,
                            modifier = Modifier
                                .background(mauNenNhan, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Color(0xFFDDE2F5), RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        } else {
            // Bước 3: chuông + dòng nhắc việc
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔔", fontSize = 28.sp)
                }
                Text(
                    "Ôn bài Toán lúc 19:00",
                    fontSize = 11.sp,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                )
            }
        }
    }
}