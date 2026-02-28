package cn.voicecomm.ai.voicesagex.console.api.enums.user;

import lombok.Getter;

/**
 * @author jiwh
 * @date 2023/5/5 19:41
 */
@Getter
public enum MessageTypeEnum {
  // 用户批量导入 201
  BULK_IMPORT_USERS_COMPLETE(201, "导入完成：共%d条，成功数：%d，失败数：%d"),

  ROLE_AUTHORITY_MODIFY_MSG(202, "该账户角色权限修改,请重新登录后操作,谢谢！"),

  EXPERIENCE_MODIFY_MSG(203, "保存成功！体验设置将对新注册用户生效（截止日期除外）"),

  PASSWORD_UPDATE_LOGOUT(202, "密码已修改！请重新登录"),

  ACCOUNT_DISABLE_LOGOUT(202, "账号已禁用，无法使用"),

  SINGLE_LOGIN_LOGOUT(202, "该账号已在其他处登录"),

  ORDER_PLATFORM_CANCEL_MSG(204, "订单-%s：无人接单"),

  ORDER_NURSE_CANCEL_MSG(204, "护工-%s取消订单：订单编号%s"),

  AWAITING_REVIEW_MSG(205, "护工个人信息已提交，待审核！"),


  MODEL_TRAIN_RESULT_NOTICE(301, "模型训练结果通知"),

  MODEL_DOWNLOAD_RESULT_NOTICE(302, "模型下载结果通知"),

  MODEL_DATASET_ANALYSIS_NOTICE(303, "模型数据集解析结果通知"),

  MODEL_PRE_TRAIN_GENERATE_STATUS_NOTICE(304, "预训练模型手动添加结果通知"),

  MODEL_TRAIN_DEPLOY_RESULT_NOTICE(305, "算法模型训练部署结果通知"),

  MODEL_FINETUNE_RESULT_NOTICE(306, "预训练模型微调结果通知"),

  MODEL_FINETUNE_DEPLOY_RESULT_NOTICE(307, "预训练模型微调部署结果通知"),

  MODEL_ALGORITHM_EVAL_RESULT_NOTICE(308, "算法模型评测结果通知"),

  MODEL_PRE_TRAIN_EVAL_RESULT_NOTICE(309, "预训练模型评测结果通知"),

  WORKFLOW_UPDATE_NOTICE(401, "工作流配置信息变更通知"),

  KNOWLEDGE_ENTITY_RELATION_IMPORT(501, "知识库实体关系导入"),


  ORIGINAL_DATASET_ANALYSIS_NOTICE(601, "标注-原始数据集解析结果通知");



  private final Integer code;

  private final String message;

  MessageTypeEnum(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
}
