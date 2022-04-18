package me.xnmk.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author:xnmk_zhan
 * @create:2022-04-18 09:38
 * @Description: 关于Cookie工具类
 */
public class CookieUtil {

    /**
     * 从request获取指定Cookie的值
     *
     * @param request 请求
     * @param name    CookieName
     * @return Cookie.value
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
