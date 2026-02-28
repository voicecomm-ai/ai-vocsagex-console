package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SessionInfoDTO {
    private String sessionId; // 会话ID
    private long lastActiveTime; // 最后活跃时间

    @JsonCreator
    public SessionInfoDTO( @JsonProperty("sessionId") String sessionId,@JsonProperty("lastActiveTime") long lastActiveTime) {
        this.sessionId = sessionId;
        this.lastActiveTime = lastActiveTime;
    }
}
