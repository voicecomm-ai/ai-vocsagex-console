package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.jackson;

import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo.SysUserDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SysUserDeserializer extends JsonDeserializer<SysUserDetails> {

  @Override
  public SysUserDetails deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = (ObjectMapper) jp.getCodec();
    JsonNode jsonNode = mapper.readTree(jp);

    JsonNode passwordNode = readJsonNode(jsonNode, "password");
    Integer userId = readJsonNode(jsonNode, "id").asInt();
    String account = readJsonNode(jsonNode, "account").asText();
    String username = readJsonNode(jsonNode, "username").asText();
    String password = passwordNode.asText("");
    String phone = readJsonNode(jsonNode, "phone").asText();
    String deviceType = readJsonNode(jsonNode, "deviceType").asText();
    Integer dataPermission = readJsonNode(jsonNode, "dataPermission").asInt();
    SysUserDetails result =
      SysUserDetails.builder()
        .id(userId)
        .account(account)
        .username(username)
        .password(password)
        .phone(phone)
        .deviceType(deviceType)
        .dataPermission(dataPermission)
        .build();

    log.info("反序列化User成功: {}", result);

    if (passwordNode.asText(null) == null) {
      result.eraseCredentials();
    }
    return result;
  }

  private JsonNode readJsonNode(JsonNode jsonNode, String field) {
    return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
  }
}
