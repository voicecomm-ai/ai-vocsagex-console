package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.FileTransferMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文件类
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {

  /**
   * 文件ID
   */
  private String id;
  /**
   * 文件名
   */
  private String filename;
  /**
   * 文件扩展名
   */
  private String extension;
  /**
   * 文件MIME类型
   */
  private String mime_type;
  /**
   * 租户ID
   */
  private String tenant_id;
  /**
   * 文件类型
   */
  private String type;
  /**
   * 文件上传方式
   */
  private FileTransferMethod transfer_method;
  /**
   * 文件远程URL
   */
  private String remote_url;
  /**
   * 文件关联ID
   */
  private String related_id;
  /**
   * 文件大小
   */
  private long size;
  /**
   * 文件存储路径
   */
  private String storage_key;
  /**
   * 文件存储路径
   */
  private String url;

}
