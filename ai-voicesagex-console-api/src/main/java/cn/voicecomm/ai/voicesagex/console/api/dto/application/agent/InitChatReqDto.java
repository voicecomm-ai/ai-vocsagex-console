package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import cn.hutool.json.JSONObject;
import java.util.List;

/**
 * 初始化智能体对话历史
 * @param agentId 智能体id
 * @param appId 应用id
 * @param token token
 * @param urlKey urlKey
 * @param query 查询
 * @param chatHistory 聊天记录
 */
public record InitChatReqDto(Integer agentId, Integer appId, String token, String urlKey,
                             String query,
                             List<JSONObject> chatHistory) {

}
