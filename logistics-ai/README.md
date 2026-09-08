# 🚚 物流 AI 系统

基于 **Spring Boot + Spring AI** 构建的智能物流助手，支持 Function Calling（工具调用）、多轮对话、流式输出。

## 项目结构

```
logistics-ai/
├── pom.xml                              # Spring Boot 3.4 + Spring AI 1.0
├── .gitignore
├── src/main/
│   ├── java/com/logistics/ai/
│   │   ├── LogisticsAiApplication.java          # 启动类
│   │   ├── config/
│   │   │   └── AiConfig.java                    # ChatClient 配置
│   │   ├── agent/
│   │   │   └── tool/
│   │   │       ├── OrderQueryTool.java          # 📦 运单查询
│   │   │       ├── TrackingTool.java            # 🚚 轨迹查询
│   │   │       └── ShippingFeeCalculatorTool.java  # 💰 运费计算
│   │   ├── service/
│   │   │   └── LogisticsAgentService.java       # AI 对话服务
│   │   ├── controller/
│   │   │   └── LogisticsChatController.java     # REST API
│   │   └── model/dto/
│   │       ├── ChatRequest.java
│   │       └── ChatResponse.java
│   └── resources/
│       └── application.yml                      # 配置文件
```

## 快速启动

### 1. 配置（已内置，按需修改）

API Key 和地址已配置在 `application.yml`，如需覆盖：

```bash
export AI_API_KEY=你的密钥
export AI_BASE_URL=https://dashscope.aliyuncs.com/apps/anthropic
export AI_MODEL=claude-sonnet-4-20250514
```

### 2. 启动

```bash
cd logistics-ai
mvn spring-boot:run
```

看到 `Started LogisticsAiApplication` 即启动成功。

### 3. 测试

```bash
# 查询运单
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-001","message":"帮我查一下 SF1000001 的信息"}'

# 查询轨迹
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-001","message":"这个包裹到哪了？"}'

# 计算运费
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-002","message":"从上海寄一个 5 公斤的包裹到北京，多少钱？"}'

# 流式输出
curl -N -X POST http://localhost:8080/api/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-003","message":"你能帮我做什么？"}'
```

## 核心能力

| 功能 | 说明 |
|------|------|
| **Function Calling** | LLM 自动调用 Java 方法获取运单、轨迹、运费 |
| **多轮对话** | 通过 sessionId 维持会话上下文 |
| **流式输出** | SSE 逐字输出，适合前端实时显示 |

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/chat` | 同步对话 |
| POST | `/api/chat/stream` | 流式对话（SSE） |
| DELETE | `/api/chat/{sessionId}` | 清除会话 |

## 下一步

- [ ] 接入真实数据库（MySQL）
- [ ] 添加 RAG 知识库（物流政策、FAQ）
- [ ] 多 Agent 编排（订单 Agent、调度 Agent、客服 Agent）
- [ ] 会话持久化（Redis）
- [ ] 前端对话界面
- [ ] 单元测试
