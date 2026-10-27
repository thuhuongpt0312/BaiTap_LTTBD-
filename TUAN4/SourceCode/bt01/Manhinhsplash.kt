package com.example.tuan_4.bt01

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Màn hình Start: nền xanh, logo, tên app, khẩu hiệu, thanh tiến trình, phiên bản
@Composable
fun ManHinhSplash(onXong: () -> Unit) {
    val tt = DuLieuTodo.thongTin

    // Đợi 2 giây rồi chuyển màn hình
    LaunchedEffect(Unit) {
        delay(2000)
        onXong()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mauXanhTodo)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoTodo(104.dp, Color.White, mauXanhTodo)
            Spacer(modifier = Modifier.height(16.dp))
            Text(tt.ten, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(tt.khauHieu, fontSize = 13.sp, color = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier.width(110.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(tt.phienBan, fontSize = 11.sp, color = Color.White)
        }
    }
}

// Logo: ô vuông bo góc, bên trong có 2 dòng (hình tròn + thanh ngang)
@Composable
fun LogoTodo(kichThuoc: Dp, mauNen: Color, mauNet: Color) {
    Box(
        modifier = Modifier
            .size(kichThuoc)
            .background(mauNen, RoundedCornerShape(kichThuoc * 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(kichThuoc * 0.1f)) {
            for (i in 1..2) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(kichThuoc * 0.08f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(kichThuoc * 0.18f)
                            .background(mauNet, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .width(kichThuoc * 0.32f)
                            .height(kichThuoc * 0.07f)
                            .background(mauNet, RoundedCornerShape(50))
                    )
                }
            }
        }
    }
}