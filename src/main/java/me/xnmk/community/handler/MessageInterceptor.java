package me.xnmk.community.handler;

import me.xnmk.community.entity.User;
import me.xnmk.community.service.MessageService;
import me.xnmk.community.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:xnmk_zhan
 * @create:2022-05-06 14:23
 * @Description: 消息拦截器（用于返回未读消息数量（私信 + 通知））
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private UserThreadLocal userThreadLocal;
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = userThreadLocal.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
