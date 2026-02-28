package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase;

public enum DocumentVerificationEnums {

  ORIGINAL_STATE(0),
  CHECKED(1),
  LOADED_MAP(2);

  DocumentVerificationEnums(Integer status) {
    this.status = status;
  }

  // Getter 方法（如果使用了Lombok的@Getter注解，则Lombok会自动生成）
  public Integer getStatus() {
    return status;
  }

  /**
   * 数据项值
   */
  private Integer status;
}
