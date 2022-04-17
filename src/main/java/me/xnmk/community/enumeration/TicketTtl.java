package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-04-17 16:17
 * @Description: 凭证超时时间
 */
public enum TicketTtl {

    DEFAULT_EXPIRED_SECOND(3600 * 12, "12小时"),
    REMEMBER_EXPIRED_SECOND(3600 * 24 * 7, "一周");;

    private int expiredSecond;
    private String msg;

    TicketTtl(int expiredSecond, String msg) {
        this.expiredSecond = expiredSecond;
        this.msg = msg;
    }

    public int getExpiredSecond() {
        return expiredSecond;
    }

    public void setExpiredSecond(int expiredSecond) {
        this.expiredSecond = expiredSecond;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
