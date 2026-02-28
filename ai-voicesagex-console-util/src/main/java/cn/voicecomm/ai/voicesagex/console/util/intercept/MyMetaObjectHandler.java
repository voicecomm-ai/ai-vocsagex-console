package cn.voicecomm.ai.voicesagex.console.util.intercept;

import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

/**
 * ·Mybatis-plus自动填充
 *
 * @author wangfan
 * @date 2023/3/29 17:12
 */
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

  private static final String CREATE_BY = "createBy";
  private static final String CREATED_BY = "createdBy";
  private static final String UPDATE_BY = "updateBy";
  private static final String UPDATE_TIME = "updateTime";
  private static final String CREATE_TIME = "createTime";

  @Override
  public void insertFill(MetaObject metaObject) {
    fillUserId(metaObject, CREATE_BY);
    fillUserId(metaObject, UPDATE_BY);
    fillUserId(metaObject, CREATED_BY);
    Object createTime = getFieldValByName(CREATE_TIME, metaObject);
//    Object updateTime = getFieldValByName(UPDATE_TIME, metaObject);
    LocalDateTime localDateTime = LocalDateTime.now();
    if (createTime == null) {
      setFieldValByName(CREATE_TIME, localDateTime, metaObject);
    }
    setFieldValByName(UPDATE_TIME, localDateTime, metaObject);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    fillUserId(metaObject, UPDATE_BY);
    setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);
  }

  private void fillUserId(MetaObject metaObject, String field) {
    try {
      if (CREATE_BY.equals(field) || CREATED_BY.equals(field)) {
        Object fieldVal = getFieldValByName(field, metaObject);
        if (fieldVal != null) {
          return;
        }
      }
      Integer currentUserId = UserAuthUtil.getUserId();
      setFieldValByName(field, currentUserId, metaObject);
    } catch (Exception e) {
      log.error("Fill UserId异常,从会话获取不到user:{}", e.getMessage(), e);
    }
  }
}
