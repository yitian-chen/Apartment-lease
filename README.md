# LeaseGo | 公寓租赁管理系统

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.7-green.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.9-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## 项目简介
LeaseGo 是一款现代化的全栈公寓租赁管理系统，基于 **Spring Boot 3** 和 **Java 17** 开发。项目采用前后端分离架构，分为**后台管理系统 (web-admin)** 和 **移动端应用 (web-app)**。管理端实现了房源全生命周期管理、配套设施动态配置及租约审批；移动端则为用户提供便捷的找房、预约看房及合同管理服务。

## 系统架构
项目采用 Maven 多模块 + Spring Cloud 微服务化架构，确保代码的高内聚、低耦合与易扩展性：

**服务架构：**
```
Client → Gateway(8083) → [lb:// 负载均衡]
                     ├── web-app(8081)
                     ├── chat-service(8082) [WebSocket]
                     └── agent-service(8084) [AI Agent]

Nacos(8848): 服务注册发现 + LoadBalancer 动态路由
Sentinel(8858): 流量控制 + 熔断降级
Redis Stack(6379): 缓存存储 + 向量检索 + 分布式锁
RabbitMQ(5672/15672): 异步消息队列 (房间重索引、缓存延时双删、聊天持久化、浏览历史、租约通知)
```

**模块划分：**
- `common`: 核心公共模块。封装了全局异常处理、统一结果返回 (`Result`)、Redis 自定义序列化、RabbitMQ 队列声明、Redisson 锁配置、MinIO 工具类及 `AuthenticationInterceptor` 认证拦截器。
- `model`: 数据模型模块。包含所有数据库实体类 (Entity)、业务视图对象 (VO) 以及业务枚举类(Enum)。
- `gateway`: API 网关服务(端口 8083)。统一入口，基于 Nacos 服务发现 + LoadBalancer 动态路由到下游服务，集成 Sentinel 流量控制与熔断降级，聚合 Knife4j 文档。
- `web-admin`: 后台管理端后端接口。负责公寓、房间、配套、标签、租约及系统用户与移动端用户的权限管理。
- `web-app`: 用户移动端后端接口。支持手机验证码登录、房源检索、浏览记录、预约看房、个人租约查询。
- `chat-service`: 独立聊天服务(端口 8082)。基于 WebSocket 实现即时通讯，支持跨实例消息分发(Redis Pub/Sub)。
- `agent-service`: AI Agent 服务(端口 8084)。基于 LangChain4j + MiniMax + 阿里 DashScope 实现自然语言房源检索。

## 技术栈
- **核心框架:** Java 17, Spring Boot 3.2.7, Spring Cloud Alibaba 2023.0.1.0
- **服务治理:** Nacos 2.2.3 (服务发现) + Spring Cloud LoadBalancer + Sentinel 1.8.6 (流量控制、熔断降级)
- **持久层:** MyBatis-Plus 3.5.9 + MySQL 8.0
- **中间件:**
    - **Redis Stack (7.4):** 缓存存储 + 向量检索 (RediSearch HNSW)
    - **RabbitMQ (4.0):** 异步消息队列（持久化投递、重试机制、死信处理）
    - **MinIO:** 分布式对象存储，统一管理房源图片、聊天文件
- **分布式锁:** Redisson 3.50（租约超卖防护、房间更新串行化、会话创建去重、定时任务防重复）
- **AI Agent:**
    - **框架:** LangChain4j 1.0.0 (AiServices + RAG 管道)
    - **LLM:** MiniMax-M2.7 (OpenAI 兼容 API)
    - **Embedding:** 阿里 DashScope text-embedding-v4 (1024 维)
    - **向量检索:** Redis Stack RediSearch (KNN + cosine 距离)
- **实时通讯:** WebSocket + Redis Pub/Sub 实现跨实例消息推送
- **安全与验证:**
    - **认证:** JWT (JSON Web Token) 实现无状态登录
    - **验证:** EasyCaptcha (图形验证码) + 阿里云 SMS 服务(短信验证码)
