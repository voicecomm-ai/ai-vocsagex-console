package cn.voicecomm.ai.voicesagex.console.api.nebula.enums;

public enum FindEnums {


    REVERSELY (0,"REVERSELY"),
    BIDIRECT(2,"BIDIRECT");


    private int code;

    private String path;
    public static String find(int code) {
        for (FindEnums direction : FindEnums.values()) {
            if (direction.code == code) {
                return direction.path;
            }
        }
        // 如果找不到对应的code，可以抛出异常或者返回null，这里返回null作为示例
        return null;
    }

    FindEnums(int code, String path) {
        this.code = code;
        this.path = path;
    }
}
