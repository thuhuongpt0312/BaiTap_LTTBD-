package com.example.tuan_3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Màn hình chi tiết: thanh tiêu đề (nút quay lại + tên) và nội dung theo từng thành phần
@Composable
fun ManHinhChiTiet(ten: String, onBack: () -> Unit) {
    var tieuDe = ten
    if (ten == "Text") {
        tieuDe = "Text Detail"
    } else if (ten == "Image") {
        tieuDe = "Images"
    } else if (ten == "Row") {
        tieuDe = "Row Layout"
    } else if (ten == "Column") {
        tieuDe = "Column Layout"
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "<",
                    fontSize = 24.sp,
                    color = mauXanh,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                )
                Text(
                    tieuDe,
                    fontSize = 20.sp,
                    color = mauXanh,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Chọn nội dung theo tên thành phần
                if (ten == "Text") {
                    ChiTietText()
                } else if (ten == "Image") {
                    ChiTietImage()
                } else if (ten == "TextField") {
                    ChiTietTextField()
                } else if (ten == "PasswordField") {
                    ChiTietPasswordField()
                } else if (ten == "Toggle") {
                    ChiTietToggle()
                } else if (ten == "Slider") {
                    ChiTietSlider()
                } else if (ten == "DatePicker") {
                    ChiTietDatePicker()
                } else if (ten == "Button") {
                    ChiTietButton()
                } else if (ten == "Checkbox") {
                    ChiTietCheckbox()
                } else if (ten == "RadioButton") {
                    ChiTietRadioButton()
                } else if (ten == "Picker") {
                    ChiTietPicker()
                } else if (ten == "NavigationView") {
                    ChiTietNavigationView()
                } else if (ten == "TabBar") {
                    ChiTietTabBar()
                } else if (ten == "Menu") {
                    ChiTietMenu()
                } else if (ten == "List") {
                    ChiTietList()
                } else if (ten == "Card") {
                    ChiTietCard()
                } else if (ten == "Divider") {
                    ChiTietDivider()
                } else if (ten == "Column") {
                    ChiTietColumn()
                } else if (ten == "Row") {
                    ChiTietRow()
                }
            }
        }
    }
}

// ---------- Display ----------

@Composable
fun ChiTietText() {
    // Mỗi đoạn chữ có một kiểu riêng
    val noiDung = buildAnnotatedString {
        append("The ")
        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append("quick") }
        append(" ")
        withStyle(SpanStyle(color = Color(0xFFCC7A00), fontSize = 32.sp)) { append("Brown") }
        append("\nfox ")
        withStyle(SpanStyle(letterSpacing = 6.sp)) { append("jumps") }
        append(" ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) { append("over") }
        append("\n")
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append("the") }
        append(" ")
        withStyle(SpanStyle(fontStyle = FontStyle.Italic, fontFamily = FontFamily.Cursive, fontSize = 16.sp)) { append("lazy") }
        append(" dog.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(noiDung, fontSize = 24.sp, lineHeight = 40.sp)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
    }
}

@Composable
fun ChiTietImage() {
    val link = "https://giaothongvantaitphcm.edu.vn/wp-content/uploads/2025/01/Logo-GTVT.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ảnh tải từ internet
        AsyncImage(
            model = link,
            contentDescription = "Ảnh từ internet",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            link,
            fontSize = 11.sp,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ảnh nằm trong app
        Image(
            painter = painterResource(id = R.drawable.logo_full),
            contentDescription = "Ảnh trong app",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF3DDC84)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("In app", fontSize = 12.sp, color = Color.Gray)
    }
}

// ---------- Input ----------

