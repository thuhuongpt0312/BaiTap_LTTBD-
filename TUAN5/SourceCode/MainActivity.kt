package com.example.tuan_5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CauHinhTuXa.tai()          // lấy cấu hình từ Remote Config
        setContent {
            MaterialTheme {
                UngDung()
            }
        }
    }
}

// Luồng: Đăng nhập -> Trang chủ -> Hồ sơ (thông tin cá nhân)
@Composable
fun UngDung() {
    val navController = rememberNavController()

    // Đã đăng nhập từ lần trước thì vào thẳng trang chủ
    val diemBatDau = remember {
        if (DichVuXacThuc.nguoiDungHienTai() != null) "trangchu" else "dangnhap"
    }

    NavHost(navController = navController, startDestination = diemBatDau) {

        composable("dangnhap") {
            ManHinhDangNhap(onThanhCong = {
                navController.navigate("trangchu") {
                    popUpTo("dangnhap") { inclusive = true }
                }
            })
        }

        composable("trangchu") {
            ManHinhTrangChu(onMoHoSo = { navController.navigate("hoso") })
        }

        composable("hoso") {
            ManHinhHoSo(
                onBack = { navController.popBackStack() },
                onDangXuat = {
                    navController.navigate("dangnhap") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}