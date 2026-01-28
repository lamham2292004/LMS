# 🐳 Docker Guide - LMS Application

Hướng dẫn sử dụng Docker cho dự án LMS (Learning Management System).

## 📋 Mục Lục

- [Yêu Cầu Hệ Thống](#-yêu-cầu-hệ-thống)
- [Quick Start](#-quick-start)
- [Cấu Trúc Docker](#-cấu-trúc-docker)
- [Môi Trường Development](#-môi-trường-development)
- [Môi Trường Production](#-môi-trường-production)
- [Các Lệnh Thường Dùng](#-các-lệnh-thường-dùng)
- [Troubleshooting](#-troubleshooting)

---

## 💻 Yêu Cầu Hệ Thống

- **Docker**: 20.10+ 
- **Docker Compose**: 2.0+
- **RAM**: Tối thiểu 4GB (khuyến nghị 8GB)
- **Disk**: Tối thiểu 5GB free space

Kiểm tra phiên bản:
```bash
docker --version
docker compose version
```

---

## 🚀 Quick Start (Dành cho người mới)

Nếu bạn muốn chạy toàn bộ hệ thống (Web App + Database) từ đầu:

1.  **Chuẩn bị:**
    ```bash
    copy .env.example .env
    ```

2.  **Khởi chạy (Full Stack):**
    ```bash
    # Lệnh này sẽ chạy cả MySQL và Web App
    docker compose up -d --build
    ```

3.  **Truy cập:**
    *   Web App: http://localhost:8083

---

## 🛠️ Quick Start (Dành cho Dev / Máy đã có MySQL)

Nếu máy bạn **đã có MySQL** chạy port 3306 (ví dụ: đã cài XAMPP, hoặc container MySQL khác), hãy dùng cách này để tránh xung đột:

**Cách 1: Chạy App kết nối vào DB có sẵn**
(Sử dụng file cấu hình riêng cho app-only)
```bash
docker compose -f docker-compose.app-only.yml up -d --build
```

**Cách 2: Đổi port cho Database mới**
(Sửa file .env)
1. Mở file `.env`
2. Sửa `MYSQL_PORT=3307` (hoặc port khác chưa dùng)
3. Chạy lệnh: `docker compose up -d --build`

---

## 📁 Cấu Trúc Docker

```
LMS/
├── Dockerfile                  # Multi-stage build cho Spring Boot
├── docker-compose.yml          # Base configuration
├── docker-compose.dev.yml      # Development overrides
├── docker-compose.prod.yml     # Production overrides
├── docker-entrypoint.sh        # Custom entrypoint script
├── .dockerignore               # Files to exclude from build
├── .env.example                # Environment template
└── docker/
    ├── nginx/
    │   ├── nginx.conf          # Nginx main config
    │   ├── conf.d/
    │   │   └── default.conf    # Server block config
    │   └── ssl/                # SSL certificates (production)
    └── mysql/
        ├── conf.d/
        │   └── custom.cnf      # MySQL custom config
        └── init/
            └── 01-init.sql     # Initial database setup
```

---

## 🔧 Môi Trường Development

### Chạy development mode
```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### Các dịch vụ development
| Service     | URL                   | Mô tả                    |
|-------------|----------------------|--------------------------|
| LMS App     | http://localhost:8083 | Spring Boot Application  |
| Debug Port  | localhost:5005        | Remote debugging         |
| phpMyAdmin  | http://localhost:8081 | Database management      |
| Mailhog UI  | http://localhost:8025 | Email testing            |
| MySQL       | localhost:3306        | Database                 |

### Remote Debugging với IntelliJ
1. Run → Edit Configurations → Add New → Remote JVM Debug
2. Host: `localhost`, Port: `5005`
3. Click Debug

### Hot Reload
Source code được mount vào container. Thay đổi code và restart container:
```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml restart lms-app
```

---

## 🏭 Môi Trường Production

### Chuẩn bị
```bash
# Tạo .env với production values
copy .env.example .env

# Chỉnh sửa các giá trị quan trọng:
# - MYSQL_ROOT_PASSWORD (bắt buộc)
# - JWT_SECRET (bắt buộc)
# - CORS_ORIGINS
```

### Chạy production mode
```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Các dịch vụ production
| Service     | URL                    | Mô tả                    |
|-------------|------------------------|--------------------------|
| Nginx       | http://localhost:80    | Reverse proxy            |
| Nginx SSL   | https://localhost:443  | HTTPS (cần cấu hình SSL) |
| LMS App     | Internal               | Spring Boot (behind Nginx)|
| MySQL       | Internal               | Database                 |
| Redis       | Internal               | Cache                    |

### SSL Configuration
1. Đặt certificates vào `docker/nginx/ssl/`:
   - `cert.pem` - SSL Certificate
   - `key.pem` - Private Key
2. Uncomment HTTPS server block trong `docker/nginx/conf.d/default.conf`

---

## 📝 Các Lệnh Thường Dùng

### Container Management
```bash
# Xem containers đang chạy
docker compose ps

# Stop tất cả containers
docker compose down

# Stop và xóa volumes
docker compose down -v

# Restart một service
docker compose restart lms-app

# Xem logs
docker compose logs -f [service_name]
```

### Build
```bash
# Build lại image
docker compose build

# Build không cache
docker compose build --no-cache

# Build với build args
docker compose build --build-arg SKIP_TESTS=false
```

### Database
```bash
# Truy cập MySQL CLI
docker compose exec mysql mysql -u root -p

# Backup database
docker compose exec mysql mysqldump -u root -p LMS > backup.sql

# Restore database
docker compose exec -T mysql mysql -u root -p LMS < backup.sql
```

### Debugging
```bash
# Shell vào container
docker compose exec lms-app sh

# Xem logs của app
docker compose logs -f lms-app

# Kiểm tra health
curl http://localhost:8083/actuator/health
```

---

## 🔍 Troubleshooting

### Container không start được

**Kiểm tra logs:**
```bash
docker compose logs lms-app
```

**Database chưa sẵn sàng:**
```bash
# Kiểm tra MySQL health
docker compose ps mysql
docker compose logs mysql
```

### Port đã được sử dụng
```bash
# Tìm process đang dùng port
netstat -ano | findstr :8083

# Thay đổi port trong .env
APP_PORT=8084
```

### Out of memory
```bash
# Tăng memory limit trong docker-compose.prod.yml
# Hoặc giảm JVM heap size
JAVA_OPTS=-Xmx512m
```

### Build thất bại
```bash
# Clear Docker cache
docker builder prune -f

# Build lại
docker compose build --no-cache
```

### Reset hoàn toàn
```bash
# Stop và xóa tất cả
docker compose down -v --rmi all

# Xóa orphan containers
docker compose down --remove-orphans
```

---

## 📊 Monitoring

### Basic Health Check
```bash
curl http://localhost:8083/actuator/health
```

### Container Stats
```bash
docker stats
```

### Disk Usage
```bash
docker system df
```

---

## 🔐 Security Best Practices

1. **Thay đổi mật khẩu mặc định** trong production
2. **Sử dụng secrets** thay vì environment variables cho sensitive data
3. **Giới hạn network access** - chỉ expose ports cần thiết
4. **Regular updates** - cập nhật base images định kỳ
5. **Scan vulnerabilities**: `docker scan lms-app:latest`

---

## 📞 Support

Nếu gặp vấn đề, hãy:
1. Kiểm tra logs: `docker compose logs`
2. Xem Docker documentation
3. Tạo issue trên GitHub repository
