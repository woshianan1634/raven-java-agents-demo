# raven-java-agents-demo

用 **Java / Spring Boot 3 / Java 17** 实现的三个 specialty Agent，通过 **OpenAI 兼容** `POST .../v1/chat/completions` 暴露给 [Raven](https://github.com/EverMind-AI/Raven) Host 的 roster（`kind: openai`）。

> 本仓库是 **Agent 业务实现**。Raven 只负责调度 / Playbook / DAG；不会替你写这些分析逻辑。  
> 配套编排脚手架见：[raven-multi-agent-demo](https://github.com/woshianan1634/raven-multi-agent-demo)。

三个 Agent：

| Agent | 说明 | OpenAI 兼容路径 | 领域 API |
|-------|------|-----------------|----------|
| `ImageAnalysisAgent` | 图像分析 | `POST /agents/image/v1/chat/completions` | `POST /api/agents/image/analyze` |
| `TextAnalysisAgent` | 文字分析 | `POST /agents/text/v1/chat/completions` | `POST /api/agents/text/analyze` |
| `ResultAnalysisAgent` | 结果分析（综合图文） | `POST /agents/result/v1/chat/completions` | `POST /api/agents/result/analyze` |

默认单进程、三套 **path 前缀**（Option A），方便 Raven roster 分别指向不同 `baseUrl`。

## 环境要求

- JDK 17+
- Maven 3.9+

## 运行

```bash
# 无密钥：使用确定性 mock 分析（离线可用）
mvn spring-boot:run

# 有 OpenAI 兼容网关时：
export OPENAI_API_KEY=sk-...
export OPENAI_BASE_URL=https://api.openai.com/v1   # 或其他兼容网关
export OPENAI_MODEL=gpt-4o-mini
mvn spring-boot:run
```

默认端口：`8080`（可用 `SERVER_PORT` 覆盖）。环境变量示例见 `.env.example`。

测试（离线、不依赖外网）：

```bash
mvn -q test
```

## 端点一览

- `GET /api/agents` — 列出三个 Agent（id / description / path）
- 领域 API（JSON 入出）：
  - `POST /api/agents/image/analyze`
  - `POST /api/agents/text/analyze`
  - `POST /api/agents/result/analyze`
- OpenAI 兼容（供 Raven `kind: openai`）：
  - `POST /agents/image/v1/chat/completions`
  - `POST /agents/text/v1/chat/completions`
  - `POST /agents/result/v1/chat/completions`

Raven 侧 `baseUrl` 应写成 **到 `/v1` 为止** 的根，例如 `http://127.0.0.1:8080/agents/image/v1`（客户端会再拼 `/chat/completions`）。

## curl 示例

```bash
# 列表
curl -s http://127.0.0.1:8080/api/agents | jq .

# 图像分析（领域 API）
curl -s http://127.0.0.1:8080/api/agents/image/analyze \
  -H 'Content-Type: application/json' \
  -d '{"description":"一张产品包装照片","imageUrl":"https://example.com/pack.png"}' | jq .

# 文字分析
curl -s http://127.0.0.1:8080/api/agents/text/analyze \
  -H 'Content-Type: application/json' \
  -d '{"content":"这是一段产品说明，强调有机认证与冷链运输。"}' | jq .

# 结果综合
curl -s http://127.0.0.1:8080/api/agents/result/analyze \
  -H 'Content-Type: application/json' \
  -d '{"imageFindings":"包装完整","textFindings":"文案声称有机认证","instruction":"判断图文是否一致"}' | jq .

# OpenAI 兼容（Raven 实际会打的形状）
curl -s http://127.0.0.1:8080/agents/image/v1/chat/completions \
  -H 'Content-Type: application/json' \
  -d '{"model":"image-agent","messages":[{"role":"user","content":"描述包装上的关键信息"}]}' | jq .

curl -s http://127.0.0.1:8080/agents/text/v1/chat/completions \
  -H 'Content-Type: application/json' \
  -d '{"model":"text-agent","messages":[{"role":"user","content":"请摘要：春季促销即将开始"}]}' | jq .

curl -s http://127.0.0.1:8080/agents/result/v1/chat/completions \
  -H 'Content-Type: application/json' \
  -d '{"model":"result-agent","messages":[{"role":"user","content":"imageFindings: ok\ntextFindings: ok"}]}' | jq .
```

领域 API 输入字段：

| Agent | JSON 字段 |
|-------|-----------|
| image | `imageUrl`（可选）、`imageBase64`（可选）、`description`（可选） |
| text | `content` |
| result | `imageFindings`、`textFindings`、`instruction`（可选） |

## 注册到 Raven roster

1. 先启动本服务：`mvn spring-boot:run`
2. 备份：`cp ~/.raven/config.json ~/.raven/config.json.bak`
3. 打开本仓库的 `raven-roster.fragment.json`，把其中 `subagents.agents` **数组成员追加**进已有 `~/.raven/config.json`（保留你已有的其他 agents，不要整文件覆盖）
4. 按需改 `baseUrl` / `model` / `apiKey`
5. **重启 / 重载** Raven host，使 roster 重建
6. 在 Playbook / DAG 节点的 `subagent` 里填写 roster 名：`image-agent` / `text-agent` / `result-agent`

也可用 `jq` 手工合并，例如：

```bash
jq -s '
  .[0] as $cfg | .[1] as $frag |
  $cfg
  | .subagents.agents = ((.subagents.agents // []) + $frag.subagents.agents)
' ~/.raven/config.json ./raven-roster.fragment.json > /tmp/raven-config.merged.json
# 审阅 /tmp/raven-config.merged.json 后再自行覆盖 ~/.raven/config.json
```

片段字段与官方文档一致：`name`、`kind: openai`、`enabled`、`description`、`baseUrl`、`model`、`apiKey`。参考：

- [Agent Integrations](https://evermind-ai.github.io/Raven/agent-integrations/)
- 编排脚手架：[raven-multi-agent-demo](https://github.com/woshianan1634/raven-multi-agent-demo)

## 设计说明

- 无 `OPENAI_API_KEY` 时走 `FakeLlmGateway`，保证 `mvn test` 离线通过
- 有密钥时用 LangChain4j `OpenAiChatModel`（OpenAI 兼容 `baseUrl`）
- 本 demo **不做** Raven Host / Playbook 调度；只提供可被 Raven 调用的 HTTP Agent

## License

MIT
