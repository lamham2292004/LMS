# 🚀 Quick Start - Docker Setup

## Chuẩn bị

1. **Cài đặt Docker Desktop**

   - Download tại: https://www.docker.com/products/docker-desktop/
   - Khởi động Docker Desktop

2. **Clone hoặc navigate đến project**
   ```bash
   cd F:\DoAn\LMS
   ```

## Khởi động nhanh

```bash
# Bước 1: Build và start tất cả services
docker-compose up -d

# Bước 2: Xem logs (optional)
docker-compose logs -f lms-app

# Bước 3: Truy cập application
http://localhost:8083/api/health
```

## Kiểm tra hoạt động

```bash
# Kiểm tra services đang chạy
docker-compose ps

# Kiểm tra health status
curl http://localhost:8083/api/health

# Expected response:
# {
#   "result": {
#     "status": "UP",
#     "timestamp": "2024-10-15T...",
#     "service": "LMS API",
#     "version": "1.2"
#   }
# }
```

## Các lệnh thường dùng

```bash
# Dừng services
docker-compose down

# Dừng và xóa data
docker-compose down -v

# Restart sau khi sửa code
docker-compose up -d --build

# Xem logs real-time
docker-compose logs -f

# Vào container để debug
docker exec -it lms-app sh

# Kiểm tra Docker health
docker inspect lms-app --format='{{.State.Health.Status}}'
```

## Cấu trúc files

```
├── Dockerfile              # Multi-stage build configuration
├── docker-compose.yml      # Services orchestration
├── .dockerignore          # Files to exclude from build
├── DOCKER.md              # Detailed documentation
└── docker-setup.md        # This file (quick start)
```

## Cổng (Ports)

- **Application**: http://localhost:8083
- **MySQL**: localhost:3306
- **Health Check**: http://localhost:8083/api/health

## Thông tin Database

Khi chạy với Docker Compose:

- **Host**: `mysql` (từ container) hoặc `localhost` (từ host machine)
- **Port**: `3306`
- **Database**: `LMS`
- **Username**: `root`
- **Password**: `abc123-`

## Troubleshooting

### Port 8083 đã được sử dụng?

Sửa trong `docker-compose.yml`:

```yaml
ports:
  - "8084:8083" # Dùng port 8084 thay vì 8083
```

### MySQL không kết nối được?

```bash
# Đợi MySQL khởi động hoàn toàn
docker-compose logs mysql

# Restart lại services
docker-compose restart
```

### Build lỗi?

```bash
# Xóa cache và rebuild
docker-compose build --no-cache
docker-compose up -d
```

### Xem logs chi tiết?

```bash
# Logs của application
docker-compose logs lms-app

# Logs của MySQL
docker-compose logs mysql

# Logs real-time
docker-compose logs -f
```

## Production Notes

⚠️ **QUAN TRỌNG**: Trước khi deploy production:

1. ✅ Đổi `MYSQL_ROOT_PASSWORD` trong `docker-compose.yml`
2. ✅ Đổi `APP_JWT_SECRET` thành secret key mạnh hơn
3. ✅ Set `SPRING_PROFILES_ACTIVE=prod`
4. ✅ Cấu hình backup cho MySQL volume
5. ✅ Cấu hình SSL/TLS nếu cần
6. ✅ Set `SPRING_JPA_SHOW_SQL=false` để giảm logs

## Tính năng Docker

✨ **Multi-stage build**: Giảm image size
✨ **Health checks**: Tự động kiểm tra sức khỏe application
✨ **Non-root user**: Bảo mật cao hơn
✨ **Volume mapping**: Persistent data cho MySQL và uploads
✨ **Network isolation**: Services giao tiếp qua internal network
✨ **Dependency caching**: Build nhanh hơn khi code thay đổi

## Liên hệ & Hỗ trợ

- Chi tiết đầy đủ: Xem `DOCKER.md`
- Issues: Báo lỗi trong phần Issues của project

