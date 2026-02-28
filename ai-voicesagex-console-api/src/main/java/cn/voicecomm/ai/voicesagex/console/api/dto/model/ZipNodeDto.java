package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ZipNodeDto implements Serializable {

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
  @Schema(example = """
      [
          {
              "children": [],
              "fullPath": "测试文件199.99m/1/1111.txt",
              "name": "1111.txt",
              "size": "0.01 KB"
          },
          {
              "children": [],
              "fullPath": "测试文件199.99m/新建 文本文档.txt",
              "name": "新建 文本文档.txt",
              "size": "0.00 KB"
          }
      ]""")
  private List<ZipNodeDto> children = new ArrayList<>();

}
