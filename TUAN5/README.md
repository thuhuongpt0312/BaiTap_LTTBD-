# Bài tập tuần 5 - Giới thiệu về Firebase

- **Họ tên:** Phan Thị Thu Hương
- **MSSV:** 067306005888
- **Lớp:** 012012103402

## Mục tiêu

- **BT1:** Thực hành Firebase Authentication: dựng màn hình đăng nhập và hiển thị kết quả (Google, Facebook, Email, Số điện thoại, ...).
- **BT2:** Tích hợp các dịch vụ của Firebase vào ứng dụng UTH Todo.

## Công nghệ

Kotlin, Jetpack Compose, Navigation Compose, Firebase (BoM 34.19.0), Credential Manager, Facebook Login SDK, Coil.

## BT1 - Firebase Authentication

Luồng ứng dụng:

```
Đăng nhập --(thành công)--> Trang chủ --(bấm avatar hoặc Cài đặt)--> Hồ sơ --(Đăng xuất)--> Đăng nhập
```

| Tình huống | Kết quả |
|---|---|
| Bấm đăng nhập bị lỗi (sai mật khẩu, thiếu thông tin, ...) | Hiện thông báo lỗi (Toast "Đăng nhập lỗi: ...") |
| Bấm đăng nhập thành công | Hiện thông báo thành công và vào Trang chủ |
| Từ Trang chủ bấm vào ảnh đại diện | Vào màn hình thông tin cá nhân |
| Mở lại app khi đã đăng nhập | Vào thẳng Trang chủ |

5 cách đăng nhập đã kết nối:

| Cách đăng nhập | Cách làm |
|---|---|
| **Google** | Credential Manager lấy ID token, đổi sang Firebase bằng `GoogleAuthProvider` |
| **Facebook** | Facebook SDK lấy access token, đổi sang Firebase bằng `FacebookAuthProvider` |
| **Email / mật khẩu** | `signInWithEmailAndPassword`, `createUserWithEmailAndPassword` |
| **Số điện thoại** | Gửi mã SMS (`PhoneAuthProvider.verifyPhoneNumber`), nhập mã để xác nhận |
| **Dùng thử không cần tài khoản** | `signInAnonymously` |

## BT2 - Các dịch vụ Firebase đã tích hợp

| Dịch vụ | Dùng để làm gì | File |
|---|---|---|
| **Authentication** | 5 cách đăng nhập ở trên | `DichVuXacThuc.kt` |
| **Realtime Database** | Lưu công việc của từng người dùng (thêm, tích xong, xóa), tự cập nhật theo thời gian thực | `DuLieu.kt` (`KhoCongViec`) |
| **Storage** | Tải ảnh đại diện lên `avatars/<uid>.jpg` | `DuLieu.kt` (`DichVuAnh`) |
| **Cloud Messaging** | Nhận thông báo đẩy, hiển thị mã FCM trong Hồ sơ để gửi thử | `ThongBaoService.kt` |
| **Remote Config** | Đổi lời động viên và bật/tắt nút Facebook từ xa | `DuLieu.kt` (`CauHinhTuXa`) |
| **Analytics** | Ghi sự kiện đăng nhập, thêm việc, đổi ảnh | `DuLieu.kt` (`PhanTich`) |
| **Crashlytics** | Nút "Gây lỗi thử" trong Hồ sơ, báo cáo lỗi lên console | `ManHinhHoSo.kt` |
| **Hosting** | Đăng trang giới thiệu app (làm trên máy, xem mục bên dưới) | không có code trong app |
| **Test Lab** | Chạy thử app trên thiết bị ảo của Google (làm trên console) | không có code trong app |

Không tích hợp: Dynamic Links (Google đã ngừng), Invites và App Indexing (dịch vụ cũ, không còn được hỗ trợ), AdMob và AdWords (thuộc mảng quảng cáo).

## Cấu trúc code

