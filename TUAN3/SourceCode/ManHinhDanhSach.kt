package com.example.tuan_3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Màu dùng chung cho cả app
val mauXanh = Color(0xFF2196F3)
val mauNenXanh = Color(0xFFBBDEFB)

// Một thành phần UI và một nhóm thành phần
class ThanhPhan(val ten: String, val moTa: String)
class Nhom(val tenNhom: String, val dsThanhPhan: List<ThanhPhan>)

@Composable
fun ManHinhDanhSach(onChon: (String) -> Unit) {
    val dsNhom = listOf(
        Nhom("Display", listOf(
            ThanhPhan("Text", "Displays text"),
            ThanhPhan("Image", "Displays an image")
        )),
        Nhom("Input", listOf(
            ThanhPhan("TextField", "Input field for text"),
            ThanhPhan("PasswordField", "Input field for passwords"),
            ThanhPhan("Toggle", "On/off switch"),
            ThanhPhan("Slider", "Selects a value from a range"),
            ThanhPhan("DatePicker", "Selects a date and time")
        )),
        Nhom("Selection", listOf(
            ThanhPhan("Button", "Triggers an action when tapped"),
            ThanhPhan("Checkbox", "Selects one or multiple options"),
            ThanhPhan("RadioButton", "Selects a single option"),
            ThanhPhan("Picker", "Selects a value from a list")
        )),
        Nhom("Navigation", listOf(
            ThanhPhan("NavigationView", "Navigation container with title and back button"),
            ThanhPhan("TabBar", "Switches between main sections"),
            ThanhPhan("Menu", "Shows a menu with multiple actions")
        )),
        Nhom("Content", listOf(
            ThanhPhan("List", "Displays a list of items"),
            ThanhPhan("Card", "Container with content and style"),
            ThanhPhan("Divider", "Separates content into sections")
        )),
        Nhom("Layout", listOf(
            ThanhPhan("Column", "Arranges elements vertically"),
            ThanhPhan("Row", "Arranges elements horizontally")
        ))
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "UI Components List",
                fontSize = 20.sp,
                color = mauXanh,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dùng for để tạo từng nhóm và từng thành phần
            for (nhom in dsNhom) {
                Text(
                    nhom.tenNhom,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
                for (tp in nhom.dsThanhPhan) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(mauNenXanh, RoundedCornerShape(8.dp))
                            .clickable { onChon(tp.ten) }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(tp.ten, fontWeight = FontWeight.Bold)
                            Text(tp.moTa, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}