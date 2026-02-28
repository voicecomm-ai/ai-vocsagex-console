package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
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
public class ModelDatasetPageReq extends PagingReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -3077886410445561958L;

  /**
   * 数据集名称
   */
  @Size(message = "名称不能超过50个字", max = 50)
  private String name;
  /**
   * 数据集类型 0：训练数据；1：微调数据；2：评测数据
   */
  private Integer type;
  /**
   * 模型分类
   */
  private List<Integer> classificationIdList;

}
