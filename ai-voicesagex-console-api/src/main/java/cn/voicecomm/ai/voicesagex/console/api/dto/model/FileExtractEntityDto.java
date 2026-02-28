package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文件结构Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FileExtractEntityDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -365763058338754985L;

  /**
   * 文件路径
   */
  private String filePath;

  /**
   * 文件结构
   */
  private List<ZipNodeDto> zipNodeList = new ArrayList<>();
}
