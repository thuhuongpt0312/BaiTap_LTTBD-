package com.example.tuan_5

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.launch

val mauXanhApp = Color(0xFF3B5BDB)

// Màn hình đăng nhập: Google, Facebook, Email, Số điện thoại, Dùng thử (ẩn danh)
@Composable
fun ManHinhDangNhap(onThanhCong: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val callbackManager = remember { CallbackManager.Factory.create() }

    var tab by remember { mutableStateOf(0) }            // 0 = Email, 1 = Điện thoại
    var email by remember { mutableStateOf("") }
    var matKhau by remember { mutableStateOf("") }
    var soDT by remember { mutableStateOf("") }
    var maXacMinh by remember { mutableStateOf("") }
    var idXacMinh by remember { mutableStateOf("") }      // có giá trị khi đã gửi mã
    var dangXuLy by remember { mutableStateOf(false) }

    // Tạo hàm xử lý kết quả cho từng cách đăng nhập:
    // lỗi -> thông báo lỗi, thành công -> thông báo thành công rồi vào trang chủ
    fun taoKetQua(cach: String): (Boolean, String) -> Unit {
        return { thanhCong, thongBao ->
            dangXuLy = false
            if (thanhCong) {
                Toast.makeText(context, thongBao, Toast.LENGTH_SHORT).show()
                PhanTich.ghiDangNhap(context, cach)
                onThanhCong()
            } else {
                Toast.makeText(context, "Đăng nhập lỗi: " + thongBao, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Đăng nhập Facebook: dùng Facebook SDK rồi đổi token sang Firebase
    fun dangNhapFacebook() {
        try {
            val loginManager = LoginManager.getInstance()
            loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    DichVuXacThuc.dangNhapFacebook(result.accessToken, taoKetQua("facebook"))
                }

                override fun onCancel() {
                    dangXuLy = false
                    Toast.makeText(context, "Bạn đã hủy đăng nhập Facebook", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    dangXuLy = false
                    Toast.makeText(context, "Đăng nhập lỗi: " + error.message, Toast.LENGTH_LONG).show()
                }
            })
            loginManager.logInWithReadPermissions(
                activity as ActivityResultRegistryOwner,
                callbackManager,
                listOf("email", "public_profile")
            )
        } catch (e: Exception) {
            dangXuLy = false
            Toast.makeText(context, "Facebook chưa được cấu hình: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(mauXanhApp, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Chào mừng đến với\nUTH Todo", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (dangXuLy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Nút Google
            OutlinedButton(
                onClick = {
                    dangXuLy = true
                    scope.launch {
                        DichVuXacThuc.dangNhapGoogle(context, taoKetQua("google"))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("G   Tiếp tục với Google", color = Color.Black)
            }

            // Nút Facebook (Remote Config có thể bật/tắt nút này từ xa)
            if (CauHinhTuXa.hienFacebook) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        dangXuLy = true
                        dangNhapFacebook()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) {
                    Text("f   Tiếp tục với Facebook")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "hoặc",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Chọn Email hoặc Điện thoại
            TabRow(selectedTabIndex = tab) {
                Tab(selected = (tab == 0), onClick = { tab = 0 }, text = { Text("Email") })
                Tab(selected = (tab == 1), onClick = { tab = 1 }, text = { Text("Điện thoại") })
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (tab == 0) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = matKhau,
                    onValueChange = { matKhau = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            dangXuLy = true
                            DichVuXacThuc.dangNhapEmail(email, matKhau, taoKetQua("email"))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = mauXanhApp)
                    ) {
                        Text("Đăng nhập")
                    }
                    OutlinedButton(
                        onClick = {
                            dangXuLy = true
                            DichVuXacThuc.dangKyEmail(email, matKhau, taoKetQua("dang_ky_email"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Đăng ký")
                    }
                }
            } else {
                OutlinedTextField(
                    value = soDT,
                    onValueChange = { soDT = it },
                    label = { Text("Số điện thoại (VD: 0912345678)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (idXacMinh == "") {
                    // Bước 1: gửi mã
                    Button(
                        onClick = {
                            dangXuLy = true
                            DichVuXacThuc.guiMaSDT(
                                activity,
                                soDT,
                                onMaDaGui = { id ->
                                    idXacMinh = id
                                    dangXuLy = false
                                    Toast.makeText(context, "Đã gửi mã xác minh", Toast.LENGTH_SHORT).show()
                                },
                                ketQua = taoKetQua("phone")
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = mauXanhApp)
                    ) {
                        Text("Gửi mã xác minh")
                    }
                } else {
                    // Bước 2: nhập mã nhận được
                    OutlinedTextField(
                        value = maXacMinh,
                        onValueChange = { maXacMinh = it },
                        label = { Text("Mã xác minh (6 số)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            dangXuLy = true
                            DichVuXacThuc.xacNhanMaSDT(idXacMinh, maXacMinh, taoKetQua("phone"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = mauXanhApp)
                    ) {
                        Text("Xác nhận")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    dangXuLy = true
                    DichVuXacThuc.dangNhapAnDanh(taoKetQua("anonymous"))
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Dùng thử không cần tài khoản", color = mauXanhApp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}