package cn.voicecomm.ai.voicesagex.console.api.nebula.enums;

/**
 * 路径类型枚举
 */
public enum QueryTypeEnums {


    ALL(0,"ALL"),
    SHORTEST(1,"SHORTEST"),
    NOLOOP(2,"NOLOOP");


    private int code;

    private String path;
    public static String getPathByCode(int code) {
        for (QueryTypeEnums direction : QueryTypeEnums.values()) {
            if (direction.code == code) {
                return direction.path;
            }
        }
        // 如果找不到对应的code，可以抛出异常或者返回null，这里返回null作为示例
        return null;
    }

    QueryTypeEnums(int code, String path) {
        this.code = code;
        this.path = path;
    }
}
