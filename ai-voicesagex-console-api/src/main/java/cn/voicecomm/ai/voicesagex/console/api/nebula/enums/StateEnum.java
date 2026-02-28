package cn.voicecomm.ai.voicesagex.console.api.nebula.enums;

import lombok.Getter;

@Getter
public enum StateEnum {

    MODEL(0),
    KNOWLEDGE(1),
    VISUAL(2);

    StateEnum(Integer status) {
        this.status = status;
    }

    // Getter 方法（如果使用了Lombok的@Getter注解，则Lombok会自动生成）
    public Integer getStatus() {
        return status;
    }
    /**
     * 数据项值
     */
    private  Integer status ;

}
