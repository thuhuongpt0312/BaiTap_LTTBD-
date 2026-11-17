# Bài tập E-learning – Lập trình thiết bị di động

## Thông tin sinh viên

| Thông tin | Nội dung |
|---|---|
| Họ và tên | Phan Thị Thu Hương |
| MSSV | 067306005888 |
| Lớp | 012012103402 |

## Đề tài

**Dãy Fibonacci và Tỉ lệ vàng** (Kotlin from Scratch – Part II – Chapter 4 – Project 13), phiên bản Android dùng Kotlin và Jetpack Compose.

## Chức năng

- Màn hình danh sách (`LazyColumn`): hiển thị F(n) và tỉ số F(n) / F(n−1), chọn N từ 3 đến 20 bằng thanh trượt.
- Màn hình xoắn ốc (`Canvas`): vẽ các ô vuông cạnh Fibonacci và đường xoắn ốc từ các cung 1/4 đường tròn.
- Điều hướng giữa hai màn hình bằng Navigation Compose.

## Cấu trúc thư mục

```
E-learning/
├── SourceCode/
│   └── MainActivity.kt   # Toàn bộ mã nguồn ứng dụng
├── Slide/                # Slide thuyết trình
└── README.md
```

## Cách chạy

1. Tạo project **Empty Activity (Compose)** trong Android Studio, package `com.example.fibonacci`.
2. Chép `SourceCode/MainActivity.kt` thay cho file `MainActivity.kt` của project.
3. Thêm vào `build.gradle.kts` (module app) rồi Sync:
   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.8.5")
   implementation("androidx.compose.material:material-icons-core:1.7.5")
   ```
4. Chạy ứng dụng trên máy ảo hoặc điện thoại thật.
