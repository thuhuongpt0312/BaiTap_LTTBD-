package com.example.tuan_4.bt01

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Trang chủ sau khi bấm "Bắt đầu ngay": danh sách công việc (đối tượng CongViec)
@Composable
fun ManHinhTrangChu() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("Công việc hôm nay", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            for (cv in DuLieuTodo.dsCongViec) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(Color(0xFFECEFFB), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(cv.ten, fontWeight = FontWeight.Bold)
                        Text(cv.nhom, fontSize = 12.sp, color = mauXanhTodo)
                    }
                    Text(cv.hanChot, fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}