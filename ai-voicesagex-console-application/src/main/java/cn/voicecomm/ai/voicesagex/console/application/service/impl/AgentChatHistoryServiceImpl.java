package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import static cn.voicecomm.ai.voicesagex.console.application.controller.PromptGenerateController.buildPromptRequest;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentChatHistoryService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentChatHistoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentChatListRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentUrlChatListRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.ChatHistorySaveDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.InitChatReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.AgentChatHistoryConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.AgentChatHistoryMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentChatHistoryPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;


@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class AgentChatHistoryServiceImpl extends
    ServiceImpl<AgentChatHistoryMapper, AgentChatHistoryPo> implements AgentChatHistoryService {


  private final AgentInfoService agentInfoService;
  private final ModelService modelService;

  private final AgentChatHistoryConverter agentChatHistoryConverter;


  @Value("${algoUrlPrefix}${chat.conversationTitle}")
  private String conversationTitleUrl;


  @Override
  public CommonRespDto<Integer> initChat(InitChatReqDto dto) {
    log.info("初始化智能体对话历史,请求参数：{}", JSONUtil.toJsonStr(dto));
    AgentChatHistoryPo agentChatHistoryPo = new AgentChatHistoryPo();
    agentChatHistoryPo.setAppId(dto.appId());
    agentChatHistoryPo.setAgentId(dto.agentId());
    agentChatHistoryPo.setConversationToken(dto.token());
    agentChatHistoryPo.setUrlKey(dto.urlKey());
    agentChatHistoryPo.setLastChatTime(LocalDateTime.now());
    // 获取标题
    CommonRespDto<String> tiltleComm = getTiltle(dto.appId(), dto.query());
    if (!tiltleComm.isOk()) {
      return CommonRespDto.error(tiltleComm.getMsg());
    }
    agentChatHistoryPo.setConversationTitle(tiltleComm.getData());
    agentChatHistoryPo.setChatHistory(JSONUtil.toJsonStr(dto.chatHistory()));
    baseMapper.insert(agentChatHistoryPo);
    return CommonRespDto.success(agentChatHistoryPo.getId());

  }

  @Override
  public void updateChatHistory(ChatHistorySaveDto dto) {

    AgentChatHistoryPo agentChatHistoryPo = baseMapper.selectById(dto.getId());
    List<JSONObject> chatHistoryArray;
    if (StrUtil.isBlank(agentChatHistoryPo.getChatHistory()) || !JSONUtil.isTypeJSONArray(
        agentChatHistoryPo.getChatHistory())) {
      chatHistoryArray = new ArrayList<>();
    } else {
      chatHistoryArray = JSONUtil.toList(agentChatHistoryPo.getChatHistory(), JSONObject.class);
    }
    chatHistoryArray.addAll(dto.getChatHistory());
    agentChatHistoryPo.setChatHistory(JSONUtil.toJsonStr(chatHistoryArray));
    agentChatHistoryPo.setLastChatTime(LocalDateTime.now());
    baseMapper.updateById(agentChatHistoryPo);
    CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> updateTitle(Integer id, String title) {
    baseMapper.updateById(AgentChatHistoryPo.builder().id(id).conversationTitle(title).build());
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<List<AgentUrlChatListRespDto>> chatList(String token) {

    List<AgentChatHistoryPo> agentChatHistoryPos = baseMapper.selectList(
        Wrappers.<AgentChatHistoryPo>lambdaQuery()
            .select(AgentChatHistoryPo::getId, AgentChatHistoryPo::getConversationTitle,
                AgentChatHistoryPo::getCreateTime, AgentChatHistoryPo::getUpdateTime,
                AgentChatHistoryPo::getLastChatTime)
            .eq(AgentChatHistoryPo::getConversationToken, token)
            .orderByDesc(AgentChatHistoryPo::getLastChatTime));

    // 按日期分组并构建结果
    Map<String, List<AgentChatListRespDto>> groupedByDate = new LinkedHashMap<>();
    for (AgentChatHistoryPo po : agentChatHistoryPos) {
      String dateStr = LocalDateTimeUtil.format(po.getLastChatTime(), "yyyy-MM-dd");
      // 如果是昨天，dateStr = "昨天"
      if (LocalDateTime.now().minusDays(1).toLocalDate()
          .equals(po.getLastChatTime().toLocalDate())) {
        dateStr = "昨天";
      }
      if (LocalDateTime.now().toLocalDate().equals(po.getLastChatTime().toLocalDate())) {
        dateStr = "今天";
      }
      groupedByDate.computeIfAbsent(dateStr, k -> new ArrayList<>())
          .add(AgentChatListRespDto.builder().id(po.getId())
              .conversationTitle(po.getConversationTitle()).build());
    }

    // 构建最终结果列表
    List<AgentUrlChatListRespDto> result = new ArrayList<>();
    groupedByDate.forEach((date, dataList) -> {
      AgentUrlChatListRespDto respDto = new AgentUrlChatListRespDto();
      respDto.setDate(date);
      respDto.setAgentChatList(dataList);
      result.add(respDto);
    });

    return CommonRespDto.success(result);
  }


  @Override
  public CommonRespDto<AgentChatHistoryDto> chatInfo(Integer id) {
    AgentChatHistoryPo agentChatHistoryPo = baseMapper.selectById(id);
    AgentChatHistoryDto agentChatHistoryDto = agentChatHistoryConverter.poToDto(agentChatHistoryPo);
    List<ObjectNode> objectNodeList = agentChatHistoryDto.getChatHistory().stream()
        .peek(objectNode -> {
          if ("ai".equals(objectNode.get("type").asText())) {
            // 去除思考过程<think>和</think>中间的数据
            String content = objectNode.get("content").asText();
            Pattern pattern = Pattern.compile("^<think>.*?</think>", Pattern.DOTALL);
            String newContent = pattern.matcher(content).replaceFirst("");
            if (newContent.startsWith("\n\n")) {
              newContent = newContent.substring(2);
            }
            objectNode.put("content", newContent);
          }
        }).toList();
    agentChatHistoryDto.setChatHistory(objectNodeList);
    return CommonRespDto.success(agentChatHistoryDto);
  }


  @Override
  public CommonRespDto<Void> deleteChat(Integer id) {
    baseMapper.delete(Wrappers.<AgentChatHistoryPo>lambdaQuery()
        .eq(AgentChatHistoryPo::getId, id));

    return CommonRespDto.success();
  }

  private CommonRespDto<String> getTiltle(Integer appId, String query) {

    CommonRespDto<AgentInfoResponseDto> agentInfoResp = agentInfoService.getPublishedInfo(appId);
    // 获取模型
    CommonRespDto<ModelDto> commonRespDto = modelService.getInfo(
        agentInfoResp.getData().getModelId());
    JSONObject promptRequestJson = buildPromptRequest(commonRespDto.getData());
    JSONObject reqJson = JSONUtil.createObj().putOnce("model_instance_provider", "ollama")
        .putOnce("query", query).putOnce("stream", false)
        .putOnce("model_instance_config", promptRequestJson);
    log.info("对话标题生成请求 url：{}，参数：{}", conversationTitleUrl, JSONUtil.toJsonStr(reqJson));
    String post = HttpUtil.post(conversationTitleUrl, JSONUtil.toJsonStr(reqJson));
    log.info("对话标题生成执行结果：{}", JSONUtil.toJsonStr(post));
    JSONObject jsonObject = JSONUtil.parseObj(post);
    if (jsonObject.getInt("code") != 1000) {
      return CommonRespDto.error(jsonObject.getStr("msg"));
    }
    return CommonRespDto.success(JSONUtil.getByPath(jsonObject, "data.conversation_title", ""));
  }
}
