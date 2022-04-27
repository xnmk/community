package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-04-27 11:11
 * @Description: 消息状态
 */
public enum MessageStatus {

    MESSAGE_UNREAD(0, "未读"),
    MESSAGE_READ(1, "已读"),
    MESSAGE_DELETE(2, "删除");

    private int code;
    private String msg;

    MessageStatus(int code, String msg) {
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
