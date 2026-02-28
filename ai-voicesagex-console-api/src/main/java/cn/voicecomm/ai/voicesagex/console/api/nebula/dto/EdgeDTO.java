package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import lombok.Data;

@Data
public class EdgeDTO {

    private String sourceId;


    private  String edge;


    private  String targetId;


    private Long rank;


    public EdgeDTO(String sourceId, String edge, String targetId, Long rank) {
        this.sourceId = sourceId;
        this.edge = edge;
        this.targetId = targetId;
        this.rank = rank;
    }
}
