# Bài tập tuần 3 - Lập trình thiết bị di động

- **Họ tên:** Phan Thị Thu Hương
- **MSSV:** 067306005888
- **Lớp:** 012012103402

## Mục tiêu

Xây dựng ứng dụng hiển thị đầy đủ các thành phần UI cơ bản của Jetpack Compose và điều hướng giữa các màn hình (Navigation).

## Công nghệ

- Kotlin, Jetpack Compose (Material 3)
- Navigation Compose (điều hướng giữa các màn hình)
- Coil (tải ảnh từ internet)

## Các màn hình

| Màn hình | File | Nội dung |
|---|---|---|
| Chào | `ManHinhChao.kt` | Logo, giới thiệu Jetpack Compose, nút **I'm ready** |
| Danh sách | `ManHinhDanhSach.kt` | Danh sách 19 thành phần UI chia theo 6 nhóm, bấm vào để xem chi tiết |
| Chi tiết | `ManHinhChiTiet.kt` | Minh họa từng thành phần, có nút `<` quay lại |
| Điều hướng | `MainActivity.kt` | `NavHost` nối 3 loại màn hình với nhau |

## Các thành phần UI đã làm (19)

| Nhóm | Thành phần |
|---|---|
| Display | Text, Image |
| Input | TextField, PasswordField, Toggle (Switch), Slider, DatePicker |
| Selection | Button, Checkbox, RadioButton, Picker |
| Navigation | NavigationView, TabBar, Menu |
| Content | List, Card, Divider |
| Layout | Column, Row |

## Giải thích các hàm chính

- `UngDung()`: tạo `NavController` và `NavHost`, khai báo 3 đường đi `chao`, `danhsach`, `chitiet/{ten}`.
- `ManHinhChao(onSanSang)`: màn hình đầu tiên, bấm nút thì gọi `onSanSang` để chuyển sang danh sách.
- `ManHinhDanhSach(onChon)`: dùng `for` lặp qua từng nhóm và từng thành phần, bấm vào một mục thì gọi `onChon(tên)`.
- `ManHinhChiTiet(ten, onBack)`: nhận tên thành phần, dùng `if / else` để chọn hàm minh họa tương ứng.
- Các hàm `ChiTietText()`, `ChiTietSlider()`, ... : mỗi hàm minh họa một thành phần, dùng `remember { mutableStateOf(...) }` để lưu trạng thái.

## Kết quả

Bỏ ảnh chụp màn hình vào thư mục `TaiLieu/` rồi chèn vào đây:

```markdown
![Màn hình chào](TaiLieu/chao.png)
![Danh sách](TaiLieu/danhsach.png)
![Text Detail](TaiLieu/text.png)
```

## Cách chạy

1. Tạo project **Empty Activity**, package `com.example.tuan_3`.
2. Trong `app/build.gradle.kts`, thêm vào khối `dependencies` rồi bấm **Sync Now**:
   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.8.9")
   implementation("io.coil-kt:coil-compose:2.7.0")
   ```
3. Trong `app/src/main/AndroidManifest.xml`, thêm dòng này ngay trên thẻ `<application>`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```
4. Chép các file trong `SourceCode/` vào `app/src/main/java/com/example/tuan_3/` (thay `MainActivity.kt` cũ).
5. Bấm Run (▶) để chạy trên emulator.
