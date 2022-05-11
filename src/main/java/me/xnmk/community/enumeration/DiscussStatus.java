package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-05-11 14:58
 * @Description: 帖子状态
 */
public enum DiscussStatus {

    DISCUSS_STATUS_COMMON(0),
    DISCUSS_STATUS_ESSENCE(1),
    DISCUSS_STATUS_DELETE(2),
    DISCUSS_TYPES_COMMON(0),
    DISCUSS_TYPES_TOP(1),
    ;

    private int code;

    DiscussStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
