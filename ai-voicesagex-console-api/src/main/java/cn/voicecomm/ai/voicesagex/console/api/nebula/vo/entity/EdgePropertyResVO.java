package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "关系属性信息", description = "关系属性信息")
public class EdgePropertyResVO {

  @Schema(description = "关系名称", example = "tag")
  private List<String> edges;


  private List<PropertyInfoVO> propertyInfos;


}
