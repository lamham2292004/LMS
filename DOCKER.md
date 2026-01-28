# Docker Setup Guide - LMS Application

## Cấu trúc Docker

Dự án này bao gồm các file Docker sau:

- `Dockerfile` - Multi-stage build cho Spring Boot application
- `docker-compose.yml` - Orchestration cho MySQL và LMS app
- `.dockerignore` - Tối ưu hóa quá trình build

## Yêu cầu

- Docker Desktop hoặc Docker Engine (phiên bản 20.10+)
- Docker Compose (phiên bản 2.0+)

## Cách sử dụng

### 1. Build và chạy với Docker Compose (Khuyến nghị)

```bash
# Build và khởi động tất cả services
docker-compose up -d

# Xem logs
docker-compose logs -f lms-app

# Dừng services
docker-compose down

# Dừng và xóa volumes (database data)
docker-compose down -v
```

### 2. Build và chạy riêng lẻ

```bash
# Build Docker image
docker build -t lms-app:latest .

# Chạy MySQL container trước
docker run -d \
  --name lms-mysql \
  -e MYSQL_DATABASE=LMS \
  -e MYSQL_ROOT_PASSWORD=abc123- \
  -p 3306:3306 \
  mysql:8.0

# Chạy LMS application
docker run -d \
  --name lms-app \
  -p 8083:8083 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://lms-mysql:3306/LMS \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=abc123- \
  --link lms-mysql:mysql \
  lms-app:latest
```

## Cấu hình môi trường

Bạn có thể tùy chỉnh các biến môi trường trong `docker-compose.yml`:

### Server Configuration

- `SERVER_PORT` - Application port (default: 8083)

### Database Configuration

- `SPRING_DATASOURCE_URL` - MySQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME` - JDBC driver class

### JPA/Hibernate Configuration

- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Hibernate DDL mode (update/create/validate)
- `SPRING_JPA_SHOW_SQL` - Show SQL queries in logs
- `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT` - Hibernate SQL dialect

### Application Configuration

- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod)
- `APP_JWT_SECRET` - JWT secret key for authentication
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins
- `SPRING_JACKSON_TIME_ZONE` - Timezone for JSON serialization

### File Upload Configuration

- `SPRING_SERVLET_MULTIPART_ENABLED` - Enable multipart file upload
- `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE` - Maximum file size
- `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE` - Maximum request size

## Truy cập ứng dụng

Sau khi khởi động thành công:

- Application: http://localhost:8083
- MySQL: localhost:3306

## Các lệnh hữu ích

```bash
# Xem logs real-time
docker-compose logs -f

# Restart service
docker-compose restart lms-app

# Rebuild sau khi thay đổi code
docker-compose up -d --build

# Vào container để debug
docker exec -it lms-app sh

# Xem danh sách containers
docker-compose ps

# Kiểm tra health status
docker inspect lms-app | grep -i health
```

## Troubleshooting

### Application không kết nối được MySQL

- Đảm bảo MySQL container đã khởi động xong (health check passed)
- Kiểm tra logs: `docker-compose logs mysql`

### Port đã được sử dụng

- Thay đổi port mapping trong `docker-compose.yml`:
  ```yaml
  ports:
    - "8084:8083" # Sử dụng port 8084 thay vì 8083
  ```

### Build lỗi

- Xóa cache và rebuild:
  ```bash
  docker-compose build --no-cache
  ```

## Production Deployment

Để deploy lên production:

1. Cập nhật biến môi trường trong `docker-compose.yml`
2. Sử dụng strong passwords cho database
3. Cấu hình volume backups cho MySQL data
4. Thêm reverse proxy (nginx) nếu cần
5. Cấu hình SSL/TLS certificates

## Tối ưu hóa

- Image size được tối ưu bằng multi-stage build
- JVM được cấu hình với G1GC collector
- Health checks được tích hợp sẵn (endpoint: `/api/health`)
- Non-root user được sử dụng cho bảo mật
- Dependency caching để build nhanh hơn
- Alpine Linux base image cho kích thước nhỏ gọn

## Health Check

Application có health check endpoint tại:

- URL: `http://localhost:8083/api/health`
- Interval: 30s
- Timeout: 3s
- Start period: 40s
- Retries: 3

Kiểm tra health status:

```bash
# Trong container
curl http://localhost:8083/api/health

# Từ host machine
curl http://localhost:8083/api/health

# Kiểm tra Docker health status
docker inspect lms-app --format='{{.State.Health.Status}}'
```
