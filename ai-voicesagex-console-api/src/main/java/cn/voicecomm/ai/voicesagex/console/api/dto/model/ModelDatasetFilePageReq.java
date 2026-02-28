package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型数据集分页列表请求
 *
 * @author ryc
 * @date 2025/6/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelDatasetFilePageReq extends PagingReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -3077886410445561958L;

  /**
   * 数据集id
   */
  @NotNull(message = "数据集id不能为空")
  private Integer datasetId;

  /**
   * 数据集名称
   */
  @Size(message = "名称不能超过50个字", max = 50)
  private String name;

}
