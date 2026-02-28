package cn.voicecomm.ai.voicesagex.console.api.nebula.enums;

/**流向枚举
 *
 */
public enum DirectionEnums {

    IN(0,"IN"),
    OUT(1,"OUT"),
    BOTH(2,"BOTH");


    private int code;

    private String path;
    public static String getPathByCode(int code) {
        for (DirectionEnums direction : DirectionEnums.values()) {
            if (direction.code == code) {
                return direction.path;
            }
        }
        // 如果找不到对应的code，可以抛出异常或者返回null，这里返回null作为示例
        return null;
    }

    DirectionEnums(int code, String path) {
        this.code = code;
        this.path = path;
    }


}
