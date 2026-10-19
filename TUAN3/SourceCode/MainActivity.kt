package com.example.tuan_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UngDung()
            }
        }
    }
}

// Điều hướng giữa 3 loại màn hình: chào -> danh sách -> chi tiết
@Composable
fun UngDung() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "chao") {

        // Màn hình 1: chào
        composable("chao") {
            ManHinhChao(onSanSang = { navController.navigate("danhsach") })
        }

        // Màn hình 2: danh sách thành phần UI
        composable("danhsach") {
            ManHinhDanhSach(onChon = { ten -> navController.navigate("chitiet/" + ten) })
        }

        // Màn hình 3: chi tiết, nhận tên thành phần qua đường dẫn
        composable("chitiet/{ten}") { entry ->
            val ten = entry.arguments?.getString("ten") ?: ""
            ManHinhChiTiet(ten = ten, onBack = { navController.popBackStack() })
        }
    }
}