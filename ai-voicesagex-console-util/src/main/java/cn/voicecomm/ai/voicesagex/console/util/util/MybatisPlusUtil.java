package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.transaction.annotation.Transactional;

/**
 * ·mybatisplus批量插入工具
 *
 * @author wangfan
 * @date 2022/3/21 13:47
 */
public class MybatisPlusUtil<T> {

  protected BaseMapper baseMapper;

  private final Class tClass;

  public MybatisPlusUtil(BaseMapper baseMapper, Class tClass) {
    this.baseMapper = baseMapper;
    this.tClass = tClass;
  }

  protected Class currentModelClass() {
    return this.tClass;
  }

  /**
   * 批量操作 SqlSession
   */
  protected SqlSession sqlSessionBatch() {
    return SqlHelper.sqlSessionBatch(currentModelClass());
  }

  /**
   * 获取 SqlStatement
   *
   * @param sqlMethod ignore
   * @return ignore
   */
  protected String sqlStatement(SqlMethod sqlMethod) {
    return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
  }

  /**
   * 批量插入
   *
   * @param entityList ignore
   * @param batchSize  ignore
   * @return ignore
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean saveBatch(Collection<T> entityList, int batchSize) {
    if (CollUtil.isEmpty(entityList)) {
      return true;
    }
    String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
    try (SqlSession batchSqlSession = sqlSessionBatch()) {
      int i = 0;
      for (T anEntityList : entityList) {
        batchSqlSession.insert(sqlStatement, anEntityList);
        if (i >= 1 && i % batchSize == 0) {
          batchSqlSession.flushStatements();
        }
        i++;
      }
      batchSqlSession.flushStatements();
    }
    return true;
  }

  @Transactional(rollbackFor = Exception.class)
  public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
    if (CollUtil.isEmpty(entityList)) {
      return true;
    }
    Class<?> cls = currentModelClass();
    TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
    Assert.notNull(
        tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
    String keyProperty = tableInfo.getKeyProperty();
    Assert.notEmpty(
        keyProperty, "error: can not execute. because can not find column for id from entity!");
    try (SqlSession batchSqlSession = sqlSessionBatch()) {
      int i = 0;
      for (T entity : entityList) {
        Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
        if (StringUtils.checkValNull(idVal)
            || Objects.isNull(baseMapper.selectById((Serializable) idVal))) {
          batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), entity);
        } else {
          MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
          param.put(Constants.ENTITY, entity);
          batchSqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
        }
        // 不知道以后会不会有人说更新失败了还要执行插入 😂😂😂
        if (i >= 1 && i % batchSize == 0) {
          batchSqlSession.flushStatements();
        }
        i++;
      }
      batchSqlSession.flushStatements();
    }
    return true;
  }

  @Transactional(rollbackFor = Exception.class)
  public boolean updateBatchById(Collection<T> entityList, int batchSize) {
    if (CollUtil.isEmpty(entityList)) {
      return true;
    }
    String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
    try (SqlSession batchSqlSession = sqlSessionBatch()) {
      int i = 0;
      for (T anEntityList : entityList) {
        MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
        param.put(Constants.ENTITY, anEntityList);
        batchSqlSession.update(sqlStatement, param);
        if (i >= 1 && i % batchSize == 0) {
          batchSqlSession.flushStatements();
        }
        i++;
      }
      batchSqlSession.flushStatements();
    }
    return true;
  }
}
