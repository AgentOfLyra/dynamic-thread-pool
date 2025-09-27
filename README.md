# 动态线程池管理项目

## 项目概述

这是一个基于 Spring Boot 的动态线程池管理项目，旨在提供可动态调整的线程池配置，支持通过 Redis 存储和实时更新线程池参数。项目分为几个模块：

- **dynamic-thread-pool-admin**：管理后台，提供 API 接口和简单的前端页面，用于查询和修改线程池配置。
- **dynamic-thread-pool-spring-boot-starter**：Starter 模块，便于其他项目集成动态线程池功能。
- **dynamic-thread-pool-test**：测试模块，用于验证线程池的动态调整。

项目使用 Redisson 作为 Redis 客户端，实现配置的存储和发布订阅。

## 功能特性

- **动态配置**：支持实时修改线程池的核心线程数、最大线程数等参数。
- **查询接口**：查询线程池列表和特定线程池配置。
- **更新接口**：通过 API 更新线程池配置，并通过 Redis 通知相关应用。
- **前端页面**：简单的 Web 界面，支持列表查询、配置查询和更新（使用 Bootstrap 样式）。
- **日志记录**：集成 Logback，支持错误和信息日志。

## 安装指南

### 环境要求

- Java 8 或更高版本
- Maven 3.6+
- Redis 服务器（用于配置存储）
- Spring Boot 2.x

### 步骤

1. 克隆项目：
   ```
   git clone https://github.com/your-repo/dynamic-thread-pool.git
   ```

2. 进入项目目录：
   ```
   cd dynamic-thread-pool
   ```

3. 构建项目：
   ```
   mvn clean install
   ```

4. 配置 Redis：在 `dynamic-thread-pool-admin/src/main/resources/application.yml` 中设置 Redis 连接信息。（如果需要）

没有事先安装Redis，可以使用docker-compose.yml文件启动Redis。
``` bash
docker-compose up -d
```

5. 运行管理后台：
   ```
   cd dynamic-thread-pool-admin
   mvn spring-boot:run
   cd dynamic-thread-pool-test
   mvn spring-boot:run
   ```
   默认端口：8089。

6. 访问前端页面：打开浏览器，访问 `http://localhost:8089/index.html`，或者使用IDE的插件。

## 使用说明

### API 接口

项目暴露以下 RESTful API（基路径：`/api/v1/dynamic/thread/pool/`）：

1. **查询线程池列表**：
   - 方法：GET
   - 路径：`query_thread_pool_list`
   - 返回：线程池配置列表（JSON）

2. **查询特定线程池配置**：
   - 方法：GET
   - 路径：`query_thread_pool_config?appName={appName}&threadPoolName={threadPoolName}`
   - 返回：特定线程池配置（JSON）

3. **更新线程池配置**：
   - 方法：POST
   - 路径：`update_thread_pool_config`
   - 请求体：JSON（如 `{"appName": "app", "threadPoolName": "pool", "corePoolSize": 5, "maximumPoolSize": 10}`）
   - 返回：更新结果（JSON）

### 前端页面

- **线程池列表**：列表视图和表格视图切换，表格模式下每 5 秒自动刷新。
- **查询配置**：输入应用名和线程池名查询详情。
- **更新配置**：填写表单提交更新。

## 贡献指南

欢迎贡献！请遵循以下步骤：

1. Fork 项目。
2. 创建 feature 分支（`git checkout -b feature/new-feature`）。
3. 提交更改（`git commit -m 'Add new feature'`）。
4. 推送分支（`git push origin feature/new-feature`）。
5. 创建 Pull Request。


## 联系

如有问题，请联系[agentoflyra@gmail.com](agentoflyra@gmail.com) 或者[agentoflyra@qq.com](agentoflyra@qq.com)
或提交 Issue。
