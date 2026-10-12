package com.example.tuan_2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// BT02 - Bài 1: Vòng lặp for và kiểu nullable

class Student(
    val name: String,
    val email: String?,
    val phone: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BT02Screen() {
    val danhSach = listOf(
        Student("Nguyễn Văn An", "an@sv.edu.vn", "0901 234 567"),
        Student("Trần Thị Bình", null, "0912 888 999"),
        Student("Lê Minh Chi", "chi@sv.edu.vn", null),
        Student("Phạm Quốc Dũng", null, null),
        Student("Võ Thanh Em", "em@sv.edu.vn", "0933 111 222")
    )

    var thieuEmail = 0
    for (sv in danhSach) {
        if (sv.email == null) {
            thieuEmail = thieuEmail + 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách sinh viên", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2C4170))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF3F4F8))
                .padding(16.dp)
        ) {
            for (sv in danhSach) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(sv.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        // 4. Nếu null thì hiện "Chưa cập nhật" (dùng ?: giống ?? của Dart)
                        Row {
                            Text("Email: ", fontSize = 13.sp)
                            if (sv.email == null) {
                                Text(
                                    sv.email ?: "Chưa cập nhật",
                                    fontSize = 13.sp,
                                    color = Color.Red,
                                    fontStyle = FontStyle.Italic
                                )
                            } else {
                                Text(sv.email, fontSize = 13.sp)
                            }
                        }
                        Row {
                            Text("SĐT: ", fontSize = 13.sp)
                            if (sv.phone == null) {
                                Text(
                                    sv.phone ?: "Chưa cập nhật",
                                    fontSize = 13.sp,
                                    color = Color.Red,
                                    fontStyle = FontStyle.Italic
                                )
                            } else {
                                Text(sv.phone, fontSize = 13.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Tổng kết cuối danh sách
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE6EBF5))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng: " + danhSach.size + " sinh viên")
                    Text("Thiếu email: " + thieuEmail, color = Color.Red)
                }
            }
        }
    }
}