package me.xnmk.community.enumeration;

public enum ActivationStates {

    ACTIVATION_SUCCESS(0,"激活成功"),
    ACTIVATION_REPEAT(1,"重复激活"),
    ACTIVATION_FAILURE(2,"激活失败"),;

    private int code;
    private String msg;

    ActivationStates(int code, String msg){
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
