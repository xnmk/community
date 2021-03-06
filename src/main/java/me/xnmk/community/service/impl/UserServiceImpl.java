package me.xnmk.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import me.xnmk.community.dao.LoginTicketMapper;
import me.xnmk.community.dao.UserMapper;
import me.xnmk.community.entity.LoginTicket;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.ActivationStates;
import me.xnmk.community.enumeration.TicketStatus;
import me.xnmk.community.enumeration.UserPermissions;
import me.xnmk.community.enumeration.UserStatus;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.MailClient;
import me.xnmk.community.util.RedisKeyUtil;
import me.xnmk.community.util.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:15
 * @Description: UserService接口实现
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;
    // @Autowired
    // private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private UserThreadLocal userThreadLocal;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int id) {
        User user = getUserCache(id);
        if (user == null) {
            user = initUserCache(id);
        }
        return user;
    }

    @Override
    public User findUserByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, name);
        return userMapper.selectOne(queryWrapper);
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
            clearUserCache(userId);
            return ActivationStates.ACTIVATION_SUCCESS.getCode();
        }else {
            return ActivationStates.ACTIVATION_SUCCESS.getCode();
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSecond) {
        Map<String, Object> map = new HashMap<>();
        // 空值判断
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        // 验证激活状态
        if (user.getStatus() == UserStatus.USER_UNACTIVATED.getCode()){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("usernameMsg", "密码不正确！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(TicketStatus.TICKET_VALID.getCode());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecond * 1000));
        // loginTicketMapper.insert(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        // 返回登录凭证给用户
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
        // loginTicketMapper.updateStatus(ticket, TicketStatus.TICKET_INVALID.getCode());
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        // LambdaQueryWrapper<LoginTicket> queryWrapper = new LambdaQueryWrapper<>();
        // queryWrapper.eq(LoginTicket::getTicket, ticket);
        // LoginTicket loginTicket = loginTicketMapper.selectOne(queryWrapper);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        User user = new User();
        user.setHeaderUrl(headerUrl);
        user.setId(userId);
        int rows = userMapper.updateById(user);
        clearUserCache(userId);
        return rows;
    }

    @Override
    public Map<String, Object> modifyPassword(String originalPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        // 空值判断
        if (StringUtils.isBlank(originalPassword)){
            map.put("oriPasswordMsg", "原密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新密码不能为空！");
            return map;
        }

        // 原密码是否正确
        User user = userThreadLocal.getUser();
        originalPassword = CommunityUtil.md5(originalPassword + user.getSalt());
        if (!originalPassword.equals(user.getPassword())){
            // 不正确：返回提示信息
            map.put("oriPasswordMsg", "原密码不正确！");
            return map;
        }else {
            // 正确：更改密码
            newPassword = CommunityUtil.md5(newPassword + user.getSalt());
            userMapper.updatePasswordById(newPassword, user.getId());
            clearUserCache(user.getId());
            return map;
        }
    }

    @Override
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证邮箱
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未注册!");
            return map;
        }

        // 重置密码
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePasswordById(password, user.getId());
        clearUserCache(user.getId());
        map.put("user", user);

        return map;
    }

    public User getUserCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    @Override
    public User initUserCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    @Override
    public void clearUserCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return UserPermissions.AUTHORITY_ADMIN.getCode();
                    case 2:
                        return UserPermissions.AUTHORITY_MODERATOR.getCode();
                    default:
                        return UserPermissions.AUTHORITY_USER.getCode();
                }
            }
        });
        return list;
    }
}
