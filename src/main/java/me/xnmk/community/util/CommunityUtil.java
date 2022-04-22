package me.xnmk.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 18:19
 * @Description: 存放一些常用到的工具
 */
public class CommunityUtil {

    /**
     * 生成随机字符串
     *
     * @return String
     */
    public static String generateUUID() {
        // replace 内是将UUID生成的"-"替换为空
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     *
     * @param key 要加密的内容
     * @return 16进制字符串
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 返回JSON字符串
     *
     * @param code 状态码
     * @param msg  对状态码描述信息
     * @param map  对象
     * @return json
     */
    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJsonString(int code, String msg) {
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code) {
        return getJsonString(code, null, null);
    }
}
