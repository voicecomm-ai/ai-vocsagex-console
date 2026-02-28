package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 模型分页列表请求
 *
 * @author ryc
 * @date 2025/6/4
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ModelBatchReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 226672693485674785L;

  /**
   * id集合
   */
  @NotEmpty(message = "id不能为空")
  private List<Integer> ids;
  /**
   * 是否上架
   */
  private Boolean isShelf;
  /**
   * 标签id集合
   */
  private List<Integer> tagIdList;
}
