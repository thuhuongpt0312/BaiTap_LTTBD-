package com.example.tuan_6

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

// ViewModel dùng chung cho 3 màn hình (Trang chủ, Danh sách, Chi tiết).
// Dữ liệu tải từ API về và giữ trong bộ nhớ. Mọi thay đổi cập nhật ngay trên màn hình,
// đồng thời gửi lên API (PATCH / DELETE).
// Lưu ý: API mẫu luôn trả về cùng một dữ liệu, nên app lấy dữ liệu trong bộ nhớ làm chuẩn.
class TaskViewModel : ViewModel() {

    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set
    var dangTai by mutableStateOf(false)
        private set
    var loi by mutableStateOf("")
        private set
    var thongBao by mutableStateOf("")
        private set

    init {
        taiDanhSach()
    }

    fun xoaThongBao() {
        thongBao = ""
    }

    fun layCongViec(id: Int): Task? {
        return tasks.find { it.id == id }
    }

    // ---------- GET: tải danh sách ----------
    fun taiDanhSach() {
        viewModelScope.launch {
            dangTai = true
            loi = ""
            try {
                tasks = DichVuApi.api.layDanhSach()
            } catch (e: Exception) {
                loi = "Không tải được dữ liệu: " + e.message
            }
            dangTai = false
        }
    }

    // ---------- GET: tải chi tiết một việc ----------
    // API mẫu luôn trả về cùng một việc, nên chỉ dùng kết quả khi việc đó chưa có trong bộ nhớ.
    fun taiChiTiet(id: Int) {
        viewModelScope.launch {
            try {
                val moi = DichVuApi.api.layChiTiet(id)
                if (moi.id == id && layCongViec(id) == null) {
                    tasks = tasks + moi
                }
            } catch (e: Exception) {
                // Không gọi được thì dùng dữ liệu đang có
            }
        }
    }

    private fun capNhatTaiCho(id: Int, doi: (Task) -> Task) {
        tasks = tasks.map { if (it.id == id) doi(it) else it }
    }

    // ---------- PATCH ----------
    private fun guiPatch(id: Int, thayDoi: JsonObject) {
        viewModelScope.launch {
            try {
                val phanHoi = DichVuApi.api.capNhat(id, thayDoi)
                if (!phanHoi.isSuccessful) {
                    thongBao = "Cập nhật lỗi (mã " + phanHoi.code() + ")"
                }
            } catch (e: Exception) {
                thongBao = "Không kết nối được máy chủ"
            }
        }
    }

    // Tick xong / bỏ tick
    fun doiTrangThai(id: Int) {
        val cv = layCongViec(id)
        if (cv == null) {
            return
        }
        val xong = !cv.done
        capNhatTaiCho(id) { it.copy(done = xong) }
        val body = JsonObject()
        body.addProperty("done", xong)
        guiPatch(id, body)
    }

    // Sửa tiêu đề và ghi chú
    fun suaCongViec(id: Int, tieuDe: String, ghiChu: String) {
        if (tieuDe.isBlank()) {
            return
        }
        capNhatTaiCho(id) { it.copy(title = tieuDe.trim(), note = ghiChu.trim()) }
        val body = JsonObject()
        body.addProperty("title", tieuDe.trim())
        body.addProperty("note", ghiChu.trim())
        guiPatch(id, body)
    }

    // ---------- Việc nhỏ ----------
    private fun datViecNho(id: Int, ds: List<ViecNho>) {
        capNhatTaiCho(id) { it.copy(subtasks = ds) }
        val mang = JsonArray()
        for (v in ds) {
            val o = JsonObject()
            o.addProperty("title", v.title)
            o.addProperty("done", v.done)
            mang.add(o)
        }
        val body = JsonObject()
        body.add("subtasks", mang)
        guiPatch(id, body)
    }

    private fun danhSachViecNho(id: Int): List<ViecNho> {
        return layCongViec(id)?.subtasks ?: emptyList()
    }

    fun themViecNho(id: Int, ten: String) {
        if (ten.isNotBlank()) {
            datViecNho(id, danhSachViecNho(id) + ViecNho(ten.trim(), false))
        }
    }

    fun doiViecNho(id: Int, viTri: Int) {
        val ds = danhSachViecNho(id).mapIndexed { i, v ->
            if (i == viTri) v.copy(done = !v.done) else v
        }
        datViecNho(id, ds)
    }

    fun suaViecNho(id: Int, viTri: Int, tenMoi: String) {
        if (tenMoi.isNotBlank()) {
            val ds = danhSachViecNho(id).mapIndexed { i, v ->
                if (i == viTri) v.copy(title = tenMoi.trim()) else v
            }
            datViecNho(id, ds)
        }
    }

    fun xoaViecNho(id: Int, viTri: Int) {
        val ds = danhSachViecNho(id).filterIndexed { i, _ -> i != viTri }
        datViecNho(id, ds)
    }

    // ---------- DELETE ----------
    fun xoaCongViec(id: Int) {
        tasks = tasks.filter { it.id != id }
        viewModelScope.launch {
            try {
                val phanHoi = DichVuApi.api.xoa(id)
                if (!phanHoi.isSuccessful) {
                    thongBao = "Xóa lỗi (mã " + phanHoi.code() + ")"
                }
            } catch (e: Exception) {
                thongBao = "Không kết nối được máy chủ"
            }
        }
    }

    // ---------- Thêm việc mới (chỉ lưu trong bộ nhớ) ----------
    fun themCongViec(ten: String, nhom: String) {
        var lonNhat = 0
        for (t in tasks) {
            if (t.id > lonNhat) {
                lonNhat = t.id
            }
        }
        tasks = tasks + Task(
            id = lonNhat + 1,
            title = ten,
            note = "",
            category = nhom,
            date = NGAY_HOM_NAY,
            time = "",
            priority = "medium",
            remind = false,
            done = false,
            subtasks = emptyList()
        )
    }
}
