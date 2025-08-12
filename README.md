# 1KM聊天应用

一个基于地理位置的实时聊天应用，让用户可以与附近1公里范围内的其他用户进行实时交流。

## 功能特性

- 🌍 **基于地理位置的聊天室**：根据用户位置自动分配到1公里范围内的聊天房间
- 💬 **实时聊天**：使用WebSocket技术实现实时消息推送
- 👤 **用户管理**：支持用户注册、登录和JWT身份验证
- 🏠 **动态房间管理**：自动创建和管理基于地理位置的聊天房间
- 📱 **跨平台支持**：RESTful API设计，支持多种客户端

## 技术栈

### 后端
- **Spring Boot 2.7.3** - 主框架
- **MyBatis** - 数据持久化
- **MySQL** - 主数据库
- **Redis** - 缓存支持
- **WebSocket** - 实时通信
- **JWT** - 身份认证
- **Druid** - 数据库连接池

### 核心算法
- **Haversine公式** - 计算地理坐标距离

## 快速开始

### 环境要求
- JDK 8+
- MySQL 5.7+
- Redis 3.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd 1km001
   ```

2. **数据库配置**
   - 创建MySQL数据库 `1kmdb`
   - 执行数据库初始化脚本

3. **配置文件**
   ```bash
   # 复制配置模板
   cp src/main/resources/application-dev.yml.template src/main/resources/application-dev.yml
   
   # 编辑配置文件，填入实际的数据库和服务配置
   vim src/main/resources/application-dev.yml
   ```

4. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

5. **访问应用**
   - API地址：http://localhost:8081
   - WebSocket地址：ws://localhost:8081/chat

## API文档

### 用户相关
- `POST /api/register` - 用户注册
- `POST /api/login` - 用户登录
- `POST /api/enterRoom` - 进入聊天房间
- `GET /api/messages` - 获取房间消息
- `POST /api/sendMessage` - 发送消息

### WebSocket连接
```javascript
// 连接示例
const ws = new WebSocket('ws://localhost:8081/chat?token=your_jwt_token&roomId=room_id');
```

## 项目结构

```
src/main/java/com/sky/
├── controller/          # 控制器层
│   ├── admin/          # 管理员接口
│   └── user/           # 用户接口
├── service/            # 服务层
├── mapper/             # 数据访问层
├── entity/             # 实体类
├── dto/                # 数据传输对象
├── vo/                 # 视图对象
├── config/             # 配置类
├── interceptor/        # 拦截器
├── exception/          # 异常处理
└── utils/              # 工具类
```

## 核心算法说明

### 地理位置匹配算法
使用Haversine公式计算两个地理坐标之间的距离：

```java
public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
    double earthRadius = 6371.0; // 地球半径，单位：km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadius * c;
}
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。