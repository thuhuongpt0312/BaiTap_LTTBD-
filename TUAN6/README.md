# Bài tập tuần 6 - UTH SmartTasks (UI + API)

- **Họ tên:** Phan Thị Thu Hương
- **MSSV:** 0673060058888
- **Lớp:** 012012103402

## Mục tiêu

- Tiếp tục phát triển ứng dụng **UTH SmartTasks**: dựng 3 flow **Trang chủ**, **Danh sách**, **Chi tiết** (UI + API).
- **Nâng cao 1:** xử lý quyền (Location, Notification, Camera).
- **Nâng cao 2:** concurrency | asynchronous với Coroutines + Retrofit (dữ liệu crypto).

## Công nghệ

Kotlin, Jetpack Compose, Navigation Compose, ViewModel, Retrofit + Gson, Coroutines.

## Cấu trúc code

```
SourceCode/
├── MainActivity.kt          (thanh TabRow chuyển giữa bài chính và 2 bài nâng cao)
├── ApiTask.kt               (mô hình dữ liệu + Retrofit)
├── TaskViewModel.kt         (dữ liệu và các thao tác gọi API)
├── ThanhPhanChung.kt        (hàm định dạng, thành phần giao diện dùng chung)
├── SmartTasksApp.kt         (điều hướng)
├── ManHinhTrangChu.kt       (flow Trang chủ)
├── ManHinhDanhSach.kt       (flow Danh sách)
├── ManHinhChiTiet.kt        (flow Chi tiết)
├── NangCao01Quyen.kt        (nâng cao 1)
└── NangCao02Crypto.kt       (nâng cao 2)
```

## Bài chính: 3 flow

API: `https://share.tryapps.store/` (mock). Mọi lệnh đều gửi header `Authorization: Bearer <token>`.

| Flow | Màn hình | API | Hành vi |
|---|---|---|---|
| **Trang chủ** | Tiến độ hôm nay (2/4 việc, 50%), nhóm Học tập / Cuộc sống, danh sách việc hôm nay | `GET /mock/teach/tasks`, `PATCH /mock/teach/tasks/{id}` | Tải khi mở màn hình, có trạng thái đang tải, lỗi và nút **Thử lại**. Tick xong thì cập nhật tiến độ ngay |
| **Danh sách** | Tìm kiếm, lọc Tất cả / Học tập / Cuộc sống, chia **Cần làm** và **Đã xong** | `GET /mock/teach/tasks`, `PATCH /mock/teach/tasks/{id}` | Lọc và tìm trên dữ liệu đã tải, tick để chuyển giữa 2 nhóm |
| **Chi tiết** | Hạn chót, nhắc nhở, ghi chú, việc nhỏ (có thanh tiến độ), nút hoàn thành, xóa | `GET`, `PATCH`, `DELETE /mock/teach/tasks/{id}` | Sửa tiêu đề và ghi chú, thêm, sửa, xóa việc nhỏ, đánh dấu hoàn thành, xóa công việc |

Luồng điều hướng: `Trang chủ ⇄ Danh sách → Chi tiết → quay lại`. Thanh dưới có Trang chủ, Danh sách, Thông báo, Cài đặt (2 mục sau chưa làm) và nút **+** thêm việc.

### Giải thích các hàm chính

| Hàm / lớp | Làm gì |
|---|---|
| `TaskApi` | Khai báo 4 lệnh gọi API bằng Retrofit (`@GET`, `@PATCH`, `@DELETE`), đều là hàm `suspend` |
| `DichVuApi` | Tạo Retrofit, tự gắn header `Authorization` cho mọi lệnh |
| `TaskViewModel.taiDanhSach()` | Chạy `viewModelScope.launch` gọi `GET`, lưu danh sách, bắt lỗi mạng |
| `TaskViewModel.doiTrangThai()` | Cập nhật trên màn hình ngay rồi gửi `PATCH` |
| `TaskViewModel.xoaCongViec()` | Xóa trong danh sách rồi gửi `DELETE` |
| `nhanNgay()` | Đổi `2026-10-20` + `14:00` thành "Hôm nay, 14:00", "Ngày mai", "Tuần này" |
| `HangCongViec()` | Một dòng công việc dùng chung cho Trang chủ và Danh sách |

### Về API mẫu

API này là **mock**: luôn trả về cùng một dữ liệu mẫu, bất kể gọi gì (đúng như ghi chú trên trang "The mock answers with the same sample whatever the query or body"). Vì vậy:

- App giữ danh sách trong `ViewModel` và coi đó là dữ liệu chuẩn. Tick xong, sửa, xóa đều cập nhật ngay trên màn hình, đồng thời vẫn gửi `PATCH` / `DELETE` lên máy chủ.
- `GET /tasks/{id}` luôn trả về cùng một việc, nên màn hình Chi tiết chỉ dùng kết quả khi việc đó chưa có trong danh sách.
- Dữ liệu mẫu cố định ngày **20/10/2026**, nên app lấy ngày này làm "hôm nay" (hằng số `NGAY_HOM_NAY`).
- Nút **+** thêm việc chỉ lưu trong bộ nhớ vì chưa có API thêm mới trong đề.

