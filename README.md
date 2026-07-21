# YouTube Viewer - App xem YouTube trên Android 7+

## Giới thiệu
App Android native (Java) để xem video YouTube, chạy được trên Android 7 (API 24) trở lên. Sử dụng YouTube Data API v3 và ExoPlayer để phát video.

## Tính năng
- Tìm kiếm video YouTube
- Xem video trending/phổ biến
- Hiển thị thông tin video (tiêu đề, kênh, lượt xem)
- Phát video mượt mà với ExoPlayer
- Hỗ trợ phát nền (background playback)
- Giao diện Material Design

## Yêu cầu
- Android Studio Arctic Fox trở lên
- JDK 8+
- YouTube Data API Key

## Cách cài đặt

### 1. Lấy YouTube API Key
1. Vào [Google Cloud Console](https://console.cloud.google.com)
2. Tạo project mới hoặc chọn project có sẵn
3. Vào APIs & Services > Library
4. Tìm và bật "YouTube Data API v3"
5. Vào APIs & Services > Credentials
6. Tạo API Key
7. Copy API Key để sử dụng

### 2. Build và cài app
1. Mở thư mục `YouTubeViewer` trong Android Studio
2. Chờ Android Studio sync Gradle
3. Kết nối thiết bị Android hoặc chạy emulator
4. nhấn Run (▶) để build và cài app
5. Khi mở app lần đầu, nhập API Key đã lấy

### 3. Cài đặt trên thiết bị Android 7
1. Vào Settings > Security
2. Bật "Unknown sources" (Cho phép cài từ nguồn không xác định)
3. Copy file APK từ thư mục `app/build/outputs/apk/debug/`
4. Mở file APK trên thiết bị để cài đặt

## Cấu trúc dự án
```
YouTubeViewer/
├── app/
│   ├── src/main/
│   │   ├── java/com/youtubeviewer/
│   │   │   ├── MainActivity.java          # Màn hình chính
│   │   │   ├── PlayerActivity.java        # Màn hình phát video
│   │   │   ├── adapter/
│   │   │   │   └── VideoAdapter.java      # Adapter cho RecyclerView
│   │   │   ├── api/
│   │   │   │   └── YouTubeApiHelper.java  # Helper gọi YouTube API
│   │   │   ├── model/
│   │   │   │   └── Video.java             # Model dữ liệu video
│   │   │   └── service/
│   │   │       └── BackgroundPlayerService.java  # Service phát nền
│   │   ├── res/
│   │   │   ├── layout/                    # Các layout XML
│   │   │   ├── drawable/                  # Icons, backgrounds
│   │   │   ├── menu/                      # Menu navigation
│   │   │   ├── values/                    # Colors, strings, themes
│   │   │   └── xml/                       # Network security config
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Sử dụng
1. Mở app
2. Nhập API Key khi được yêu cầu
3. Dùng thanh tìm kiếm hoặc chọn tab "Xu hướng"
4. Nhấn vào video để xem
5. Sử dụng nút "Tải video" để mở video trong trình duyệt

## Ghi chú
- API Key chỉ dùng nội bộ, không chia sẻ công khai
- YouTube API có giới hạn 10,000 đơn vị/ngày
- Một số tính năng như đăng ký kênh sẽ được bổ sung phiên bản sau

## License
MIT License
