
## project dependency environment

| Field           | Version  |
|-----------------|----------|
| JDK version     | 17       |
| Maven version   | 3.9.9    |


## Run project command
| Command Type    | Command                |
|-----------------|------------------------|
| Build           | mvn compile            |
| Package         | mvn package            |
| Run Command     | mvn spring-boot:run    |
| Test Command    | mvn test               |


## 方案设计

### 限流器实现：Redis 令牌桶算法

该方案采用 Redis 令牌桶算法进行限流。通过配置数据和速率来计算生成令牌的速度，配置数据为“每分钟生成多少个令牌”，从而计算出每秒生成的令牌数量。

#### 主要流程：

1. **请求处理：**
    - 每次用户发起请求时，系统会检查桶中是否有足够的令牌：
        - 如果桶中有足够的令牌，消费一个令牌，允许请求通过。
        - 如果桶中没有足够的令牌，拒绝请求（限流）。

2. **令牌补充：**
    - 令牌会根据预定的时间间隔动态补充到桶中。补充过程确保总令牌数不会超过桶的容量，避免桶溢出。

#### 关键点：
- 令牌生成速率由配置数据决定，通过计算每秒生成的令牌数量来实现平滑的请求处理。
- 令牌桶的容量限制了最大并发请求量，确保系统稳定性。

### 流量统计实现：基于内存的ConcurrentHashMap统计方案，由于时间关系，后续改进：采用kafka流式生产者消费者异步流式计算方案

### 不足之处
1. 工程项目依赖redis,redis server并没有集成到项目中，后续会采用docker compose的方式，集成redis到项目开发环境中，实现调试。
2. 流量统计目前是采用内存方式，后续改进可以集成kafka生产者消费者异步统计请求流量。
3. 目前关于redis限流器测试多线程高并发测试case全部是passed，但是本地机器运行docker redis.

### Demo
相关的demo video在根目录下。