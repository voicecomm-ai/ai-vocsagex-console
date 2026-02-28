package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 应用
 *
 * @author wangf
 * @date 2025/5/19 下午 1:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto extends BaseAuditDto {


  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  private Integer id;

  /**
   * 应用类型---智能体agent,工作流应用workflow,智能体编排应用agent_arrangement
   */
  private String type;

  /**
   * 应用名称
   */
  @NotBlank(message = "应用名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(message = "应用名称不能超过50个字", max = 50, groups = {AddGroup.class, UpdateGroup.class})
  private String name;

  /**
   * 描述
   */
  @Size(message = "应用描述不能超过400个字", max = 400, groups = {AddGroup.class,
      UpdateGroup.class})
  private String description;

  /**
   * 图标地址
   */
  private String iconUrl;

  /**
   * 状态  -1删除，0草稿，1已发布
   */
  private Integer status;

  /**
   * 上架状态
   */
  private Boolean onShelf;


  /**
   * 标签List(新增时只需传id)
   */
  private List<ApplicationTagDto> tagList;


  /**
   * mcp List(新增时只需传id)
   */
  private List<McpDto> mcpList;


  /**
   * 是否允许api访问
   */
  private Boolean apiAccessable;


  /**
   * URL是否可访问
   */
  private Boolean urlAccessable;


  /**
   * URL地址的key
   */
  private String urlKey;


  /**
   * 长期记忆是否开启
   */
  private Boolean longTermMemoryEnabled;


  /**
   * 是否内置
   */
  private Boolean isIntegrated;


  /**
   * agent类型  single单个，multiple多个
   */
  private String agentType;


}