```
SourceCode/
├── MainActivity.kt          (điều hướng, tải Remote Config)
├── DichVuXacThuc.kt         (Authentication)
├── DuLieu.kt                (Realtime Database, Remote Config, Analytics, Storage)
├── ThongBaoService.kt       (Cloud Messaging)
├── ManHinhDangNhap.kt
├── ManHinhTrangChu.kt
└── ManHinhHoSo.kt
```

## Cài đặt

### 1. Tạo project Firebase

1. Vào https://console.firebase.google.com, tạo project (bật Google Analytics).
2. Thêm ứng dụng Android, **package name phải đúng với project** (`com.example.tuan_5`).
3. Lấy SHA-1: trong Android Studio mở tab **Gradle** (bên phải) → `Tasks` → `android` → `signingReport`, copy dòng `SHA1` của biến thể `debug`. Dán vào mục **SHA certificate fingerprints** trong Project settings.
4. Tải `google-services.json` và đặt vào thư mục `app/` (chuyển cây thư mục sang chế độ **Project** để thấy).
5. Bật các dịch vụ (xem mục 2). **Sau khi bật Google và thêm SHA-1, tải lại `google-services.json` và thay file cũ**, nếu không Google Sign-In báo thiếu `default_web_client_id`.

### 2. Bật dịch vụ trong console

| Mục trong console | Việc cần làm |
|---|---|
| **Authentication → Sign-in method** | Bật **Email/Password**, **Google**, **Phone**, **Anonymous**, **Facebook** (cần App ID và App Secret, xem mục 6) |
| **Authentication → Phone** | Thêm số thử nghiệm, ví dụ `+84 912345678` với mã `123456` (khỏi tốn SMS thật) |
| **Realtime Database** | Create database, chọn Locked mode, rồi dán Rules bên dưới |
| **Storage** | Get started (có thể yêu cầu nâng lên gói Blaze), rồi dán Rules bên dưới |
| **Remote Config** | Tạo tham số `loi_dong_vien` (String) và `hien_facebook` (Boolean), bấm **Publish changes** |
| **Crashlytics** | Bấm Enable |

Rules cho Realtime Database:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null && auth.uid === $uid",
        ".write": "auth != null && auth.uid === $uid"
      }
    }
  }
}
```

Rules cho Storage:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /avatars/{tenFile} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && tenFile == request.auth.uid + '.jpg';
    }
  }
}
```

### 3. Gradle

`build.gradle.kts` (cấp project), thêm vào khối `plugins`:

```kotlin
id("com.google.gms.google-services") version "4.5.0" apply false
id("com.google.firebase.crashlytics") version "3.0.8" apply false
```

`app/build.gradle.kts`, thêm vào khối `plugins`:

```kotlin
id("com.google.gms.google-services")
id("com.google.firebase.crashlytics")
```

và thêm vào khối `dependencies`, rồi bấm **Sync Now**:

```kotlin
implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-database")
implementation("com.google.firebase:firebase-storage")
implementation("com.google.firebase:firebase-messaging")
implementation("com.google.firebase:firebase-config")
implementation("com.google.firebase:firebase-analytics")
implementation("com.google.firebase:firebase-crashlytics")

implementation("androidx.credentials:credentials:1.3.0")
implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
implementation("com.facebook.android:facebook-login:18.0.3")
implementation("androidx.navigation:navigation-compose:2.8.9")
implementation("io.coil-kt:coil-compose:2.7.0")
```

### 4. AndroidManifest.xml

Thêm 2 quyền ngay trên thẻ `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Thêm vào **bên trong** thẻ `<application>` (cạnh `<activity android:name=".MainActivity" ...>`):

```xml
<meta-data android:name="com.facebook.sdk.ApplicationId" android:value="@string/facebook_app_id" />
<meta-data android:name="com.facebook.sdk.ClientToken" android:value="@string/facebook_client_token" />

<service android:name=".ThongBaoService" android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>

<activity
    android:name="com.facebook.FacebookActivity"
    android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
    android:label="@string/app_name" />
<activity android:name="com.facebook.CustomTabActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="@string/fb_login_protocol_scheme" />
    </intent-filter>
