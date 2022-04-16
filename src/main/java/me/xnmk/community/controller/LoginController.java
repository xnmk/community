package me.xnmk.community.controller;

import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.ActivationStates;
import me.xnmk.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 17:01
 * @Description: 登录接口
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

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
}
