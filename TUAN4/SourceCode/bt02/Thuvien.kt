package com.example.tuan_4.bt02

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// =====================================================================
// 4 ĐẶC TRƯNG CỦA OOP TRONG BÀI NÀY
// 1. Trừu tượng  (Abstraction)  : abstract class Sach
// 2. Đóng gói    (Encapsulation): private set, private val, chỉ đổi dữ liệu qua hàm
// 3. Kế thừa     (Inheritance)  : SachGiaoTrinh, SachThamKhao kế thừa Sach; SinhVien kế thừa NguoiDung
// 4. Đa hình     (Polymorphism) : override thongTin(), loaiSach(), vaiTro()
// =====================================================================

// ----- 1. TRỪU TƯỢNG: lớp Sach không tạo trực tiếp được, chỉ là khuôn cho các loại sách -----
abstract class Sach(val ma: String, val ten: String) {

    // ----- 2. ĐÓNG GÓI: bên ngoài chỉ đọc được, không gán trực tiếp (private set) -----
    var nguoiMuon: SinhVien? by mutableStateOf(null)
        private set

    // Muốn thay đổi người mượn thì phải đi qua 2 hàm này
    fun choMuon(sv: SinhVien): Boolean {
        if (nguoiMuon != null) {
            return false            // sách đã có người mượn
        }
        nguoiMuon = sv
        return true
    }

    fun nhanTra() {
        nguoiMuon = null
    }

    // Hàm trừu tượng: mỗi loại sách tự quy định
    abstract fun loaiSach(): String

    // Hàm có sẵn, lớp con có thể ghi đè
    open fun thongTin(): String {
        return loaiSach() + " - " + ten + " (mã " + ma + ")"
    }
}

// ----- 3. KẾ THỪA + 4. ĐA HÌNH -----
class SachGiaoTrinh(ma: String, ten: String, val monHoc: String) : Sach(ma, ten) {
    override fun loaiSach(): String {
        return "Giáo trình"
    }

    override fun thongTin(): String {
        return super.thongTin() + " - môn " + monHoc
    }
}

class SachThamKhao(ma: String, ten: String, val tacGia: String) : Sach(ma, ten) {
    override fun loaiSach(): String {
        return "Tham khảo"
    }

    override fun thongTin(): String {
        return super.thongTin() + " - tác giả " + tacGia
    }
}

// ----- Người dùng và sinh viên (kế thừa) -----
open class NguoiDung(val ten: String) {
    open fun vaiTro(): String {
        return "Người dùng"
    }
}

class SinhVien(ten: String, val maSV: String) : NguoiDung(ten) {

    // Đóng gói: danh sách sách đang mượn để private, bên ngoài không tự thêm/xóa được
    private val dsMuon = mutableStateListOf<Sach>()
    private val toiDa = 3

    fun muon(sach: Sach): Boolean {
        if (dsMuon.size >= toiDa) {
            return false            // mượn quá số lượng cho phép
        }
        if (sach.choMuon(this)) {
            dsMuon.add(sach)
            return true
        }
        return false
    }

    fun tra(sach: Sach) {
        sach.nhanTra()
        dsMuon.remove(sach)
    }

    fun dangMuon(): List<Sach> {
        return dsMuon
    }

    override fun vaiTro(): String {
        return "Sinh viên"
    }
}

// ----- Thư viện: quản lý danh sách sách và danh sách sinh viên -----
class ThuVien {
    private val dsSach = mutableStateListOf<Sach>()
    private val dsSinhVien = mutableStateListOf<SinhVien>()

    init {
        // Tạo danh sách sách
        dsSach.add(SachGiaoTrinh("S01", "Sách 01", "Lập trình di động"))
        dsSach.add(SachThamKhao("S02", "Sách 02", "Nguyễn Văn B"))

        // Tạo danh sách sinh viên
        val a = SinhVien("Nguyen Van A", "SV001")
        dsSinhVien.add(a)
        dsSinhVien.add(SinhVien("Tran Thi B", "SV002"))
        dsSinhVien.add(SinhVien("Le Van C", "SV003"))

        // Giống hình mẫu: sinh viên A đã mượn 2 cuốn
        a.muon(dsSach[0])
        a.muon(dsSach[1])
    }

    fun layDanhSachSach(): List<Sach> {
        return dsSach
    }

    fun layDanhSachSinhVien(): List<SinhVien> {
        return dsSinhVien
    }

    // Nút "Thêm": thêm một cuốn sách mới
    fun themSach() {
        val so = dsSach.size + 1
        var soHienThi = "" + so
        if (so < 10) {
            soHienThi = "0" + so
        }
        val ten = "Sách " + soHienThi

        if (so % 2 == 1) {
            dsSach.add(SachGiaoTrinh("S" + soHienThi, ten, "Lập trình di động"))
        } else {
            dsSach.add(SachThamKhao("S" + soHienThi, ten, "Nguyễn Văn B"))
        }
    }

    // Nút "Thay đổi": tìm sinh viên theo tên, chưa có thì tạo mới
    fun timHoacTaoSinhVien(ten: String): SinhVien {
        val tenDaCat = ten.trim()
        for (sv in dsSinhVien) {
            if (sv.ten.equals(tenDaCat, ignoreCase = true)) {
                return sv
            }
        }
        val moi = SinhVien(tenDaCat, "SV00" + (dsSinhVien.size + 1))
        dsSinhVien.add(moi)
        return moi
    }
}