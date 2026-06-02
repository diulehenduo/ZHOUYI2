# 周易八卦算命服务平台

基于 Spring Boot 4 微服务 + Vue 3 前后端分离的周易占卜服务平台。

## 项目简介

用户输入姓名和要测算的事情，后端通过**周易六爻（摇卦）算法**进行占卜测算，生成卦象后调用**大语言模型（LLM）**结合具体问题进行解读分析，给出卦象寓意、变爻分析和行为建议。

### 核心流程

```
用户输入姓名 + 事由
       ↓
  后端摇卦（模拟三枚铜钱，生成六爻）
       ↓
  匹配本卦 → 计算变卦 → 分析动爻
       ↓
  构建 Prompt → 调用大模型
       ↓
  返回卦象解读
```

## 技术栈

| 层级 | 技术 |
|------|------|
| **后端** | Spring Boot 4.0.6, Java 17, Maven |
| **前端** | Vue 3, Vite, Axios |
| **大模型** | OpenAI 兼容 API（支持 DeepSeek / 通义千问 / ChatGPT 等） |
| **数据** | 内置完整 64 卦字典（卦名、卦辞、象辞） |

## 项目结构

```
ZHOUYI2/
├── pom.xml                              # Maven 依赖管理
├── src/main/java/.../zhouyi2/
│   ├── Zhouyi2Application.java          # 应用入口
│   ├── config/
│   │   ├── CorsConfig.java              # CORS 跨域配置
│   │   └── AppConfig.java               # RestClient & LLM 配置
│   ├── controller/
│   │   └── DivinationController.java    # REST API: POST /api/v1/divination
│   ├── model/
│   │   ├── request/DivinationRequest.java
│   │   └── response/
│   │       ├── ApiResponse.java         # 统一响应包装
│   │       └── DivinationResponse.java  # 卦象结果 DTO
│   ├── enums/
│   │   └── YaoType.java                 # 爻类型（老阴/少阳/少阴/老阳）
│   ├── entity/
│   │   ├── Yao.java                     # 爻实体
│   │   └── HexagramResult.java          # 卦象结果
│   ├── service/
│   │   ├── YaoGenerator.java            # 六爻生成（铜钱法）
│   │   ├── DivinationService.java       # 核心占卜编排
│   │   └── LlmService.java              # 大模型调用
│   └── util/
│       └── HexagramDictionary.java      # 六十四卦完整数据
├── src/main/resources/
│   └── application.yml                  # 配置文件
├── frontend/                            # Vue 3 前端项目
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── App.vue                      # 根组件
│       ├── api/divination.js            # API 调用封装
│       └── components/
│           ├── DivinationForm.vue       # 输入表单
│           └── HexagramResult.vue       # 卦象结果展示
└── .env                                 # 环境变量（API Key）
```

## 快速开始

### 前置要求

- JDK 17+
- Node.js 18+
- Maven（或使用项目自带的 `./mvnw`）

### 1. 配置 API Key

> 本项目使用 OpenAI 兼容接口调用大模型（默认配置为 DeepSeek）。

复制 `.env` 文件（已创建），将其中的 `sk-xxxxxxxxxxxx` 替换为你的真实 API Key：

```bash
# .env 文件内容格式
LLM_API_KEY=sk-your-real-api-key-here
```

> `.env` 文件已加入 `.gitignore`，不会被提交到 Git。

### 2. 启动后端

```bash
./mvnw spring-boot:run
```

启动后访问：http://localhost:8080/api/v1/health

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

启动后访问：http://localhost:5173

> 前端开发服务器会自动将 `/api` 请求代理到后端 `localhost:8080`。

## API 文档

### 执行占卜

```
POST /api/v1/divination
Content-Type: application/json

{
  "name": "张三",
  "matter": "求事业"
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "originalNumber": 1,
    "originalName": "乾为天",
    "originalSymbol": "䷀",
    "originalJudgment": "元亨利贞。",
    "originalYaos": [
      { "position": 0, "positionName": "初爻", "type": "少阳", "symbol": "⚊", "yang": true, "changing": false },
      { "position": 1, "positionName": "二爻", "type": "少阴", "symbol": "⚋", "yang": false, "changing": false },
      { "position": 2, "positionName": "三爻", "type": "老阳", "symbol": "⚊", "yang": true, "changing": true },
      ...
    ],
    "changedNumber": 10,
    "changedName": "天泽履",
    "changedSymbol": "䷉",
    "changedJudgment": "履虎尾，不咥人，亨。",
    "movingYaoDescriptions": ["三爻"],
    "analysis": "（大模型解读内容...）",
    "llmUsed": true
  },
  "timestamp": 1717056000000
}
```

### 健康检查

```
GET /api/v1/health
```

## 摇卦算法说明

采用传统**三枚铜钱法**：

| 抛掷结果 | 总分 | 爻类型 | 符号 | 是否变动 |
|---------|------|--------|------|---------|
| 三正（阳阳阳） | 9 | 老阳 | ⚊ | ✅ 变爻 |
| 二正一反 | 8 | 少阴 | ⚋ | ❌ |
| 一正二反 | 7 | 少阳 | ⚊ | ❌ |
| 三反（阴阴阴） | 6 | 老阴 | ⚋ | ✅ 变爻 |

- 从下往上生成 6 爻 → **本卦（主卦）**
- 变爻取反 → **变卦（之卦）**
- 无变爻 → **静卦**，以本卦卦辞为主

## 大模型配置

在 `application.yml` 中配置：

```yaml
llm:
  api-url: https://api.deepseek.com          # API 地址
  api-key: ${LLM_API_KEY}                     # 从 .env 读取
  model: deepseek-chat                        # 模型名
  temperature: 0.7                            # 生成温度
  max-tokens: 1000                            # 最大 Token 数
```

> 支持切换为其他兼容 OpenAI 接口的模型，如通义千问、智谱 GLM 等。

## 大模型不可用时的降级方案

当 LLM 调用失败时（网络问题 / API Key 未配置 / 超时），系统会自动降级为**卦辞解读**，返回预设的卦辞和象辞内容，保证服务可用性。

----

📜 *周易云：天行健，君子以自强不息。*
