package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataEntryVO {

    private String type;
    private String name;
    private Long count;
}
