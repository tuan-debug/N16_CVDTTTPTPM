# Lighting Store - Design Pattern Implementation

## Hướng dẫn chạy dự án

### Bước 1: Khởi động các dịch vụ phụ thuộc (MongoDB, Redis)
```
docker compose up
```

### Bước 2: Chạy front-end
```
cd fe 
npm i
npm run dev
```

```
cd be
npm i
npm start
```

```
cd BE_JAVA
.\mvnw clean install 
.\mvnw spring-boot:run
```

## Các Design Pattern được triển khai

### 1. Singleton Pattern
| Class | Vai trò | Mô tả |
|-------|---------|-------|
| `EmailServiceSingleton` | Singleton | Đảm bảo chỉ có một thể hiện của dịch vụ gửi email trong toàn bộ ứng dụng |

**Mục đích**: Đảm bảo một lớp chỉ có một thể hiện và cung cấp một điểm truy cập toàn cục đến thể hiện đó.

**Triển khai**: Lớp `EmailServiceSingleton` được tạo để đảm bảo quản lý tập trung việc gửi email, tiết kiệm tài nguyên hệ thống và đảm bảo nhất quán.

**Ứng dụng thực tế**: Được sử dụng để gửi thông báo xác thực đăng ký, email quên mật khẩu, và các thông báo hệ thống.

**API endpoint**: `GET /api/design-patterns/singleton`

### 2. Adapter Pattern
| Class/Interface | Vai trò | Mô tả |
|-----------------|---------|-------|
| `EmailSender` | Target Interface | Giao diện chung cho việc gửi email trong hệ thống |
| `JavamailSenderAdapter` | Adapter | Chuyển đổi giao diện JavaMail thành giao diện EmailSender |
| Thư viện JavaMail | Adaptee | Thư viện bên thứ ba cần được tích hợp vào hệ thống |

**Mục đích**: Chuyển đổi giao diện của một lớp thành một giao diện khác mà khách hàng mong muốn.

**Triển khai**: `JavamailSenderAdapter` đóng vai trò trung gian giữa code của chúng ta (`EmailSender`) và thư viện JavaMail, cho phép tích hợp liền mạch.

**Ứng dụng thực tế**: Giúp hệ thống gửi email thông báo bằng JavaMail mà không cần thay đổi code khi chuyển đổi nhà cung cấp dịch vụ email.

**API endpoint**: `POST /api/design-patterns/adapter`

### 3. Bridge Pattern
| Class/Interface | Vai trò | Mô tả |
|-----------------|---------|-------|
| `OrderStorage` | Abstraction | Giao diện dùng chung cho việc lưu trữ đơn hàng |
| `OrderService` | Refined Abstraction | Lớp sử dụng abstraction, gọi qua giao diện chung |
| `MongoOrderStorage`, `MySqlOrderStorage` | Implementor | Các cách lưu dữ liệu cụ thể (MongoDB, MySQL) |
| `StorageConfig` | Bridge Connector | Bean cấu hình chọn loại lưu trữ (Mongo hoặc MySQL) tại runtime |

**Mục đích**: Tách rời abstraction từ implementation để cả hai có thể thay đổi độc lập.

**Triển khai**: Sử dụng `OrderStorage` như một abstraction và các implementor cụ thể cho các cơ sở dữ liệu khác nhau.

**Ứng dụng thực tế**: Cho phép ứng dụng chuyển đổi linh hoạt giữa lưu trữ đơn hàng trên MongoDB và MySQL mà không ảnh hưởng đến logic kinh doanh.

**API endpoint**: `GET /api/design-patterns/bridge`

### 4. Composite Pattern
| Class/Interface | Vai trò | Mô tả |
|-----------------|---------|-------|
| `EmailComponent` | Component Interface | Giao diện chung cho các thành phần email |
| `EmailComposite` | Composite | Chứa và quản lý nhiều thành phần email con |
| `VerificationEmail` | Leaf | Email xác thực đơn giản |
| `WelcomeEmail` | Leaf | Email chào mừng đơn giản |
| `EmailService` | Client | Sử dụng các thành phần email |

**Mục đích**: Tổ chức các đối tượng thành cấu trúc cây để biểu diễn phần-toàn thể (part-whole hierarchies).

**Triển khai**: `EmailComposite` có thể chứa các email đơn hoặc nhóm email khác, cho phép xây dựng cấu trúc email phức tạp từ các thành phần đơn giản.

**Ứng dụng thực tế**: Được sử dụng để tạo chiến dịch email với nhiều loại email khác nhau (chào mừng, xác nhận) cho khách hàng mới.

**API endpoint**: `POST /api/design-patterns/composite`

### 5. Decorator Pattern
| Class/Interface | Vai trò | Mô tả |
|-----------------|---------|-------|
| `OrderPriceCalculator` | Component Interface | Giao diện chung cho mọi cách tính giá |
| `BasicOrderPriceCalculator` | Concrete Component | Tính giá cơ bản từ sản phẩm |
| `OrderPriceDecorator` | Base Decorator | Lớp cha cho các lớp trang trí |
| `TaxDecorator` | Concrete Decorator | Thêm thuế 8% |
| `ShippingFeeDecorator` | Concrete Decorator | Thêm phí ship theo tỉnh |
| `DiscountDecorator` | Concrete Decorator | Giảm giá nếu khách lần đầu |

**Mục đích**: Thêm trách nhiệm mới cho đối tượng một cách linh hoạt mà không làm thay đổi cấu trúc của lớp.

**Triển khai**: Sử dụng mẫu Decorator để xây dựng hệ thống tính giá động, cho phép thêm các thành phần giá (thuế, phí vận chuyển, giảm giá) mà không thay đổi logic tính giá cơ bản.

**Ứng dụng thực tế**: Tính toán giá cuối cùng của đơn hàng dựa trên nhiều yếu tố (thuế, phí vận chuyển, khuyến mãi) một cách linh hoạt.

**API endpoint**: `POST /api/design-patterns/decorator`

### 6. Flyweight Pattern
| Class/Interface | Vai trò | Mô tả |
|-----------------|---------|-------|
| `ColorConfig` | Flyweight | Đối tượng chia sẻ chứa thông tin cấu hình màu sắc |
| `ColorConfigFactory` | Flyweight Factory | Quản lý và tạo các đối tượng ColorConfig, tái sử dụng khi có thể |

**Mục đích**: Sử dụng chia sẻ để hỗ trợ số lượng lớn các đối tượng nhỏ một cách hiệu quả.

**Triển khai**: `ColorConfigFactory` lưu trữ và quản lý các đối tượng `ColorConfig`, trả về đối tượng đã có thay vì tạo mới khi có thể.

**Ứng dụng thực tế**: Quản lý hiệu quả cấu hình màu sắc cho hàng nghìn sản phẩm đèn mà không tạo đối tượng mới cho mỗi sản phẩm.

**API endpoint**: `GET /api/design-patterns/flyweight`

## Truy cập API Demo

Để trải nghiệm các design pattern qua REST API, bạn có thể sử dụng:

```
GET http://localhost:8080/api/design-patterns/all
```

Endpoint này sẽ trả về thông tin về tất cả các mẫu thiết kế được triển khai và ví dụ sử dụng.
