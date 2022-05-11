package me.xnmk.community.handler;

import me.xnmk.community.entity.LoginTicket;
import me.xnmk.community.entity.User;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CookieUtil;
import me.xnmk.community.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-04-18 09:36
 * @Description: 使用拦截器在每次请求前根据登录凭证获得用户信息
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 cookie 获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null){
            // 查询凭证信息
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效（不为空、状态、超时时间）
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求持有用户
                userThreadLocal.setUser(user);
                // 构造用户认证结果，并存入 SecurityContext，以便于 SpringSecurity 进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId())
                );
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 获得用户信息存入 ModelAndView
        User user = userThreadLocal.getUser();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理 userThreadLocal
        userThreadLocal.clear();
    }
}
