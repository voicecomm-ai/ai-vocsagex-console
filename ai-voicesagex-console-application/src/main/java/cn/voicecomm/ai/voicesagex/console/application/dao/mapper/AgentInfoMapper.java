package cn.voicecomm.ai.voicesagex.console.application.dao.mapper;

import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentInfoPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AgentInfoMapper extends BaseMapper<AgentInfoPo> {

  @Select({"<script>",
      "SELECT id, agent_id, application_id, create_time, version, config_data " +
      "FROM (" +
      "  SELECT id, agent_id, application_id, create_time, version, config_data, " +
      "         ROW_NUMBER() OVER (PARTITION BY application_id ORDER BY create_time DESC) as rn " +
      "  FROM agent_publish_history " +
      "  WHERE application_id IN " +
      "  <foreach collection='applicationIds' item='appId' open='(' separator=',' close=')'>" +
      "    #{appId}" +
      "  </foreach>" +
      ") ranked " +
      "WHERE rn = 1",
      "</script>"})
  List<AgentPublishHistoryPo> selectLatestByApplicationIds(@Param("applicationIds") List<Integer> applicationIds);

}