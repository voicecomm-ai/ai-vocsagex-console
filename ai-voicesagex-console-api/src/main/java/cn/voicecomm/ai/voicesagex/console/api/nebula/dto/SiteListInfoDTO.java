package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SiteListInfoDTO {

    private Long  id;


    private String  spaceName;

    private Long spaceId;


    private String  siteAddress;


    private  String  siteKey;


    private String createdUser;


    private  Integer type;


    /**
     * 更新时间
     */
    private Date createTime;

    private Integer concurrency;

}
