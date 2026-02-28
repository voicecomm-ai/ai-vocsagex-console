package cn.voicecomm.ai.voicesagex.console.user.config;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.lifecycle.Closeable;
import com.alibaba.nacos.common.utils.ThreadUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * NACOS服务自动上下线
 */
@Component
@Slf4j
public class NacosServiceInstanceUpAndDownConfig implements ApplicationRunner, Closeable {

  /**
   * nacos服务实例上线
   */
  private static final String OPERATOR_UP = "UP";

  /**
   * nacos服务实例下线
   */
  private static final String OPERATOR_DOWN = "DOWN";

  private static final long DELAY_DOWN = 5000L;

  private static final long DELAY_UP = 10000L;

  private final NacosServiceRegistry nacosServiceRegistry;

  private final NacosRegistration nacosRegistration;

  private ScheduledExecutorService executorService;

  @Autowired
  public NacosServiceInstanceUpAndDownConfig(
    NacosServiceRegistry nacosServiceRegistry, NacosRegistration nacosRegistration) {
    this.nacosServiceRegistry = nacosServiceRegistry;
    this.nacosRegistration = nacosRegistration;
  }

  @PostConstruct
  public void init() {
    int poolSize = 1;
    this.executorService =
      new ScheduledThreadPoolExecutor(
        poolSize,
        r -> {
          Thread thread = new Thread(r);
          thread.setDaemon(true);
          thread.setName("NacosServiceInstanceUpAndDownOperator");
          return thread;
        });
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    this.executorService.schedule(
      new InstanceDownAndUpTask(nacosServiceRegistry, nacosRegistration, OPERATOR_DOWN),
      DELAY_DOWN,
      TimeUnit.MILLISECONDS);
    this.executorService.schedule(
      new InstanceDownAndUpTask(nacosServiceRegistry, nacosRegistration, OPERATOR_UP),
      DELAY_UP,
      TimeUnit.MILLISECONDS);
  }

  @Override
  public void shutdown() throws NacosException {
    ThreadUtils.shutdownThreadPool(executorService);
  }

  /**
   * 服务实例上下线任务
   */
  class InstanceDownAndUpTask implements Runnable {

    private final NacosServiceRegistry nacosServiceRegistry;
    private final NacosRegistration nacosRegistration;
    private final String nacosServiceInstanceOperator;

    InstanceDownAndUpTask(
      NacosServiceRegistry nacosServiceRegistry,
      NacosRegistration nacosRegistration,
      String nacosServiceInstanceOperator) {
      this.nacosServiceRegistry = nacosServiceRegistry;
      this.nacosRegistration = nacosRegistration;
      this.nacosServiceInstanceOperator = nacosServiceInstanceOperator;
    }

    @Override
    public void run() {
      if (OPERATOR_DOWN.equals(nacosServiceInstanceOperator)) {
        // 服务下线
        this.nacosServiceRegistry.deregister(nacosRegistration);
      } else {
        // 服务上线
        this.nacosServiceRegistry.register(nacosRegistration);
      }
      // 上线后，关闭线程池
      if (NacosServiceInstanceUpAndDownConfig.OPERATOR_UP.equals(nacosServiceInstanceOperator)) {
        ThreadUtils.shutdownThreadPool(NacosServiceInstanceUpAndDownConfig.this.executorService);
      }
    }
  }
}
