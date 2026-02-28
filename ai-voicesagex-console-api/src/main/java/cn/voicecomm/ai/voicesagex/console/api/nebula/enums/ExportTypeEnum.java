package cn.voicecomm.ai.voicesagex.console.api.nebula.enums;

import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import lombok.Getter;

@Getter
public enum ExportTypeEnum {


     ENTITY_DATA(0,"cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.EntityExport"),
    RELATION_DATA(1,"cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.RelationExport");


    ExportTypeEnum(int type ,String path) {
        this.type = type;
        this.path = path;
    }

    // Getter 方法（如果使用了Lombok的@Getter注解，则Lombok会自动生成）
    public int getType() {
        return type;
    }

    public String getPath(int type) {
      return   type == SpaceConstant.INDEX ? ENTITY_DATA.path : RELATION_DATA.path;
    }

    /**
     * 数据项值
     */
    private  int type ;


    private String path;

}
