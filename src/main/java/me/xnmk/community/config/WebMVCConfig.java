package me.xnmk.community.config;

import me.xnmk.community.entity.Message;
import me.xnmk.community.handler.LoginRequiredInterceptor;
import me.xnmk.community.handler.LoginTicketInterceptor;
import me.xnmk.community.handler.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author:xnmk_zhan
 * @create:2022-04-18 09:26
 * @Description: MVC配置
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private MessageInterceptor messageInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 携带登录凭证拦截器：拦截所有路径
        registry.addInterceptor(loginTicketInterceptor)
                // 排除静态资源
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        // // 登录拦截器：拦截所有路径
        // registry.addInterceptor(loginRequiredInterceptor)
        //         // 排除静态资源
        //         .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        // 消息拦截器：拦截所有路径
        registry.addInterceptor(messageInterceptor)
                // 排除静态资源
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
