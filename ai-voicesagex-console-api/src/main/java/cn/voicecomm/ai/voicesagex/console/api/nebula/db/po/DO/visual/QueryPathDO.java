package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QueryPathDO implements Serializable {


    @Serial
    private static final long serialVersionUID = -1825830975720774881L;
    private String spaceId;



    private String startId;


    private String  endId;

    private String edgeNameList;


    private String  direction;


    private Integer  stepInterval;

    private  String queryType;



}
