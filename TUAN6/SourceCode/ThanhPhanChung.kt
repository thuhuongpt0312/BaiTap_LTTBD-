package com.example.tuan_6

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val mauXanhTask = Color(0xFF3B5BDB)
val mauNenTrang = Color(0xFFF5F6FA)

// Dữ liệu mẫu của API cố định ngày 20/10/2026 nên app lấy ngày này làm "hôm nay"
const val NGAY_HOM_NAY = "2026-10-20"

// ---------- Các hàm định dạng ----------

fun tenNhom(category: String): String {
    if (category == "study") {
        return "Học tập"
    }
    return "Cuộc sống"
}

fun tenUuTien(priority: String): String {
    if (priority == "high") {
        return "Ưu tiên cao"
    } else if (priority == "low") {
        return "Ưu tiên thấp"
    }
    return "Ưu tiên vừa"
}

// 2026-10-20 + 14:00 -> "Hôm nay, 14:00"
fun nhanNgay(date: String, gio: String): String {
    var nhan = date
    try {
        val dd = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val soNgay = ((dd.parse(date)!!.time - dd.parse(NGAY_HOM_NAY)!!.time) / 86400000L).toInt()
        if (soNgay == 0) {
            nhan = "Hôm nay"
        } else if (soNgay == 1) {
            nhan = "Ngày mai"
        } else if (soNgay in 2..6) {
            nhan = "Tuần này"
        } else {
            nhan = date.substring(8, 10) + "/" + date.substring(5, 7)
        }
    } catch (e: Exception) {
        // giữ nguyên chuỗi ngày gốc
    }
    if (gio != "") {
        nhan = nhan + ", " + gio
    }
    return nhan
}

// "Thứ Ba, 20 tháng 10"
fun ngayHomNayDayDu(): String {
    val cal = Calendar.getInstance()
    cal.time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(NGAY_HOM_NAY)!!
    val cacThu = listOf("Chủ nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy")
    val thu = cacThu[cal.get(Calendar.DAY_OF_WEEK) - 1]
    return thu + ", " + cal.get(Calendar.DAY_OF_MONTH) + " tháng " + (cal.get(Calendar.MONTH) + 1)
}

// ---------- Các thành phần giao diện dùng chung ----------

// Nhãn nhóm: Học tập (xanh) / Cuộc sống (cam)
@Composable
fun ChipNhom(category: String) {
    var mauChu = mauXanhTask
    var mauNen = Color(0xFFE3E9FF)
    if (category != "study") {
        mauChu = Color(0xFFD9822B)
        mauNen = Color(0xFFFDEBD6)
    }
    Text(
        tenNhom(category),
        fontSize = 11.sp,
        color = mauChu,
        modifier = Modifier
            .background(mauNen, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

// Một dòng công việc dùng ở Trang chủ và Danh sách
@Composable
fun HangCongViec(cv: Task, hienNgay: Boolean, onTick: () -> Unit, onMo: () -> Unit) {
    var kieuChu = TextDecoration.None
    var mauTen = Color.Black
    if (cv.done) {
        kieuChu = TextDecoration.LineThrough
        mauTen = Color.Gray
    }
    var nhanGio = cv.time
    if (hienNgay) {
        nhanGio = nhanNgay(cv.date, cv.time)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(14.dp))
            .clickable { onMo() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = cv.done, onCheckedChange = { onTick() })
        Column(modifier = Modifier.weight(1f)) {
            Text(cv.title, textDecoration = kieuChu, color = mauTen, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChipNhom(cv.category)
                Spacer(modifier = Modifier.width(8.dp))
                Text(nhanGio, fontSize = 12.sp, color = Color.Gray)
            }
        }
        if (cv.priority == "high" && !cv.done) {
            Text(
                "Gấp",
                fontSize = 11.sp,
                color = Color(0xFFC0392B),
                modifier = Modifier
                    .background(Color(0xFFFBE0DD), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Text("›", fontSize = 22.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
    }
}

// Thanh điều hướng dưới: Trang chủ, Danh sách, Thông báo, Cài đặt
@Composable
fun ThanhDieuHuong(dangChon: String, onDi: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = (dangChon == "trangchu"),
            onClick = { onDi("trangchu") },
            icon = { Text("🏠") },
            label = { Text("Trang chủ") }
        )
        NavigationBarItem(
            selected = (dangChon == "danhsach"),
            onClick = { onDi("danhsach") },
            icon = { Text("📋") },
            label = { Text("Danh sách") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onDi("thongbao") },
            icon = { Text("🔔") },
            label = { Text("Thông báo") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { onDi("caidat") },
            icon = { Text("⚙️") },
            label = { Text("Cài đặt") }
        )
    }
}

// Nút + ở giữa
@Composable
fun NutThem(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = mauXanhTask,
        contentColor = Color.White
    ) {
        Text("+", fontSize = 26.sp)
    }
}

// Hộp thoại thêm việc mới
@Composable
fun HopThoaiThemCongViec(onHuy: () -> Unit, onThem: (String, String) -> Unit) {
    var ten by remember { mutableStateOf("") }
    var nhom by remember { mutableStateOf("study") }

    AlertDialog(
        onDismissRequest = onHuy,
        title = { Text("Thêm việc mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = ten,
                    onValueChange = { ten = it },
                    label = { Text("Tên việc") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (nhom == "study"), onClick = { nhom = "study" })
                    Text("Học tập")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = (nhom == "life"), onClick = { nhom = "life" })
                    Text("Cuộc sống")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (ten.isNotBlank()) {
                    onThem(ten.trim(), nhom)
                }
            }) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onHuy) {
                Text("Hủy")
            }
        }
    )
}
