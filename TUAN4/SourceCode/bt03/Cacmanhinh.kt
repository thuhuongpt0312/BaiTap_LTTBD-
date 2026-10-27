package com.example.tuan_4.bt03

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Màn hình 1: Forget Password ----------
@Composable
fun ManHinhQuenMatKhau(
    emailNhanVe: String,
    otpNhanVe: String,
    passNhanVe: String,
    onNext: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var loi by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            LogoSmartTasks()
            Spacer(modifier = Modifier.height(32.dp))
            Text("Forget Password?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enter your Email, we will send you a verification code.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Your Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            if (loi != "") {
                Text(loi, color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            NutXanh("Next") {
                if (email.contains("@")) {
                    loi = ""
                    onNext(email)
                } else {
                    loi = "Email không hợp lệ"
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Khung hiện dữ liệu nhận lại từ màn hình 4 (chỉ hiện khi đã có dữ liệu)
            if (emailNhanVe != "" || otpNhanVe != "" || passNhanVe != "") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD6E6FA), RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Dữ liệu từ màn hình 4", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("email: " + emailNhanVe, fontSize = 16.sp, color = Color(0xFFC0392B))
                    Text("otp: " + otpNhanVe, fontSize = 16.sp, color = Color(0xFFC0392B))
                    Text("pass: " + passNhanVe, fontSize = 16.sp, color = Color(0xFFC0392B))
                }
            }
        }
    }
}

// ---------- Màn hình 2: Verify Code (5 ô nhập mã) ----------
@Composable
fun ManHinhXacThuc(onBack: () -> Unit, onNext: (String) -> Unit) {
    val otp = remember { mutableStateListOf("", "", "", "", "") }
    val oFocus = remember { List(5) { FocusRequester() } }
    var loi by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                NutQuayLai(onBack)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LogoSmartTasks()
            Spacer(modifier = Modifier.height(32.dp))
            Text("Verify Code", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enter the code\nwe just sent you on your registered Email",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until 5) {
                    BasicTextField(
                        value = otp[i],
                        onValueChange = { v ->
                            // Chỉ nhận 1 chữ số, nhập xong tự nhảy sang ô kế tiếp
                            if (v.length <= 1 && (v == "" || v[0].isDigit())) {
                                otp[i] = v
                                if (v != "" && i < 4) {
                                    oFocus[i + 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .focusRequester(oFocus[i]),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        decorationBox = { oNhap ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(mauNenO, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                oNhap()
                            }
                        }
                    )
                }
            }
            if (loi != "") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(loi, color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            NutXanh("Next") {
                val ma = otp.joinToString("")
                if (ma.length == 5) {
                    loi = ""
                    onNext(ma)
                } else {
                    loi = "Vui lòng nhập đủ 5 số"
                }
            }
        }
    }
}

// ---------- Màn hình 3: Create new password ----------
@Composable
fun ManHinhMatKhauMoi(onBack: () -> Unit, onNext: (String) -> Unit) {
    var matKhau by remember { mutableStateOf("") }
    var nhapLai by remember { mutableStateOf("") }
    var loi by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                NutQuayLai(onBack)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LogoSmartTasks()
            Spacer(modifier = Modifier.height(32.dp))
            Text("Create new password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your new password must be different form\npreviously used password",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = matKhau,
                onValueChange = { matKhau = it },
                placeholder = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = nhapLai,
                onValueChange = { nhapLai = it },
                placeholder = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            if (loi != "") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(loi, color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            NutXanh("Next") {
                if (matKhau.length < 6) {
                    loi = "Mật khẩu phải có ít nhất 6 ký tự"
                } else if (matKhau != nhapLai) {
                    loi = "Mật khẩu nhập lại không khớp"
                } else {
                    loi = ""
                    onNext(matKhau)
                }
            }
        }
    }
}

// ---------- Màn hình 4: Confirm ----------
@Composable
fun ManHinhXacNhan(email: String, otp: String, pass: String, onBack: () -> Unit, onSubmit: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                NutQuayLai(onBack)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LogoSmartTasks()
            Spacer(modifier = Modifier.height(32.dp))
            Text("Confirm", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("We are here to help you!", fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(20.dp))

            // Hiện dữ liệu đã nhận từ 3 màn hình trước (mật khẩu hiện dạng dấu *)
            val dsGiaTri = listOf(email, otp, "*".repeat(pass.length))
            for (giaTri in dsGiaTri) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(mauNenO, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(giaTri, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            NutXanh("Summit") {
                onSubmit()
            }
        }
    }
}