# Bài tập tuần 4 - Lập trình thiết bị di động

- **Họ tên:** Phan Thị Thu Hương
- **MSSV:** 067306005888
- **Lớp:** 012012103402

Mục tiêu
BT1: Thực hành Navigation + OOP, mọi dữ liệu đều dùng OOP.
BT2: Xây dựng hệ thống quản lý thư viện, có đủ 4 đặc trưng của OOP.
BT3: Hiểu luồng dữ liệu giữa các màn hình, biết truyền dữ liệu từ màn hình này sang màn hình khác.
Công nghệ

Kotlin, Jetpack Compose (Material 3), Navigation Compose.

Cấu trúc code
SourceCode/
├── MainActivity.kt        (thanh TabRow chuyển giữa 3 bài)
├── logo_full.png          (logo UTH, đặt trong res/drawable)
├── bt01/                  (UTH Todo V2)
├── bt02/                  (Quản lý Thư viện)
└── bt03/                  (SmartTasks - quên mật khẩu)
Chuyển đổi giữa 3 bài tập

MainActivity.kt có thanh TabRow ở trên cùng. Bấm vào tab để chuyển nhanh giữa 3 bài, không cần sửa code hay bật/tắt dòng //:

Tab	Bài	Nội dung
BT 1	UTH Todo	Splash → Giới thiệu 3 bước → Danh sách công việc
BT 2	Thư viện	Quản lý mượn/trả sách, hiển thị danh sách sách và sinh viên
BT 3	SmartTasks	Truyền dữ liệu giữa các màn hình bằng Navigation, có logo UTH
BT1 - UTH Todo V2 (Navigation + OOP)

Luồng: Splash → Giới thiệu bước 1 → bước 2 → bước 3 → Trang chủ. Bấm Bỏ qua thì vào thẳng Trang chủ.

File	Nội dung
DuLieuTodo.kt	Các class ThongTinUngDung, BuocGioiThieu, CongViec và object DuLieuTodo chứa toàn bộ dữ liệu
BT01App.kt	NavHost khai báo các đường đi splash, gioithieu/{buoc}, trangchu
ManHinhSplash.kt	Màn hình xanh, tự chuyển sau 2 giây (LaunchedEffect + delay)
ManHinhGioiThieu.kt	Màn hình giới thiệu, nội dung lấy từ đối tượng BuocGioiThieu
ManHinhTrangChu.kt	Danh sách công việc, lấy từ các đối tượng CongViec

Dữ liệu (tên app, khẩu hiệu, phiên bản, tiêu đề từng bước, công việc) đều là thuộc tính của các đối tượng, không viết cứng trong giao diện.

BT2 - Quản lý Thư viện (4 đặc trưng OOP)
Đặc trưng	Thể hiện trong code (ThuVien.kt)
Trừu tượng	abstract class Sach có hàm trừu tượng loaiSach(), không tạo trực tiếp được
Đóng gói	nguoiMuon có private set, chỉ đổi qua choMuon() / nhanTra(); SinhVien giữ danh sách mượn ở private val dsMuon, giới hạn tối đa 3 cuốn
Kế thừa	SachGiaoTrinh và SachThamKhao kế thừa Sach; SinhVien kế thừa NguoiDung
Đa hình	Ghi đè thongTin(), loaiSach(), vaiTro(): cùng gọi sach.thongTin() nhưng mỗi loại sách cho kết quả khác nhau

Giao diện có 3 tab:

Quản lý: đổi sinh viên (nút Thay đổi), tích chọn để mượn / trả sách, nút Thêm để thêm sách mới.
DS Sách: thông tin từng cuốn và trạng thái (còn / đang được ai mượn).
Sinh viên: danh sách sinh viên và sách đang mượn.
BT3 - SmartTasks (Data Flow Navigation)
Màn 1 --email--> Màn 2 --email, otp--> Màn 3 --email, otp, pass--> Màn 4
  ^                                                                   |
  +------------------ email, otp, pass (bấm Summit) -----------------+
Dữ liệu được truyền qua tham số của đường dẫn (navigate("xacthuc/" + email)) và đọc lại bằng entry.arguments?.getString(...).
Uri.encode() mã hóa các ký tự đặc biệt (như @, /) trước khi đưa vào đường dẫn.
Màn 4 bấm Summit thì quay về màn 1 kèm dữ liệu (popUpTo xóa các màn hình ở giữa), màn 1 hiện dữ liệu trong khung xanh.
Logo UTH hiển thị bằng ảnh logo_full.png (đặt trong res/drawable), vẽ bằng Image(painterResource(id = R.drawable.logo_full), ...) trong hàm LogoSmartTasks() ở ThanhPhanChung.kt. Logo xuất hiện ở cả 4 màn hình của BT3.
Kiểm tra dữ liệu: email phải có @, mã OTP đủ 5 số, mật khẩu từ 6 ký tự và nhập lại phải khớp.
Kết quả

Ảnh chụp màn hình đặt trong thư mục TaiLieu/:

markdown
![BT1 - Splash](TaiLieu/bt1_splash.png)
![BT2 - Quản lý](TaiLieu/bt2_quanly.png)
![BT3 - Confirm](TaiLieu/bt3_confirm.png)
Cách chạy
Tạo project Empty Activity, package com.example.tuan_4.
Trong app/build.gradle.kts, thêm vào khối dependencies rồi bấm Sync Now:
kotlin
   implementation("androidx.navigation:navigation-compose:2.8.9")
Chép ảnh logo_full.png vào app/src/main/res/drawable/.
Tạo 3 package con bt01, bt02, bt03 trong com.example.tuan_4, chép file tương ứng vào từng package. Thay MainActivity.kt cũ.
Bấm Run (▶), rồi dùng thanh TabRow ở trên cùng để chuyển giữa BT 1, BT 2, BT 3.