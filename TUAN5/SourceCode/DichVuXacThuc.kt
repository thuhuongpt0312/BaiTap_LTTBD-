package com.example.tuan_5

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

// Firebase Authentication: gom tất cả cách đăng nhập vào một chỗ.
// Mọi hàm đều báo kết quả qua ketQua(thanhCong, thongBao).
object DichVuXacThuc {
    private val auth = FirebaseAuth.getInstance()

    fun nguoiDungHienTai(): FirebaseUser? {
        return auth.currentUser
    }

    // Dịch lỗi của Firebase sang tiếng Việt
    fun dichLoi(e: Exception?): String {
        if (e is FirebaseAuthInvalidCredentialsException) {
            return "Thông tin đăng nhập không đúng hoặc không hợp lệ"
        } else if (e is FirebaseAuthInvalidUserException) {
            return "Tài khoản không tồn tại hoặc đã bị khóa"
        } else if (e is FirebaseAuthUserCollisionException) {
            return "Email này đã được đăng ký"
        } else if (e is FirebaseTooManyRequestsException) {
            return "Thao tác quá nhiều lần, vui lòng thử lại sau"
        } else if (e != null && e.message != null) {
            return e.message!!
        }
        return "Lỗi không xác định"
    }

    private fun xuLyKetQua(task: Task<AuthResult>, ketQua: (Boolean, String) -> Unit) {
        task.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                ketQua(true, "Đăng nhập thành công")
            } else {
                ketQua(false, dichLoi(t.exception))
            }
        }
    }

    // ----- 1. Email + mật khẩu -----
    fun dangNhapEmail(email: String, matKhau: String, ketQua: (Boolean, String) -> Unit) {
        if (email.isBlank() || matKhau.isBlank()) {
            ketQua(false, "Vui lòng nhập email và mật khẩu")
            return
        }
        xuLyKetQua(auth.signInWithEmailAndPassword(email.trim(), matKhau), ketQua)
    }

    fun dangKyEmail(email: String, matKhau: String, ketQua: (Boolean, String) -> Unit) {
        if (email.isBlank() || matKhau.length < 6) {
            ketQua(false, "Email không được trống và mật khẩu từ 6 ký tự")
            return
        }
        xuLyKetQua(auth.createUserWithEmailAndPassword(email.trim(), matKhau), ketQua)
    }

    // ----- 2. Google (Credential Manager) -----
    suspend fun dangNhapGoogle(context: Context, ketQua: (Boolean, String) -> Unit) {
        // default_web_client_id do plugin google-services tự tạo từ google-services.json
        val idClient = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (idClient == 0) {
            ketQua(false, "Thiếu default_web_client_id: bật Google trong Firebase, thêm SHA-1 rồi tải lại google-services.json")
            return
        }
        try {
            val tuyChon = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(idClient))
                .setFilterByAuthorizedAccounts(false)
                .build()
            val yeuCau = GetCredentialRequest.Builder()
                .addCredentialOption(tuyChon)
                .build()
            val phanHoi = CredentialManager.create(context).getCredential(context, yeuCau)
            val thongTin = phanHoi.credential

            if (thongTin is CustomCredential &&
                thongTin.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val idToken = GoogleIdTokenCredential.createFrom(thongTin.data).idToken
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                xuLyKetQua(auth.signInWithCredential(credential), ketQua)
            } else {
                ketQua(false, "Không nhận được tài khoản Google")
            }
        } catch (e: NoCredentialException) {
            ketQua(false, "Máy chưa có tài khoản Google. Hãy thêm tài khoản Google vào máy ảo rồi thử lại")
        } catch (e: GetCredentialException) {
            ketQua(false, "Đăng nhập Google thất bại: " + e.message)
        }
    }

    // ----- 3. Facebook (token lấy từ Facebook SDK) -----
    fun dangNhapFacebook(token: AccessToken, ketQua: (Boolean, String) -> Unit) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        xuLyKetQua(auth.signInWithCredential(credential), ketQua)
    }

    // ----- 4. Số điện thoại: bước 1 gửi mã, bước 2 xác nhận mã -----
    fun guiMaSDT(
        activity: Activity,
        soDienThoai: String,
        onMaDaGui: (String) -> Unit,
        ketQua: (Boolean, String) -> Unit
    ) {
        // 0912345678 -> +84912345678
        var so = soDienThoai.trim()
        if (so.startsWith("0")) {
            so = "+84" + so.substring(1)
        }
        if (so.length < 10) {
            ketQua(false, "Số điện thoại không hợp lệ")
            return
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Máy tự đọc được mã nên đăng nhập luôn
                dangNhapBangCredential(credential, ketQua)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                ketQua(false, dichLoi(e))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onMaDaGui(verificationId)
            }
        }

        val tuyChon = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(so)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(tuyChon)
    }

    fun xacNhanMaSDT(idXacMinh: String, ma: String, ketQua: (Boolean, String) -> Unit) {
        if (ma.length < 6) {
            ketQua(false, "Mã xác minh gồm 6 số")
            return
        }
        val credential = PhoneAuthProvider.getCredential(idXacMinh, ma)
        dangNhapBangCredential(credential, ketQua)
    }

    private fun dangNhapBangCredential(credential: AuthCredential, ketQua: (Boolean, String) -> Unit) {
        xuLyKetQua(auth.signInWithCredential(credential), ketQua)
    }

    // ----- 5. Dùng thử không cần tài khoản (ẩn danh) -----
    fun dangNhapAnDanh(ketQua: (Boolean, String) -> Unit) {
        xuLyKetQua(auth.signInAnonymously(), ketQua)
    }

    // ----- Đăng xuất khỏi Firebase, Facebook và Google -----
    suspend fun dangXuat(context: Context) {
        auth.signOut()
        try {
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
            // Facebook chưa cấu hình thì bỏ qua
        }
        try {
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            // Không có gì để xóa
        }
    }
}