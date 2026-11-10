package com.example.tuan_6

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

// ===== Mô hình dữ liệu: khớp với JSON của API /mock/teach/tasks =====

// Việc nhỏ trong một công việc
data class ViecNho(
    @SerializedName(value = "title", alternate = ["name", "text", "label"])
    val title: String = "",
    @SerializedName(value = "done", alternate = ["completed", "checked"])
    val done: Boolean = false
)

// Một công việc. Mọi thuộc tính đều có giá trị mặc định để Gson đọc JSON an toàn.
data class Task(
    val id: Int = 0,
    val title: String = "",
    val note: String? = "",
    val category: String = "study",      // "study" = Học tập, "life" = Cuộc sống
    val date: String = "",               // dạng 2026-10-20
    val time: String = "",               // dạng 14:00
    val priority: String = "medium",     // low, medium, high
    val remind: Boolean = false,
    val done: Boolean = false,
    val subtasks: List<ViecNho>? = null
)

// ===== Các API dùng trong 3 flow =====
interface TaskApi {

    // Trang chủ, Danh sách: tải mọi việc khi mở màn hình
    @GET("mock/teach/tasks")
    suspend fun layDanhSach(): List<Task>

    // Chi tiết: mở khi bấm một việc
    @GET("mock/teach/tasks/{id}")
    suspend fun layChiTiet(@Path("id") id: Int): Task

    // Tick xong, sửa tiêu đề, sửa ghi chú, sửa việc nhỏ
    @PATCH("mock/teach/tasks/{id}")
    suspend fun capNhat(@Path("id") id: Int, @Body thayDoi: JsonObject): Response<Unit>

    // Xóa công việc
    @DELETE("mock/teach/tasks/{id}")
    suspend fun xoa(@Path("id") id: Int): Response<Unit>
}

object DichVuApi {
    // Giống lệnh curl trong slide: -H "Authorization: Bearer $MOCK_API_TOKEN"
    // Thay bằng token của bạn nếu máy chủ yêu cầu.
    const val TOKEN = "dien_MOCK_API_TOKEN_vao_day"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val yeuCau = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build()
            chain.proceed(yeuCau)
        }
        .build()

    val api: TaskApi = Retrofit.Builder()
        .baseUrl("https://share.tryapps.store/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TaskApi::class.java)
}
