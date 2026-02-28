package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 应用列表请求dto
 *
 * @author wangf
 * @date 2025/5/19 下午 2:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationListReq extends PagingReqDto implements Serializable {

  /**
   * 全选时不传，应用类型---智能体agent,工作流应用workflow,智能体编排应用agent_arrangement
   */
  private List<String> typeList;

  /**
   * 应用名称
   */
  @Size(message = "应用名称不能超过50个字", max = 50)
  private String name;

  /**
   * 是否已上架
   */
  private Boolean hasExperience;

  /**
   * 是否已发布
   */
  private Boolean hasPublish;


  /**
   * 是否内置
   */
  private Boolean isIntegrated;


  /**
   * agent类型  single单个，multiple多个
   */
  private String agentType;

  /**
   * 应用标签id list 全选时不传
   */
  private List<Integer> tagIdList;

}
