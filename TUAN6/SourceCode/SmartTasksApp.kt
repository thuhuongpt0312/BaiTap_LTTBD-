package com.example.tuan_6

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Điều hướng của UTH SmartTasks: Trang chủ <-> Danh sách -> Chi tiết
@Composable
fun SmartTasksApp(vm: TaskViewModel = viewModel()) {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Hiện thông báo khi gọi API bị lỗi
    LaunchedEffect(vm.thongBao) {
        if (vm.thongBao != "") {
            Toast.makeText(context, vm.thongBao, Toast.LENGTH_SHORT).show()
            vm.xoaThongBao()
        }
    }

    // Thanh dưới: Trang chủ và Danh sách là màn hình thật, 2 mục còn lại chưa làm
    val diDen: (String) -> Unit = { dich ->
        if (dich == "trangchu" || dich == "danhsach") {
            navController.navigate(dich) {
                popUpTo("trangchu")
                launchSingleTop = true
            }
        } else {
            Toast.makeText(context, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    NavHost(navController = navController, startDestination = "trangchu") {

        composable("trangchu") {
            ManHinhTrangChu(
                vm = vm,
                onMoChiTiet = { id -> navController.navigate("chitiet/" + id) },
                onDi = diDen
            )
        }

        composable("danhsach") {
            ManHinhDanhSach(
                vm = vm,
                onMoChiTiet = { id -> navController.navigate("chitiet/" + id) },
                onDi = diDen
            )
        }

        composable("chitiet/{id}") { entry ->
            val id = (entry.arguments?.getString("id") ?: "0").toInt()
            ManHinhChiTiet(
                vm = vm,
                id = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
