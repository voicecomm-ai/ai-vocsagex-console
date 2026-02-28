package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

/**
 * 文件管理器
 */
public class FileManager {

  public static Object getAttr(File file, FileAttribute attr) {
    return switch (attr) {
      case TYPE -> file.getType();
      case SIZE -> file.getSize();
      case NAME -> file.getFilename();
      case MIME_TYPE -> file.getMime_type();
      case TRANSFER_METHOD -> file.getTransfer_method().getValue();
      case URL -> file.getUrl();
      case EXTENSION -> file.getExtension();
      case RELATED_ID -> file.getRelated_id();
    };
  }
}
