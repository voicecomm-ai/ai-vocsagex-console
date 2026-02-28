package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 应用-体验
 */
@Data
@Accessors(chain = true)
public class ApplicationExperienceListReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 应用类型---智能体agent,工作流应用workflow,智能体编排应用agent_arrangement
   */
  private String type;

  /**
   * 应用名称
   */
  @Size(message = "应用名称不能超过50个字", max = 50)
  private String name;

  /**
   * 发现页分类id list 全选时不传
   */
  private List<Integer> tagIdList;
}