</activity>
```

### 5. res/values/strings.xml

Thêm 3 dòng (thay bằng giá trị thật ở mục 6):

```xml
<string name="facebook_app_id">123456789012345</string>
<string name="fb_login_protocol_scheme">fb123456789012345</string>
<string name="facebook_client_token">thay_bang_client_token</string>
```

`fb_login_protocol_scheme` luôn là chữ `fb` ghép với App ID.

### 6. Facebook

1. Vào https://developers.facebook.com → **My Apps** → **Create App**, chọn trường hợp sử dụng **Facebook Login**.
2. **App settings → Basic:** lấy **App ID** và **App Secret**. **App settings → Advanced:** lấy **Client token**.
3. Dán App ID và App Secret vào Firebase (**Authentication → Facebook**). Copy **OAuth redirect URI** mà Firebase hiển thị, dán vào Facebook Login → Settings → **Valid OAuth Redirect URIs**.
4. Thêm nền tảng Android: package `com.example.tuan_5`, class `com.example.tuan_5.MainActivity`, và **Key hashes** của máy bạn. Nếu chưa có key hash, cứ thử đăng nhập một lần, thông báo lỗi của Facebook sẽ in ra đúng hash cần thêm.
5. App ở chế độ Development nên chỉ tài khoản Facebook của người tạo app (hoặc người được thêm vào Roles) mới đăng nhập được.

## Kiểm thử

| Chức năng | Cách thử |
|---|---|
| Đăng nhập lỗi | Nhập email, mật khẩu sai rồi bấm **Đăng nhập** → hiện thông báo lỗi |
| Đăng ký, đăng nhập Email | Nhập email, mật khẩu từ 6 ký tự → **Đăng ký**, đăng xuất rồi **Đăng nhập** lại |
| Google | Máy ảo phải có tài khoản Google (Settings → Passwords & accounts) |
| Số điện thoại | Dùng số thử nghiệm đã thêm trong console, nhập mã `123456` |
| Dùng thử | Bấm **Dùng thử không cần tài khoản** → Hồ sơ ghi "Tài khoản dùng thử" |
| Realtime Database | Bấm **+** thêm việc, tích xong, xóa; mở Firebase console thấy dữ liệu đổi theo |
| Storage | Hồ sơ → **Đổi ảnh đại diện** |
| Cloud Messaging | Hồ sơ → **Sao chép mã FCM**. Console → Messaging → **Send test message**, dán mã. Hoặc gửi đến chủ đề `uth_todo` |
| Remote Config | Sửa `loi_dong_vien` trong console, Publish, mở lại app (đặt `hien_facebook` = false để ẩn nút Facebook) |
| Crashlytics | Hồ sơ → **Gây lỗi thử**, mở lại app, đợi vài phút rồi xem trên console |
| Analytics | Console → Analytics → DebugView, hoặc xem sau vài giờ |

## Hosting

Chạy trên máy (cần cài Node.js):

```
npm install -g firebase-tools
firebase login
firebase init hosting
firebase deploy --only hosting
```

Khi `init`, chọn project vừa tạo, thư mục public là `public`. Sửa `public/index.html` thành trang giới thiệu UTH Todo trước khi deploy. Console sẽ cấp địa chỉ dạng `https://<project-id>.web.app`.

## Test Lab

Trong Android Studio chọn **Build → Build APK(s)**. Trong console vào **Test Lab → Run a test → Robo test**, tải file APK debug lên, chọn thiết bị ảo rồi **Start**. Kết quả có ảnh chụp màn hình và video.

## Lưu ý bảo mật

- **Không đưa lên GitHub** các file `google-services.json`, App Secret và Client token của Facebook.
- Repo chỉ chứa file code trong `SourceCode/`. Ai muốn chạy lại cần tạo project Firebase của riêng mình theo hướng dẫn trên.
- Khung xác thực trong bài chỉ để học, Rules của database và storage đã giới hạn mỗi người chỉ đọc và ghi dữ liệu của chính mình.
