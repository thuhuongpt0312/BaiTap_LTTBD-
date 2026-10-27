package com.example.tuan_4.bt01

import androidx.compose.ui.graphics.Color

val mauXanhTodo = Color(0xFF3B5BDB)

// Thông tin chung của ứng dụng
class ThongTinUngDung(val ten: String, val khauHieu: String, val phienBan: String)

// Một bước giới thiệu (onboarding)
class BuocGioiThieu(val tieuDe: String, val moTa: String, val tenNut: String)

// Một công việc
class CongViec(val ten: String, val nhom: String, val hanChot: String)

// Toàn bộ dữ liệu của app đều là đối tượng của các class ở trên
object DuLieuTodo {
    val thongTin = ThongTinUngDung(
        "UTH Todo",
        "Việc học, việc nhà — gọn gàng mỗi ngày",
        "Phiên bản 2.1.0"
    )

    val dsBuoc = listOf(
        BuocGioiThieu(
            "Mọi việc, gọn trong một chỗ",
            "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
            "Tiếp tục"
        ),
        BuocGioiThieu(
            "Chia theo Học tập và Cuộc sống",
            "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
            "Tiếp tục"
        ),
        BuocGioiThieu(
            "Nhắc đúng lúc",
            "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
            "Bắt đầu ngay"
        )
    )

    val dsCongViec = listOf(
        CongViec("Ôn bài Toán", "Học tập", "19:00"),
        CongViec("Nộp bài tập LTTBĐ", "Học tập", "21:00"),
        CongViec("Dọn phòng", "Cuộc sống", "Cuối tuần")
    )
}