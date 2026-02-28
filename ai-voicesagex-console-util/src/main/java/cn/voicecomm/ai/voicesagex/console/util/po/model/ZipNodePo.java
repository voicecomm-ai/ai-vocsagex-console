package cn.voicecomm.ai.voicesagex.console.util.po.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 压缩目录
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ZipNodePo implements Serializable {

  @Serial
  private static final long serialVersionUID = -2321348044391821823L;

  /**
   * 文件名或目录名
   */
  private String name;
  /**
   * 文件类型：zip，file，folder
   */
  private String type;
  /**
   * 文件大小
   */
  private String size;
  /**
   * 添加完整路径字段
   */
  private String fullPath;
  /**
   * 子节点（如果是目录）
   */
  private List<ZipNodePo> children = new ArrayList<>();

}
