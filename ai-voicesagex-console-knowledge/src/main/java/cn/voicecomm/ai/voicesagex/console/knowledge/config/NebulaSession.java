package cn.voicecomm.ai.voicesagex.console.knowledge.config;

import com.vesoft.nebula.client.graph.net.Session;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author fulin 配置全局唯一session链接
 **/
@Component
@Data
public class NebulaSession {

  @Autowired
  private NebulaGraphProperties nebulaGraphProperties;


  private SessionPool sessionPool;

  @Bean
  public Session session() throws Exception {
    sessionPool = new SessionPool(nebulaGraphProperties.getPoolConfig().getMaxConnsSize(),
        nebulaGraphProperties.getPoolConfig().getMinConnsSize(),
        nebulaGraphProperties.getHosts().get(0), nebulaGraphProperties.getUsername(),
        nebulaGraphProperties.getPassword());
    return sessionPool.borrow();
  }


}
