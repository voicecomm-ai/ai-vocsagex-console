package cn.voicecomm.ai.voicesagex.console.util.config;

import cn.voicecomm.ai.voicesagex.console.util.intercept.MyMetaObjectHandler;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@Slf4j
public class MyBatisAutoConfiguration {

  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    log.info("自定义 Mybatis Plus 插件");
    MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
    log.info("MyBatis Plus 分页插件加载");
    return mybatisPlusInterceptor;
  }

  @Bean
  public MetaObjectHandler autoFill() {
    log.info("配置 Mybatis Plus 自动填充");
    return new MyMetaObjectHandler();
  }
}