## Nâng cao 1: xử lý quyền

`NangCao01Quyen.kt` hỏi lần lượt 3 quyền bằng thẻ giống mẫu: **Location**, **Notification**, **Camera**, mỗi thẻ có nút **Allow / Turn on** và **Skip for now**.

- Bấm **Allow / Turn on** thì mở hộp thoại xin quyền thật của Android (`ActivityResultContracts.RequestPermission`).
- Hỏi xong hiện bảng **Kết quả xin quyền** (đã cấp / chưa cấp, đọc bằng `checkSelfPermission`).
- Quyền bị từ chối nhiều lần thì hệ thống không hiện hộp thoại nữa, nên có nút **Mở cài đặt ứng dụng** để người dùng tự bật.
- Android dưới 13 không cần xin quyền thông báo nên bước này được bỏ qua.

## Nâng cao 2: concurrency | asynchronous

`NangCao02Crypto.kt` dựng màn hình **Home** giống mẫu (News, Favorites, All Fluctuations) với dữ liệu thật, tự làm mới 30 giây một lần.

### Các API crypto công khai, không cần khóa

| API | Ví dụ gọi | Dữ liệu |
|---|---|---|
| **CoinGecko** | `https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=bitcoin&sparkline=true` | Giá, % thay đổi 24h, đường giá 7 ngày (app dùng) |
| **Binance** | `https://api.binance.com/api/v3/ticker/24hr?symbol=BTCUSDT` | Giá, % thay đổi trên sàn (app dùng) |
| **CryptoCompare** | `https://min-api.cryptocompare.com/data/v2/news/?lang=EN` | Tin tức crypto (app dùng). Giá: `/data/pricemultifull?fsyms=BTC&tsyms=USD` |
| **Kraken** | `https://api.kraken.com/0/public/Ticker?pair=XBTUSD` | Giá, mua/bán |

Muốn giá thật sự theo từng giây thì dùng WebSocket, ví dụ Binance: `wss://stream.binance.com:9443/ws/btcusdt@ticker`.

### Nghiên cứu

| Khái niệm | Ý nghĩa |
|---|---|
| **Asynchronous (bất đồng bộ)** | Làm việc lâu (gọi mạng) mà **không chặn** luồng giao diện. Trong code: hàm `suspend` + Retrofit, màn hình vẫn mượt trong lúc chờ dữ liệu |
| **Concurrency (đồng thời)** | Nhiều việc cùng được xử lý xen kẽ hoặc song song để tiết kiệm thời gian. Trong code: `async` / `await` gọi 3 API cùng lúc |
| **Coroutine** | Luồng xử lý nhẹ của Kotlin, có thể tạm dừng (`suspend`) và chạy tiếp mà không chặn luồng chính |
| `launch` | Chạy một việc, không cần kết quả trả về |
| `async` / `await` | Chạy một việc và lấy kết quả khi cần |
| `coroutineScope` | Gom các coroutine con, đợi tất cả xong; một cái lỗi thì hủy cả nhóm |
| `delay` | Chờ mà không chặn luồng (khác `Thread.sleep`) |
| `LaunchedEffect` | Coroutine gắn với vòng đời của màn hình, tự hủy khi rời màn hình |

Trong `CryptoViewModel.taiDuLieu()`, 3 API chạy **song song**: tổng thời gian ≈ thời gian của API chậm nhất, không phải cộng dồn. Màn hình hiển thị thời gian tải thực tế ở cuối trang.

## Cách chạy

1. Tạo project **Empty Activity**, package `com.example.tuan_6`.
2. Trong `app/build.gradle.kts`, thêm vào khối `dependencies` rồi bấm **Sync Now**:
   ```kotlin
   implementation("com.squareup.retrofit2:retrofit:2.11.0")
   implementation("com.squareup.retrofit2:converter-gson:2.11.0")
   implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
   implementation("androidx.navigation:navigation-compose:2.8.9")
   ```
3. Trong `AndroidManifest.xml`, thêm 4 quyền ngay trên thẻ `<application>`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
   <uses-permission android:name="android.permission.CAMERA" />
   ```
4. Chép các file trong `SourceCode/` vào `app/src/main/java/com/example/tuan_6/` (thay `MainActivity.kt` cũ).
5. Trong `ApiTask.kt`, nếu máy chủ yêu cầu token thì thay chuỗi `TOKEN` bằng `MOCK_API_TOKEN` của bạn.
6. Bấm Run (▶), dùng thanh **TabRow** ở trên cùng để chuyển giữa **SmartTasks**, **Nâng cao 1** và **Nâng cao 2**.
