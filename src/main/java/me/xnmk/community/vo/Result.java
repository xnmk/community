package me.xnmk.community.vo;

/**
 * @author:xnmk_zhan
 * @create:2022-04-28 10:27
 * @Description: responseBody
 */
public class Result {

    private int code;

    private String msg;

    private Object data;

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success(String msg, Object data) {
        return new Result(200, msg, data);
    }

    public static Result success(Object data){
        return new Result(200, "success", data);
    }

    public static Result fail(int code, String msg, Object data){
        return new Result(code, msg, data);
    }

    public static Result fail(int code, String msg){
        return new Result(code, msg, null);
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
