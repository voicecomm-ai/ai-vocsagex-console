package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceProcessInfoDTO {


    private Long   spaceId;
    /**
     * 图空间名称
     */
    private String spaceName;



    private Integer type;

    /**
     * 创建人
     */
    private String createdUser;


    /**
     * 更新时间
     */
    private String createTime;
}
