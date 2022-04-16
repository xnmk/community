package me.xnmk.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.xnmk.community.dao.UserMapper;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.ActivationStates;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:15
 * @Description: UserService接口实现
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值判断（用户对象、账号、密码、邮箱）
        if (user == null) throw new IllegalArgumentException("参数不能为空！");
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 验证账号
        queryWrapper.eq(User::getUsername, user.getUsername());
        User userFromDB = userMapper.selectOne(queryWrapper);
        if (userFromDB != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        // 验证邮箱
        queryWrapper.clear();
        queryWrapper.eq(User::getEmail, user.getEmail());
        userFromDB = userMapper.selectOne(queryWrapper);
        if (userFromDB != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        // 注册用户
        // 完善用户信息（盐、加密密码、类型、状态、激活码、头像）
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        // 头像使用牛客网提供的路径
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userMapper.insert(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活邮箱路径：http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) return ActivationStates.ACTIVATION_REPEAT.getCode();
        if (user.getActivationCode().equals(code)){
            user.setStatus(1);
            userMapper.updateById(user);
            return ActivationStates.ACTIVATION_SUCCESS.getCode();
        }else {
            return ActivationStates.ACTIVATION_SUCCESS.getCode();
        }
    }
}
