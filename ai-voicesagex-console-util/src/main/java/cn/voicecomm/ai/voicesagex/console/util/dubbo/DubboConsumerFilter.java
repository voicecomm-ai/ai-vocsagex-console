package cn.voicecomm.ai.voicesagex.console.util.dubbo;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;

import java.util.Map;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * ·dubbo 请求过滤器 (消费者)
 *
 * @author wangfan
 * @date 2022/3/7 13:46
 */
@Activate(group = CONSUMER, order = -10000)
public class DubboConsumerFilter implements Filter {

  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

    // ** 添加我们需要放置的用户信息
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //清空
    invocation.getAttachments().clear();

    // 不为空
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        claims.forEach((k, v) -> invocation.setAttachment(k, v.toString()));
      }
    }
    if (invocation instanceof RpcInvocation) {
      ((RpcInvocation) invocation).setInvoker(invoker);
    }

    return invoker.invoke(invocation);
  }
}
