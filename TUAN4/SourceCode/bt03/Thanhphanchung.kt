package com.example.tuan_4.bt03

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuan_4.R

val mauXanhTask = Color(0xFF4A90E2)
val mauNenO = Color(0xFFF1F3F8)

// Logo UTH và tên ứng dụng SmartTasks
@Composable
fun LogoSmartTasks() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.logo_full),
            contentDescription = "UTH Logo",
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("SmartTasks", fontSize = 22.sp, color = mauXanhTask, fontWeight = FontWeight.Medium)
    }
}

// Nút xanh bo tròn dùng cho Next / Submit
@Composable
fun NutXanh(chu: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(containerColor = mauXanhTask)
    ) {
        Text(chu)
    }
}

// Nút vuông xanh có dấu < ở góc trên bên trái
@Composable
fun NutQuayLai(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(mauXanhTask, RoundedCornerShape(12.dp))
            .clickable { onBack() },
        contentAlignment = Alignment.Center
    ) {
        Text("<", color = Color.White, fontSize = 22.sp)
    }
}
