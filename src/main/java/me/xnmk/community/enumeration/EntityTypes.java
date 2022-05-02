package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-04-29 20:04
 * @Description: 实体类型
 */
public enum EntityTypes {

    ENTITY_TYPE_POST(1, "帖子"),
    ENTITY_TYPE_COMMENT(2, "评论"),
    ENTITY_TYPE_USER(3, "用户");

    private int code;
    private String msg;

    EntityTypes(int code, String msg) {
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
