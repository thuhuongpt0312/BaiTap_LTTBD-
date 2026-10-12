# Bài tập tuần 2 - Lập trình thiết bị di động

- Họ tên: Phan Thị Thu Hương
- MSSV: 067306005888
- Lớp: 012012103402

## Công nghệ

- Ngôn ngữ: Kotlin
- Giao diện: Jetpack Compose (Material 3)
- Công cụ: Android Studio

## Danh sách bài

| File | Bài | Nội dung | Kiến thức |
|---|---|---|---|
| `BT01.kt` | Thực hành 3 | Máy tính đơn giản: nhập 2 số, chọn phép toán `+ - * /`, hiện kết quả | TextField, state, xử lý sự kiện |
| `BT02.kt` | Bài 1 | Danh sách sinh viên, hiện "Chưa cập nhật" nếu thiếu email/SĐT | `for`, kiểu nullable `?`, toán tử `?:` |
| `BT03.kt` | Bài 2 | Xếp loại học lực từ điểm số | `if / else`, hàm trả về giá trị |
| `BT04.kt` | Bài 3 | Bảng điểm lớp: trung bình, số sinh viên Giỏi, chưa có điểm, điểm cao nhất | `for` + `if` + nullable |
| `MainActivity.kt` | | Chọn bài để chạy | |

## Kết quả mong đợi

- **BT01:** nhập 4 và 4, bấm `+` thì hiện `Kết quả: 8`. Chia cho 0 thì báo lỗi.
- **BT02:** 5 sinh viên, 2 người thiếu email. Cuối danh sách hiện `Tổng: 5 sinh viên` và `Thiếu email: 2`.
- **BT03:** điểm 8.5 xếp loại Giỏi. Kiểm tra thêm: 9 Giỏi, 7 Khá, 5.5 Trung bình, 3 Yếu, 11 Không hợp lệ.
- **BT04:** điểm trung bình lớp 6.63, 1 Giỏi, 1 chưa có điểm, cao nhất 9.0.

## Cách chạy

1. Tạo project **Empty Activity** trong Android Studio, đặt package là `com.example.tuan_2`.
2. Chép các file `BT01.kt` đến `BT04.kt` và `MainActivity.kt` vào thư mục `app/src/main/java/com/example/tuan_2/`.
3. Trong `MainActivity.kt`, chỉ để lại một dòng gọi bài muốn chạy (bỏ `//` dòng đó, các dòng còn lại thêm `//`).
4. Bấm nút Run (▶) để chạy trên emulator.

> `BT04.kt` dùng lại hàm `xepLoai()` và `mauChu()` trong `BT03.kt`, nên cần để cả hai file trong project.
