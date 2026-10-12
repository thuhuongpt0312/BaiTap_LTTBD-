package com.example.tuan_2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// BT01 - Thực hành 3: Máy tính đơn giản
@Composable
fun BT01Screen() {
    var soA by remember { mutableStateOf("") }
    var soB by remember { mutableStateOf("") }
    var ketQua by remember { mutableStateOf("") }

    fun tinh(phepToan: String) {
        val a = soA.toDoubleOrNull()
        val b = soB.toDoubleOrNull()

        if (a == null || b == null) {
            ketQua = "Vui lòng nhập số hợp lệ"
        } else if (phepToan == "/" && b == 0.0) {
            ketQua = "Không thể chia cho 0"
        } else {
            var kq = 0.0
            if (phepToan == "+") {
                kq = a + b
            } else if (phepToan == "-") {
                kq = a - b
            } else if (phepToan == "*") {
                kq = a * b
            } else {
                kq = a / b
            }

            if (kq % 1.0 == 0.0) {
                ketQua = kq.toLong().toString()
            } else {
                ketQua = kq.toString()
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Thực hành 03", modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = soA,
                onValueChange = { soA = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { tinh("+") },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD94848))
                ) { Text("+") }

                Button(
                    onClick = { tinh("-") },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDDA848))
                ) { Text("-") }

                Button(
                    onClick = { tinh("*") },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B3FDB))
                ) { Text("*") }

                Button(
                    onClick = { tinh("/") },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E))
                ) { Text("/") }
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = soB,
                onValueChange = { soB = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Kết quả: " + ketQua)
        }
    }
}
