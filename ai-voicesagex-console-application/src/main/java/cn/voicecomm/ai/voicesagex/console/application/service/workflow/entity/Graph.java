package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity;


import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode.Condition;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Graph implements Serializable {

  /**
   * 节点
   */
  private List<NodeCanvas> nodes;

  /**
   * 边
   */
  private List<EdgeConfig> edges;

  /**
   * 视图
   */
  private List<Object> viewport;

  @Data
  @EqualsAndHashCode
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EdgeConfig {

    private Object data;

    /**
     * 边ID
     */
    private String id;

    /**
     * selected
     */
    private Boolean selected;

    /**
     * source
     */
    private String sourceHandle;

    /**
     * target
     */
    private String targetHandle;

    /**
     * 边类型
     */
    private String type;

    /**
     * 源节点ID
     */
    private String source;

    /**
     * 目标节点ID
     */
    private String target;

    /**
     * 目标节点ID
     */
    private Integer zIndex;

  }

  /**
   * 运行条件类 定义节点运行的条件，可以是分支标识或具体条件列表
   */
  @Data
  @EqualsAndHashCode
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RunCondition {

    /**
     * 条件类型 branch_identify: 分支标识类型 condition: 条件类型
     */
    private String type;

    /**
     * 分支标识，如sourceHandle 当type为branch_identify时必需
     */
    private String branch_identify;

    /**
     * 运行节点的条件列表 当type为condition时必需
     */
    private List<Condition> conditions;

    /**
     * 计算条件的哈希值
     *
     * @return 条件的SHA256哈希值
     */
    public String getHash() {
      try {
        // 在实际实现中，这里应该序列化对象然后计算哈希
        // 这里简化处理，只基于type和branchIdentify计算
        String input = type + (branch_identify != null ? branch_identify : "");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
          String hex = Integer.toHexString(0xff & b);
          if (hex.length() == 1) {
            hexString.append('0');
          }
          hexString.append(hex);
        }
        return hexString.toString();
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("SHA-256 algorithm not available", e);
      }
    }
  }


}
