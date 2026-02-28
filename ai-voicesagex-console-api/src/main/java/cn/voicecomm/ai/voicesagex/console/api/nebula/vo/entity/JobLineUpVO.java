package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "任务排队", description = "任务排队")
public class JobLineUpVO {


  @Schema(description = "解析排队", example = "3434343")
  private ParseLineUpVO parseLineUpVOList;
}
