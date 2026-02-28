package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 变量选择resp dto
 *
 * @author wangf
 * @date 2025/5/21 下午 4:34
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariableOptionRespDto implements Serializable {


  /**
   * 节点名称
   */
  private String nodeName;


  /**
   * 变量List
   */
  private List<VariableDto> variableList;


}