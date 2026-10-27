package com.example.tuan_4.bt03

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Màn hình 1 (cũng là nơi nhận lại dữ liệu từ màn hình 4) có 3 tham số tùy chọn
const val ROUTE_QUEN = "quen?email={email}&otp={otp}&pass={pass}"

// BT3 - SmartTasks: truyền dữ liệu giữa các màn hình (Data Flow Navigation)
//
//  Màn 1 --email--> Màn 2 --email,otp--> Màn 3 --email,otp,pass--> Màn 4
//    ^                                                               |
//    +------------------- email, otp, pass (Submit) -----------------+
//
// Uri.encode: mã hóa ký tự đặc biệt (như @, /, &) trước khi đưa vào đường dẫn
@Composable
fun BT03App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_QUEN) {

        // Màn hình 1: nhập email. Nếu nhận được dữ liệu từ màn 4 thì hiện trong khung xanh.
        composable(
            route = ROUTE_QUEN,
            arguments = listOf(
                navArgument("email") { defaultValue = "" },
                navArgument("otp") { defaultValue = "" },
                navArgument("pass") { defaultValue = "" }
            )
        ) { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            val otp = entry.arguments?.getString("otp") ?: ""
            val pass = entry.arguments?.getString("pass") ?: ""
            ManHinhQuenMatKhau(
                emailNhanVe = email,
                otpNhanVe = otp,
                passNhanVe = pass,
                onNext = { emailNhap ->
                    navController.navigate("xacthuc/" + Uri.encode(emailNhap))
                }
            )
        }

        // Màn hình 2: nhập mã OTP, nhận email từ màn 1
        composable("xacthuc/{email}") { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            ManHinhXacThuc(
                onBack = { navController.popBackStack() },
                onNext = { otp ->
                    navController.navigate("matkhau/" + Uri.encode(email) + "/" + Uri.encode(otp))
                }
            )
        }

        // Màn hình 3: tạo mật khẩu mới, nhận email và otp
        composable("matkhau/{email}/{otp}") { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            val otp = entry.arguments?.getString("otp") ?: ""
            ManHinhMatKhauMoi(
                onBack = { navController.popBackStack() },
                onNext = { pass ->
                    navController.navigate(
                        "xacnhan/" + Uri.encode(email) + "/" + Uri.encode(otp) + "/" + Uri.encode(pass)
                    )
                }
            )
        }

        // Màn hình 4: xác nhận, nhận cả email, otp, pass
        composable("xacnhan/{email}/{otp}/{pass}") { entry ->
            val email = entry.arguments?.getString("email") ?: ""
            val otp = entry.arguments?.getString("otp") ?: ""
            val pass = entry.arguments?.getString("pass") ?: ""
            ManHinhXacNhan(
                email = email,
                otp = otp,
                pass = pass,
                onBack = { navController.popBackStack() },
                onSubmit = {
                    // Quay về màn hình 1 và gửi kèm dữ liệu, xóa các màn hình ở giữa
                    navController.navigate(
                        "quen?email=" + Uri.encode(email) + "&otp=" + Uri.encode(otp) + "&pass=" + Uri.encode(pass)
                    ) {
                        popUpTo(ROUTE_QUEN) { inclusive = true }
                    }
                }
            )
        }
    }
}