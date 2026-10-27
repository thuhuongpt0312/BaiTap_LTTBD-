package com.example.tuan_4.bt01

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// BT1 - UTH Todo V2: Navigation + OOP
// Luồng: Splash -> Giới thiệu bước 1 -> bước 2 -> bước 3 -> Trang chủ
@Composable
fun BT01App() {
    val navController = rememberNavController()
    val soBuoc = DuLieuTodo.dsBuoc.size

    NavHost(navController = navController, startDestination = "splash") {

        // Màn hình Start: tự chuyển sang giới thiệu sau 2 giây
        composable("splash") {
            ManHinhSplash(onXong = {
                navController.navigate("gioithieu/0") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // Màn hình giới thiệu, nhận số thứ tự bước (0, 1, 2)
        composable("gioithieu/{buoc}") { entry ->
            val buoc = (entry.arguments?.getString("buoc") ?: "0").toInt()
            ManHinhGioiThieu(
                buoc = buoc,
                onTiepTuc = {
                    if (buoc < soBuoc - 1) {
                        navController.navigate("gioithieu/" + (buoc + 1))
                    } else {
                        vaoTrangChu(navController)
                    }
                },
                onBoQua = { vaoTrangChu(navController) }
            )
        }

        composable("trangchu") {
            ManHinhTrangChu()
        }
    }
}

// Đi tới trang chủ và xóa các màn hình trước đó khỏi lịch sử
fun vaoTrangChu(navController: NavController) {
    navController.navigate("trangchu") {
        popUpTo(navController.graph.id) { inclusive = true }
    }
}