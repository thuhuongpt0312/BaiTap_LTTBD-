package com.example.tuan_5

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

// =====================================================================
// CÁC DỊCH VỤ FIREBASE DÙNG TRONG FILE NÀY
//  - Realtime Database : lưu công việc (CongViec, KhoCongViec)
//  - Remote Config     : đổi nội dung app từ xa (CauHinhTuXa)
//  - Analytics         : ghi lại hành động của người dùng (PhanTich)
//  - Storage           : lưu ảnh đại diện (DichVuAnh)
// =====================================================================

// ----- Một công việc. Mọi thuộc tính đều có giá trị mặc định để Firebase đọc dữ liệu về được -----
class CongViec(
    var id: String = "",
    var ten: String = "",
    var nhom: String = "Học tập",
    var gio: String = "",
    var daXong: Boolean = false,
    var ngayXong: Int = 0      // thứ trong tuần lúc hoàn thành (Calendar: 1 = CN, 2 = T2 ... 7 = T7)
)

// ----- Realtime Database: mỗi người dùng có một nhánh users/<uid>/congviec -----
class KhoCongViec(uid: String) {
    private val goc = FirebaseDatabase.getInstance()
        .getReference("users").child(uid).child("congviec")

    // Mỗi lần dữ liệu trên server đổi, hàm capNhat được gọi với danh sách mới
    fun langNghe(capNhat: (List<CongViec>) -> Unit): ValueEventListener {
        val boNghe = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ds = mutableListOf<CongViec>()
                for (con in snapshot.children) {
                    val cv = con.getValue(CongViec::class.java)
                    if (cv != null) {
                        ds.add(cv)
                    }
                }
                capNhat(ds)
            }

            override fun onCancelled(error: DatabaseError) {
                // Lỗi thường gặp: chưa đặt Rules cho Realtime Database
            }
        }
        goc.addValueEventListener(boNghe)
        return boNghe
    }

    fun huyNghe(boNghe: ValueEventListener) {
        goc.removeEventListener(boNghe)
    }

    fun them(ten: String, nhom: String, gio: String) {
        val id = goc.push().key
        if (id != null) {
            goc.child(id).setValue(CongViec(id, ten, nhom, gio, false, 0))
        }
    }

    fun doiTrangThai(cv: CongViec) {
        val xong = !cv.daXong
        var thu = 0
        if (xong) {
            thu = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        }
        goc.child(cv.id).child("daXong").setValue(xong)
        goc.child(cv.id).child("ngayXong").setValue(thu)
    }

    fun xoa(cv: CongViec) {
        goc.child(cv.id).removeValue()
    }
}

// Dùng ở màn hình: trả về danh sách tự cập nhật theo dữ liệu trên Firebase
@Composable
fun dungDanhSachCongViec(kho: KhoCongViec?): SnapshotStateList<CongViec> {
    val ds = remember { mutableStateListOf<CongViec>() }
    DisposableEffect(Unit) {
        val boNghe = kho?.langNghe { moi ->
            ds.clear()
            ds.addAll(moi.sortedBy { it.gio })
        }
        onDispose {
            if (boNghe != null) {
                kho?.huyNghe(boNghe)
            }
        }
    }
    return ds
}

// ----- Remote Config: sửa nội dung trong Firebase console, app tự đổi theo -----
object CauHinhTuXa {
    var loiDongVien by mutableStateOf("Đã hơn nửa chặng, cố lên!")
        private set
    var hienFacebook by mutableStateOf(true)
        private set

    fun tai() {
        val rc = FirebaseRemoteConfig.getInstance()
        val caiDat = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(60)
            .build()
        rc.setConfigSettingsAsync(caiDat)
        rc.setDefaultsAsync(
            mapOf(
                "loi_dong_vien" to "Đã hơn nửa chặng, cố lên!",
                "hien_facebook" to true
            )
        )
        rc.fetchAndActivate().addOnCompleteListener {
            loiDongVien = rc.getString("loi_dong_vien")
            hienFacebook = rc.getBoolean("hien_facebook")
        }
    }
}

// ----- Analytics: ghi sự kiện -----
object PhanTich {
    fun ghi(context: Context, tenSuKien: String) {
        FirebaseAnalytics.getInstance(context).logEvent(tenSuKien, null)
    }

    fun ghiDangNhap(context: Context, cach: String) {
        val goi = Bundle()
        goi.putString(FirebaseAnalytics.Param.METHOD, cach)
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.LOGIN, goi)
    }
}

// ----- Storage: tải ảnh đại diện lên avatars/<uid>.jpg -----
object DichVuAnh {
    fun taiLen(uid: String, uri: Uri, ketQua: (Boolean, String, Uri?) -> Unit) {
        val ref = FirebaseStorage.getInstance().reference.child("avatars/" + uid + ".jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { link ->
                        val yeuCau = UserProfileChangeRequest.Builder().setPhotoUri(link).build()
                        FirebaseAuth.getInstance().currentUser?.updateProfile(yeuCau)
                        ketQua(true, "Đã cập nhật ảnh đại diện", link)
                    }
                    .addOnFailureListener { e ->
                        ketQua(false, "Lỗi lấy đường dẫn ảnh: " + e.message, null)
                    }
            }
            .addOnFailureListener { e ->
                ketQua(false, "Lỗi tải ảnh lên: " + e.message, null)
            }
    }
}