package me.xnmk.community.controller;

import com.google.code.kaptcha.Producer;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.ActivationStates;
import me.xnmk.community.enumeration.TicketTtl;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.MailClient;
import me.xnmk.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 17:01
 * @Description: 登录接口
 */
@Controller
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 跳转至注册页面
     *
     * @return 路径
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 跳转至登录页面
     *
     * @return 路径
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 跳转至忘记密码页面
     *
     * @return 路径
     */
    @GetMapping("/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    /**
     * 注册用户
     *
     * @param model 模板
     * @param user  用户信息(用户名、密码、邮箱)
     * @return ModelAndView
     */
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        // 提示信息为空则注册成功，有值则失败
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 激活用户
     *
     * @param model  模板
     * @param userId 用户id
     * @param code   激活码
     * @return ModelAndView
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int activationState = userService.activation(userId, code);
        if (activationState == ActivationStates.ACTIVATION_SUCCESS.getCode()) {
            model.addAttribute("msg", "激活成功，您的账号可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (activationState == ActivationStates.ACTIVATION_REPEAT.getCode()) {
            model.addAttribute("msg", "无效操作，该账号已经被激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 生成验证码
     *
     * @param response 响应
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入Session（用于验证）
        // session.setAttribute("kaptcha", text);

        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     *
     * @param username 账号
     * @param password 密码
     * @param code     验证码
     * @param remember 记住我
     * @param model    模板
     * @param response 响应
     * @return ModelAndView
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean remember,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 检查验证码
        // String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        // 检查账号密码
        // 凭证存活时长
        int expiredSecond = remember ?
                TicketTtl.REMEMBER_EXPIRED_SECOND.getExpiredSecond() :
                TicketTtl.DEFAULT_EXPIRED_SECOND.getExpiredSecond();
        Map<String, Object> map = userService.login(username, password, expiredSecond);
        // 是否登录成功（服务层是否返回凭证）
        if (map.containsKey("ticket")) {
            // 返回ticket
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            // 返回错误提示信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 退出登录
     *
     * @param ticket 登录凭证
     * @return 路径
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    /**
     * 获得修改密码验证码
     *
     * @param email   邮箱
     * @param session 会话
     * @return ajax
     */
    @GetMapping("/forget/code")
    @ResponseBody
    public String getForgetCode(String email, HttpSession session) {
        // 检查邮箱是否为空
        if (StringUtils.isBlank(email)) {
            return CommunityUtil.getJsonString(1, "邮箱不能为空！");
        }

        // 发送邮箱
        Context context = new Context();
        context.setVariable("email", email);
        String code = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("verifyCode", code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "找回密码", content);

        // 保存验证码
        session.setAttribute("verifyCode", code);

        return CommunityUtil.getJsonString(0, "验证码成功发送");
    }

    /**
     * 重置密码
     *
     * @param email      邮箱
     * @param verifyCode 验证码
     * @param password   新密码
     * @param session    会话
     * @param model      模板
     * @return ModelAndView
     */
    @PostMapping("/forget/password")
    public String resetPassword(String email, String verifyCode, String password, HttpSession session, Model model) {
        // 检查验证码
        String code = (String) session.getAttribute("verifyCode");
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(verifyCode)) {
            model.addAttribute("codeMsg", "验证码错误!");
            return "/site/forget";
        }
        // 重置密码
        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }
}
