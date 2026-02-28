package cn.voicecomm.ai.voicesagex.console.api.dto.application;


import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccessResponse implements Serializable {

  /**
   * api接口信息列表
   */
  private List<ApiInterfaceInfo> apiInterfaceInfoList;


  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ApiInterfaceInfo implements Serializable {

    /**
     * api接口地址
     */
    private String apiInterfaceUrl;

    /**
     * api接口名称
     */
    private String apiInterfaceName;
  }

}