- **API 网关 + 流量治理:** Spring Cloud Gateway + LoadBalancer (lb://服务发现路由) + Sentinel (QPS 限流/JSON 429 响应/慢调用熔断)
- **配置管理:** .env 环境变量统一管理敏感信息（数据库密码、AK/SK、API Key），application.yml 提供合理默认值
- **文档工具:** Knife4j 4.5.0 (基于 OpenAPI 3，聚合各服务文档)

## 核心功能
- **房源管理:** 支持公寓与房间的联动管理，包含配套、杂费及标签的动态增删改查、图片上传与地址查找(调用高德地图接口)。
- **登录鉴权:**
    - 管理端：用户名 + 密码 + 图形验证码校验。
    - 移动端：手机号 + 短信验证码快捷登录，自动注册。
- **租赁全流程:** 涵盖房源搜索（省市区/价格/支付方式多维过滤）、预约看房提交、租约签订及状态自动流转。
- **即时通讯(chat-service):** 独立聊天服务，基于 WebSocket 的实时聊天，支持跨实例消息分发（Redis Pub/Sub）、消息已读未读状态、用户搜索（按用户名模糊搜索/手机号精准搜索）、文件与头像上传。
- **房源与聊天联动:** 房间可关联房东用户，移动端详情页可查看房东信息并一键发起聊天。
- **AI 智能检索 (agent-service):** 基于 LangChain4j 构建的 RAG 系统，用户输入自然语言（如"西湖区2000元以内朝南带独卫"），向量检索召回候选 → LLM 筛选推荐（结构化编号标注）→ 前端仅展示 AI 确认匹配的房间卡片。
- **异步消息队列:** RabbitMQ 驱动五项异步任务：
  - 房间数据变更 → Agent 向量索引自动更新
  - 房间缓存延时双删 → MQ 延迟队列实现二次缓存删除 + 失败重试
  - 聊天消息可靠持久化（同时保留 Redis Pub/Sub 实时分发）
  - 浏览历史异步记录
  - 租约到期通知
- **系统工具:** 实现图片上传至 MinIO 存储桶、基于 Spring Task 的租约到期自动结束。

## 项目亮点
1. **高性能缓存架构:** 在移动端房源详情接口引入 **Redis 缓存策略**，通过"先查缓存，穿透查库"的逻辑减少数据库 IO 压力；管理端写操作采用 **分布式锁 + @Transactional + 延时双删 + MQ 重试** 的组合策略保障数据一致性，缓存写入设置 TTL 兜底过期。
2. **异步消息队列:** 集成 **RabbitMQ** 处理房间变更→Agent 自动重索引、聊天消息持久化、浏览历史记录、租约到期通知等异步任务，支持重试机制和手动确认，确保消息不丢失。
3. **健壮的权限体系:** 封装 **`ThreadLocal` 上下文持有者 (`LoginUserHolder`)**，配合自定义拦截器实现用户信息的无感传递，有效隔离各线程间的用户信息。
4. **统一工程化规范:** 建立**全局异常拦截器 (`GlobalExceptionHandler`)**，统一捕获业务异常并返回标准 Result 格式。
5. **类型安全与转换:** 针对业务中大量的状态枚举，自定义 **`StringToBaseEnumConverterFactory`**，实现 Web 层请求参数到数据库枚举类的自动映射，增强了代码的可维护性。
6. **微服务架构:** 采用 **Spring Cloud Alibaba** 将单体应用拆分为 `gateway`、`web-app`、`chat-service`、`agent-service` 四个独立服务，配合 **Nacos** 实现服务注册发现，Gateway 通过 **LoadBalancer** 实现 `lb://` 动态路由与负载均衡。
7. **WebSocket 集群实时通讯:** chat-service 独立部署，基于 **WebSocket + Redis Pub/Sub** 实现跨实例消息分发，支持多实例部署下的实时聊天；结合 JWT 实现消息发送者的身份认证。
8. **API 网关 + 流量治理:** **Spring Cloud Gateway** 作为统一入口，支持 `lb://` 服务发现路由和请求级负载均衡；集成 **Sentinel** 实现四层流量控制（Gateway 路由级 QPS 限流、JSON 429 响应，web-app 登录慢调用降级，agent-service AI 搜索熔断），所有规则在 Sentinel Dashboard `http://localhost:8858` 可视化管理。
9. **房源聊天联动:** 将传统租赁平台的信息孤岛打通，租客可直接在房源详情页发起与房东的即时通讯，降低沟通成本，提升平台粘性。
10. **AI Agent 智能检索:** 基于 LangChain4j 构建 RAG（检索增强生成）管道，将结构化房源数据转为自然语言文档后嵌入 Redis Stack 向量库，结合 MiniMax LLM 实现自然语言驱动的智能房源推荐，并返回可视化房间卡片。
11. **分布式锁并发防护:** 基于 Redisson 实现分布式锁，按 `roomId` 粒度防护租约签约超卖（含时间段重叠检测），按用户对防护聊天会话重复创建，按房间 ID 串行化房间更新操作防止缓存一致性问题，按任务名防护定时任务多实例重复执行。

## 后续计划
1. ✅ 微服务化：引入 Spring Cloud Alibaba (Nacos/OpenFeign/Sentinel) 将聊天服务、用户服务拆分为独立微服务
2. ✅ WebSocket 集群：基于 Redis Pub/Sub 实现跨实例消息分发
3. ✅ 消息队列：引入 RabbitMQ 实现异步消息通信（房间重索引、聊天持久化、浏览历史、租约通知）
4. ✅ AI Agent 智能检索：基于 LangChain4j + 向量数据库的 RAG（接入 MiniMax + 阿里 DashScope）
5. ✅ 分布式锁：Redisson 防护租约超卖（时间段重叠检测）、会话去重、定时任务防重复
6. ✅ 缓存一致性优化：引入分布式锁 + @Transactional + 延时双删（TTL+DLX）+ MQ 重试
7. ✅ 服务发现与流量治理：Gateway 路由改造为 lb:// 服务发现 + LoadBalancer + Sentinel 流控熔断
8. ✅ 配置安全加固：敏感信息迁移至 .env 环境变量，application.yml 不再包含硬编码密钥

## 快速开始
> **注意**：本项目采用前后端分离 + 微服务架构。本仓库为后端代码（Java），前端代码请访问 [LeaseGo-Frontend](https://github.com/yitian-chen/LeaseGo-Frontend)。

### 1. 环境准备
- JDK 17
- Nacos 2.2.3 / MySQL 8.0+ / Redis 6.2+ / MinIO
- Docker & Docker Compose

### 2. 配置文件说明
项目采用 **.env 环境变量 + application.yml 默认值** 的双层配置策略：
- 根目录 `.env` 文件统一管理所有敏感信息（数据库密码、API Key、AK/SK 等），docker-compose 自动读取
- 各服务的 `application.yml` 通过 `${ENV_VAR:default}` 语法引用环境变量，本地开发配置提供合理默认值
- IDEA 用户可安装 EnvFile 插件，启动时自动加载 `.env` 文件

### 3. 初始化数据库
项目使用 MySQL 作为持久化存储。在启动后端服务前，请从以下步骤选其一初始化数据库：

1.**创建数据库**：在 MySQL 中创建名为 `lease` 的数据库。
   ```sql
   CREATE DATABASE IF NOT EXISTS lease CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   ```
2.**导入脚本**: 将根目录下的 `leaseGo.sql` 导入到上述数据库中。
- **方式 A (命令行)**:
  ```Bash
  mysql -u your_username -p lease < leaseGo.sql
  ```
- **方式 B (图形化界面)**: 使用 Navicat、DataGrip 或 IntelliJ IDEA 的 Database 插件，右键点击 lease 数据库，选择"运行 SQL 文件"并选择根目录下的脚本。

### 4. 启动服务
**启动顺序**：Nacos → MySQL/Redis Stack/RabbitMQ/MinIO/Sentinel Dashboard → web-admin/web-app → chat-service → agent-service → gateway

```bash
# 1. 启动所有中间件（Nacos、MySQL、Redis、RabbitMQ、MinIO、Sentinel Dashboard）
docker-compose up -d

# 2. 启动 web-admin (端口 8080，独立服务，不接入 Nacos)
cd web/web-admin && mvn spring-boot:run

# 3. 启动 web-app (端口 8081)
cd web/web-app && mvn spring-boot:run

# 4. 启动 chat-service (端口 8082)
cd chat-service && mvn spring-boot:run

# 5. 启动 agent-service (端口 8084)
cd agent-service && mvn spring-boot:run

# 6. 启动 gateway (端口 8083)
cd gateway && mvn spring-boot:run
```

> **首次启动 agent-service 后**，需调用 `/api/agent/admin/reindex` 触发房源数据向量索引。
> 各服务启动前请确保 `.env` 中的 API Key 已正确配置（`MINIMAX_API_KEY`、`DASHSCOPE_API_KEY`、`ALIYUN_SMS_ACCESS_KEY_ID/SECRET`）。
> 可在 IDEA 中安装 EnvFile 插件自动加载 `.env`，或手动设置系统环境变量。

### 5. Docker 部署中间件
项目通过 `docker-compose.yml` + `.env` 一键启动所有依赖服务：

```bash
# 1. 编辑 .env，填写密码、API Key 等敏感信息（首次）
# 2. 启动所有中间件服务
docker-compose up -d

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止并移除容器
docker-compose down
```

> `.env` 文件已在 `.gitignore` 中排除，不会被提交到仓库。请妥善保管其中的密钥。

**已包含服务：**
| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL | 3307 | 数据库，默认账号 root |
| Redis Stack | 6379/8001 | 缓存 + 向量检索，8001 为 RedisInsight 管理界面 |
| RabbitMQ | 5672/15672 | 异步消息队列，15672 为管理控制台 (guest/guest) |
| MinIO | 9000/9001 | 对象存储，API端口9000，控制台9001 |
| Sentinel Dashboard | 8858 | 流量控制与熔断降级控制台 (sentinel/sentinel) |

**统一 API 文档入口**：http://localhost:8083/doc.html (Knife4j 聚合文档)

| 服务 | 地址 | 说明 |
|------|------|------|
| gateway | http://localhost:8083 | API 网关（统一入口） |
| web-app | http://localhost:8081 | 登录、公寓、房间、预约、支付等业务接口 |
| chat-service | http://localhost:8082 | WebSocket 聊天服务 |
| agent-service | http://localhost:8084 | AI Agent 智能房源检索 |