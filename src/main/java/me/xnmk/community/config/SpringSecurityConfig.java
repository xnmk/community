package me.xnmk.community.config;

import com.alibaba.fastjson.JSON;
import me.xnmk.community.enumeration.UserPermissions;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.vo.Result;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author:xnmk_zhan
 * @create:2022-05-10 22:21
 * @Description: Spring-Security-Config
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 请求权限设置
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        UserPermissions.AUTHORITY_USER.getCode(),
                        UserPermissions.AUTHORITY_ADMIN.getCode(),
                        UserPermissions.AUTHORITY_MODERATOR.getCode()
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/essence"
                )
                .hasAnyAuthority(
                        UserPermissions.AUTHORITY_ADMIN.getCode(),
                        UserPermissions.AUTHORITY_MODERATOR.getCode()
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                ).hasAnyAuthority(
                        UserPermissions.AUTHORITY_ADMIN.getCode()
                )
                .anyRequest().permitAll()
                // 禁用 csrf
                .and().csrf().disable();

        // 权限不足设置
        http.exceptionHandling()
                // 未登录处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestWith = request.getHeader("x-requested-with");
                        // 判断是同步还是异步
                        if ("XMLHttpRequest".equals(xRequestWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSON.toJSONString(Result.fail(403, "你还没有登录哦！")));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                // 权限不足处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestWith = request.getHeader("x-requested-with");
                        // 判断是同步还是异步
                        if ("XMLHttpRequest".equals(xRequestWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSON.toJSONString(Result.fail(403, "你还没有访问此功能的权限！")));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // SpringSecurity 底层会自动拦截 /logout 请求进行退出处理
        // 覆盖它默认的逻辑
        http.logout()
                .logoutUrl("securityLogout");
    }
}
