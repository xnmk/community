package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-05-14 20:21
 * @Description: 帖子排序模式
 */
public enum OrderModes {

    POST_ORDER_MODE_NEW(0),
    POST_ORDER_MODE_HOT(1)
    ;

    private int code;

    OrderModes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
