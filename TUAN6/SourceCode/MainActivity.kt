package com.example.tuan_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

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

// Thanh TabRow ở trên cùng để chuyển giữa bài chính và 2 bài nâng cao
@Composable
fun UngDung() {
    var tab by remember { mutableStateOf(0) }

    Scaffold(contentWindowInsets = WindowInsets.statusBars) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = (tab == 0), onClick = { tab = 0 }, text = { Text("SmartTasks") })
                Tab(selected = (tab == 1), onClick = { tab = 1 }, text = { Text("Nâng cao 1") })
                Tab(selected = (tab == 2), onClick = { tab = 2 }, text = { Text("Nâng cao 2") })
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (tab == 0) {
                    SmartTasksApp()
                } else if (tab == 1) {
                    NangCao01Quyen()
                } else {
                    NangCao02Crypto()
                }
            }
        }
    }
}
