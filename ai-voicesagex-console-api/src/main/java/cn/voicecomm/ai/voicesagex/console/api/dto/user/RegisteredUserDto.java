package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisteredUserDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 110740680347298988L;

  private Integer id;

  /**
   * 头像地址
   */
  private String headPath;

  /**
   * 用户ID
   */
  private String userIdentifier;

  /**
   * 用户名称
   */
  private String username;

  /**
   * 手机号码
   */
  private String phone;

  /**
   * 注册天数
   */
  private Long registeredNumberOfDays;

  /**
   * 注册时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registeredTime;

  /**
   * 交易次数
   */
  private Integer tradeNumber;

  /**
   * 累计交易额
   */
  private BigDecimal totalTradePrice;

  /**
   * 登录状态
   */
  private String loginStatus;

  /**
   * 最近登录时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastLoginTime;

  /**
   * 账号状态
   *
   * @see cn.voicecomm.ai.voicesagex.console.api.enums.user.UserStatusEnum#code
   */
  private Integer status;
  /**
   * 行业
   */
  private Integer trade;

  /**
   * 行业名称
   */
  private String tradeName;

  /**
   * 公司名称
   */
  private String company;

  /**
   * 职位
   */
  private String job;

  /**
   * 邮箱
   */
  private String email;

  /**
   * 是否重新注册 0 否 1 是
   */
  private Integer reRegister;
}
