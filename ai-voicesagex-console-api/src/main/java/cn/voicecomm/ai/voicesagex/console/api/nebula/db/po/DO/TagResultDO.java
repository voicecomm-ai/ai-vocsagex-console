package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Data;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResultDO {

    private Map<String, String> srcMap;

    private Set<Data> datas;
}
