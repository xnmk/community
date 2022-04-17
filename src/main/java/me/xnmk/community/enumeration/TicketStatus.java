package me.xnmk.community.enumeration;

/**
 * 凭证状态
 */
public enum TicketStatus {

    TICKET_VALID(0, "有效"),
    TICKET_INVALID(1, "无效"),;

    private int code;
    private String msg;

    TicketStatus(int code, String msg) {
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
