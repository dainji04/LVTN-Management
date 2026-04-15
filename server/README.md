# LVTN-Management Backend

## 📋 Mô tả dự án

Đây là backend của ứng dụng mạng xã hội LVTN-Management, được xây dựng bằng Spring Boot. Dự án cung cấp các API RESTful cho các chức năng như đăng nhập, quản lý bài viết, bình luận, kết bạn, thông báo realtime, và chat.

## 🚀 Công nghệ sử dụng

- **Java**: 21
- **Spring Boot**: 3.3.5
- **Database**: MySQL (Aiven Cloud)
- **Authentication**: JWT (Nimbus JOSE JWT)
- **Security**: Spring Security
- **API Documentation**: Swagger/OpenAPI (SpringDoc)
- **Mapping**: MapStruct
- **Utilities**: Lombok
- **Build Tool**: Maven

## 📁 Cấu trúc dự án

```
server/
├── src/
│   ├── main/
│   │   ├── java/group_10/group/_0/
│   │   │   ├── Group10Application.java          # Main application class
│   │   │   ├── configuration/                   # Configuration classes (CORS, Security)
│   │   │   ├── controller/                      # REST controllers
│   │   │   │   ├── AuthenticationController.java
│   │   │   │   ├── BaiVietController.java       # Posts management
│   │   │   │   ├── BinhLuanController.java      # Comments management
│   │   │   │   ├── ChatProxyController.java     # Chat proxy
│   │   │   │   ├── LoiMoiKetBanController.java  # Friend requests
│   │   │   │   ├── LuotThichController.java     # Likes
│   │   │   │   ├── QuanHeBanBeController.java   # Friendships
│   │   │   │   ├── TheoDoiController.java       # Follows
│   │   │   │   ├── ThongBaoController.java      # Notifications
│   │   │   │   ├── ThongTinUserController.java  # User info
│   │   │   │   └── UsersController.java         # Users management
│   │   │   ├── dto/                             # Data Transfer Objects
│   │   │   │   ├── request/                     # Request DTOs
│   │   │   │   └── response/                    # Response DTOs
│   │   │   ├── entity/                          # JPA Entities
│   │   │   ├── Enum/                            # Enums
│   │   │   ├── exception/                       # Custom exceptions
│   │   │   ├── mapper/                          # MapStruct mappers
│   │   │   ├── repository/                      # JPA Repositories
│   │   │   └── service/                         # Business logic services
│   │   └── resources/
│   │       ├── application.yaml                 # Application configuration
│   │       └── static/                          # Static resources
│   └── test/
│       └── java/group_10/group/_0/
│           └── Group10ApplicationTests.java     # Unit tests
├── target/                                      # Build output
├── Dockerfile                                   # Docker configuration
├── pom.xml                                      # Maven configuration
├── mvnw & mvnw.cmd                              # Maven wrapper
└── FLOW_DOCUMENT.md                             # Application flow documentation
```

## ⚙️ Cài đặt và chạy

### Yêu cầu hệ thống

- Java 21 hoặc cao hơn
- Maven 3.6+
- MySQL Database (hoặc sử dụng Aiven Cloud như cấu hình hiện tại)

### Các bước cài đặt

1. **Clone repository:**
   ```bash
   git clone <repository-url>
   cd LVTN-Management/server
   ```

2. **Cấu hình database:**
   - Cập nhật thông tin database trong `src/main/resources/application.yaml`
   - Hoặc sử dụng biến môi trường: `DB_PASSWORD`, `SIGNER_JWT`

3. **Cài đặt dependencies:**
   ```bash
   ./mvnw clean install
   ```

4. **Chạy ứng dụng:**
   ```bash
   ./mvnw spring-boot:run
   ```

Ứng dụng sẽ chạy trên `http://localhost:8080`

### Chạy với Docker

```bash
docker build -t lvtn-backend .
docker run -p 8080:8080 lvtn-backend
```

## 📚 API Documentation

API documentation được cung cấp qua Swagger UI:

- **Swagger UI**: `http://localhost:8080/swagger-ui`
- **API Docs (JSON)**: `http://localhost:8080/api-docs`

## 🔄 Luồng ứng dụng

Chi tiết về luồng các chức năng của ứng dụng được mô tả trong [FLOW_DOCUMENT.md](./FLOW_DOCUMENT.md)

### Các luồng chính:

- **Đăng nhập**: Xác thực người dùng và tạo JWT token
- **Thông báo realtime**: Sử dụng Server-Sent Events (SSE) để push thông báo
- **Kết bạn**: Gửi và chấp nhận lời mời kết bạn
- **Bài viết**: Tạo, chỉnh sửa, xóa bài viết
- **Bình luận**: Quản lý bình luận trên bài viết
- **Chat**: Proxy cho chức năng chat (kết nối với Node.js server)

## 🔐 Authentication

Ứng dụng sử dụng JWT cho authentication:

- **Signer Key**: Cấu hình trong `application.yaml`
- **Token Duration**: 1 ngày (86400 giây)
- **Refresh Duration**: 100 giờ (360000 giây)

## 🗄️ Database

- **Dialect**: MySQL
- **Naming Strategy**: PhysicalNamingStrategyStandardImpl
- **DDL Auto**: validate (sử dụng schema được tạo sẵn)

## 🤝 Đóng góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📄 License

Dự án này sử dụng giấy phép [MIT](LICENSE).

## 📞 Liên hệ

- **Tên dự án**: LVTN-Management
- **Nhóm**: Group 10
- **Mô tả**: Project for Social Media</content>
<parameter name="filePath">D:\Dev\Nam_4_HK2\LVTN-Management\server\README.md
