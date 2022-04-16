package me.xnmk.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 18:19
 * @Description: 存放一些常用到的工具
 */
public class CommunityUtil {

    /**
     * 生成随机字符串
     * @return String
     */
    public static String generateUUID() {
        // replace 内是将UUID生成的"-"替换为空
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     * @param key 要加密的内容
     * @return 16进制字符串
     */
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


}
