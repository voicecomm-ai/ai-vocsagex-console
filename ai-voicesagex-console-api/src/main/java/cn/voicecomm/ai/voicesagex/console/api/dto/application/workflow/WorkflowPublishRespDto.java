package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import cn.hutool.json.JSONArray;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: gaox
 * @date: 2025/10/23 13:44
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowPublishRespDto implements Serializable {

  private String url;

  private JSONArray variables;

}
