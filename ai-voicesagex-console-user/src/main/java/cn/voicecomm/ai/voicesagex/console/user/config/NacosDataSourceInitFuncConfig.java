package cn.voicecomm.ai.voicesagex.console.user.config;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 注册数据源
 */
@Slf4j
public class NacosDataSourceInitFuncConfig implements InitFunc {

  private static Map<String, Map<String, Map<String, Object>>> configProperties;
  private static final String FLOW_DATA_ID_POSTFIX = "-flow-rule.json";
  private static final String DEGRADE_DATA_ID_POSTFIX = "-degrade-rule.json";

  @Override
  public void init() {
    Yaml yaml = new Yaml();
    InputStream in =
      NacosDataSourceInitFuncConfig.class.getClassLoader().getResourceAsStream("bootstrap.yaml");
    configProperties = yaml.loadAs(in, HashMap.class);
    String serverAddr =
      ((Map<String, String>)
        ((Map<String, Object>) (configProperties.get("spring").get("cloud")).get("nacos"))
          .get("cn/voicecomm/ai/voicesagex/console/user/config"))
        .get("server-addr");
    String groupId =
      ((Map<String, String>)
        ((Map<String, Object>) (configProperties.get("spring").get("cloud")).get("nacos"))
          .get("cn/voicecomm/ai/voicesagex/console/user/config"))
        .get("group");
    String namespace =
      ((Map<String, String>)
        ((Map<String, Object>) (configProperties.get("spring").get("cloud")).get("nacos"))
          .get("cn/voicecomm/ai/voicesagex/console/user/config"))
        .get("namespace");

    Properties properties = new Properties();
    properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
    properties.put(PropertyKeyConst.NAMESPACE, namespace);
    // 从nacos加载流控规则
    String flowRuleDataId =
      configProperties.get("spring").get("application").get("name")
        + "-"
        + configProperties.get("spring").get("profiles").get("active")
        + FLOW_DATA_ID_POSTFIX;
    ReadableDataSource<String, List<FlowRule>> flowRuleDataSource =
      new NacosDataSource<>(
        properties,
        groupId,
        flowRuleDataId,
        source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
        }));
    FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    // 从nacos加载降级规则
    String degradeRuleDataId =
      configProperties.get("spring").get("application").get("name")
        + "-"
        + configProperties.get("spring").get("profiles").get("active")
        + DEGRADE_DATA_ID_POSTFIX;
    ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource =
      new NacosDataSource<>(
        properties,
        groupId,
        degradeRuleDataId,
        source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
        }));
    DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
  }
}
