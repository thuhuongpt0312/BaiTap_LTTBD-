package com.example.tuan_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tuan_4.bt01.BT01App
import com.example.tuan_4.bt02.BT02App
import com.example.tuan_4.bt03.BT03App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    // 1 = BT01 (UTH Todo), 2 = BT02 (Thư viện), 3 = BT03 (SmartTasks - có logo_full)
    var baiTapHienTai by remember { mutableStateOf(3) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = baiTapHienTai - 1,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Tab(
                selected = baiTapHienTai == 1,
                onClick = { baiTapHienTai = 1 },
                text = { Text("BT 1 (Todo)") }
            )
            Tab(
                selected = baiTapHienTai == 2,
                onClick = { baiTapHienTai = 2 },
                text = { Text("BT 2 (Thư viện)") }
            )
            Tab(
                selected = baiTapHienTai == 3,
                onClick = { baiTapHienTai = 3 },
                text = { Text("BT 3 (SmartTasks)") }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (baiTapHienTai) {
                1 -> BT01App()
                2 -> BT02App()
                3 -> BT03App()
            }
        }
    }
}
