package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: gaox
 * @date: 2025/12/29 17:15
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "工作流模板 DTO")
public class TemplateDto {

  private List<ApplicationTagGroupDto> agentList;

  private List<ApplicationTagGroupDto> workflowList;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class ApplicationTagGroupDto implements Serializable {

    private Integer tagId;
    private String tagName;
    private List<ApplicationExperienceDto> applicationDtos;
  }
}