@Composable
fun ChiTietTextField() {
    var chu by remember { mutableStateOf("") }

    // Chưa nhập gì thì hiện dòng hướng dẫn, nhập rồi thì hiện đúng chữ đã nhập
    var hienThi = chu
    if (chu == "") {
        hienThi = "Tự động cập nhật dữ liệu theo textfield"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = chu,
            onValueChange = { chu = it },
            placeholder = { Text("Thông tin nhập") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            hienThi,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ChiTietPasswordField() {
    var matKhau by remember { mutableStateOf("") }
    var hien by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = matKhau,
            onValueChange = { matKhau = it },
            placeholder = { Text("Nhập mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            // Ẩn mật khẩu thành dấu chấm, bấm nút để hiện
            visualTransformation = if (hien) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { hien = !hien }) {
                    Text(if (hien) "Ẩn" else "Hiện")
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Độ dài mật khẩu: " + matKhau.length, fontSize = 14.sp)
    }
}

@Composable
fun ChiTietToggle() {
    var bat by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Switch(checked = bat, onCheckedChange = { bat = it })
        Spacer(modifier = Modifier.height(16.dp))
        if (bat) {
            Text("Đang BẬT", fontSize = 18.sp, color = Color(0xFF2E7D32))
        } else {
            Text("Đang TẮT", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ChiTietSlider() {
    var giaTri by remember { mutableStateOf(30f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Slider(
            value = giaTri,
            onValueChange = { giaTri = it },
            valueRange = 0f..100f
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Giá trị: " + giaTri.toInt(),
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietDatePicker() {
    val state = rememberDatePickerState()

    // Đổi số mili giây thành dạng ngày/tháng/năm
    var ngay = "Chưa chọn ngày"
    val mili = state.selectedDateMillis
    if (mili != null) {
        val dinhDang = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dinhDang.timeZone = TimeZone.getTimeZone("UTC")
        ngay = dinhDang.format(Date(mili))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Ngày đã chọn: " + ngay, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        DatePicker(state = state)
    }
}

// ---------- Selection ----------

@Composable
fun ChiTietButton() {
    var dem by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { dem = dem + 1 }) {
            Text("Button")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = { dem = dem + 1 }) {
            Text("OutlinedButton")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { dem = dem + 1 }) {
            Text("TextButton")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Đã bấm: " + dem + " lần", fontSize = 18.sp)
    }
}

@Composable
fun ChiTietCheckbox() {
    var chonKotlin by remember { mutableStateOf(false) }
    var chonJava by remember { mutableStateOf(false) }
    var chonDart by remember { mutableStateOf(false) }

    var daChon = ""
    if (chonKotlin) {
        daChon = daChon + "Kotlin "
    }
    if (chonJava) {
        daChon = daChon + "Java "
    }
    if (chonDart) {
        daChon = daChon + "Dart "
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Chọn ngôn ngữ bạn biết:", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = chonKotlin, onCheckedChange = { chonKotlin = it })
            Text("Kotlin")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = chonJava, onCheckedChange = { chonJava = it })
            Text("Java")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = chonDart, onCheckedChange = { chonDart = it })
            Text("Dart")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Đã chọn: " + daChon)
    }
}

@Composable
fun ChiTietRadioButton() {
    val dsLuaChon = listOf("Nam", "Nữ", "Khác")
    var chon by remember { mutableStateOf("Nam") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Giới tính:", fontWeight = FontWeight.Bold)
        for (luaChon in dsLuaChon) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = (chon == luaChon), onClick = { chon = luaChon })
                Text(luaChon)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Đã chọn: " + chon)
    }
}

@Composable
fun ChiTietPicker() {
    val dsTinh = listOf("TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Huế")
    var chon by remember { mutableStateOf(dsTinh[0]) }
    var mo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Chọn thành phố:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            OutlinedButton(onClick = { mo = true }) {
                Text(chon + "  ▼")
            }
            DropdownMenu(expanded = mo, onDismissRequest = { mo = false }) {
                for (tinh in dsTinh) {
                    DropdownMenuItem(
                        text = { Text(tinh) },
                        onClick = {
                            chon = tinh
                            mo = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Giá trị đã chọn: " + chon)
    }
}

// ---------- Navigation ----------

@Composable
fun ChiTietNavigationView() {
    var trang by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Mô phỏng NavigationView: có tiêu đề và nút quay lại.", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (trang == 2) {
                    Text(
                        "<",
                        fontSize = 22.sp,
                        color = mauXanh,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable { trang = 1 }
                    )
                }
                Text(
                    "Trang " + trang,
                    fontSize = 18.sp,
                    color = mauXanh,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (trang == 1) {
                Button(onClick = { trang = 2 }) {
                    Text("Đi tới trang 2")
                }
            } else {
                Text("Đây là trang 2, bấm < để quay lại.")
            }
        }
    }
}

@Composable
fun ChiTietTabBar() {
    val dsTab = listOf("Trang chủ", "Tìm kiếm", "Cá nhân")
    var tab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            for (i in dsTab.indices) {
                Tab(
                    selected = (tab == i),
                    onClick = { tab = i },
                    text = { Text(dsTab[i]) }
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nội dung tab: " + dsTab[tab], fontSize = 18.sp)
        }
    }
}

@Composable
fun ChiTietMenu() {
    var mo by remember { mutableStateOf(false) }
    var hanhDong by remember { mutableStateOf("Chưa chọn") }
    val dsHanhDong = listOf("Sao chép", "Chia sẻ", "Xóa")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Button(onClick = { mo = true }) {
                Text("Mở menu")
            }
            DropdownMenu(expanded = mo, onDismissRequest = { mo = false }) {
                for (hd in dsHanhDong) {
                    DropdownMenuItem(
                        text = { Text(hd) },
                        onClick = {
                            hanhDong = hd
                            mo = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hành động vừa chọn: " + hanhDong)
    }
}

// ---------- Content ----------

@Composable
fun ChiTietList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(20) { i ->
            Text(
                "Mục số " + (i + 1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ChiTietCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (i in 1..3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Card số " + i, fontWeight = FontWeight.Bold)
                    Text("Card là khung chứa nội dung, có nền và đổ bóng.", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ChiTietDivider() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Phần 1")
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text("Phần 2")
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 3.dp,
            color = mauXanh
        )
        Text("Phần 3")
    }
}

// ---------- Layout ----------

@Composable
fun ChiTietColumn() {
    // 3 cột, mỗi cột xếp 4 ô theo chiều dọc
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (cot in 1..3) {
            var mau = Color(0xFF9DB8F5)
            if (cot == 2) {
                mau = Color(0xFF4285F4)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (hang in 1..4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(mau, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("" + hang, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChiTietRow() {
    // 4 hàng, mỗi hàng xếp 3 ô theo chiều ngang
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (hang in 1..4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (cot in 1..3) {
                    var mau = Color(0xFF9DB8F5)
                    if (cot == 2) {
                        mau = Color(0xFF4285F4)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .background(mau, RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}