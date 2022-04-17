package me.xnmk.community.enumeration;

/**
 * 用户状态
 */
public enum UserStatus {

    USER_ACTIVATED(1, "已激活"),
    USER_UNACTIVATED(0, "未激活");


    private int code;
    private String msg;

    UserStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
