package me.xnmk.community.handler;

import me.xnmk.community.annotation.LoginRequired;
import me.xnmk.community.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author:xnmk_zhan
 * @create:2022-04-18 19:05
 * @Description: 登录拦截器
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private UserThreadLocal userThreadLocal;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截的是方法
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取拦截的方法对象
            Method method = handlerMethod.getMethod();
            // 取到方法的注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 有@loginRequired但没登录
            if (loginRequired != null && userThreadLocal.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");
                System.out.println(request.getContextPath());
                return false;
            }
        }
        return true;
    }
}
