package me.xnmk.community.service;

import me.xnmk.community.entity.LoginTicket;
import me.xnmk.community.entity.User;

import java.util.Map;

public interface UserService {

    /**
     * 根据用户id查找用户
     *
     * @param id 用户id
     * @return me.xnmk.community.entity.User
     */
    User findUserById(int id);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return me.xnmk.community.entity.User
     */
    User findUserByName(String name);

    /**
     * 注册用户，返回提示信息
     *
     * @param user 携带注册用户的信息
     * @return 提示信息
     */
    Map<String, Object> register(User user);

    /**
     * 激活用户
     *
     * @param userId 用户id
     * @param code   激活码
     * @return 激活状态
     */
    int activation(int userId, String code);

    /**
     * 用户登录
     *
     * @param username      用户名
     * @param password      密码
     * @param expiredSecond 凭证过期时间
     * @return 提示信息
     */
    Map<String, Object> login(String username, String password, int expiredSecond);

    /**
     * 退出
     *
     * @param ticket 登录凭证
     */
    void logout(String ticket);

    /**
     * 根据凭证查询凭证信息
     *
     * @param ticket 凭证
     * @return 凭证信息
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 更新头像路径
     *
     * @param userId    用户id
     * @param headerUrl 头像路径
     * @return int
     */
    int updateHeader(int userId, String headerUrl);

    /**
     * 修改密码
     *
     * @param originalPassword 原密码
     * @param newPassword      新密码
     * @return 提示信息
     */
    Map<String, Object> modifyPassword(String originalPassword, String newPassword);

    /**
     * 重置密码（忘记密码）
     *
     * @param email    邮箱
     * @param password 密码
     * @return 提示信息
     */
    Map<String, Object> resetPassword(String email, String password);

    /**
     * 获得缓存用户信息
     *
     * @param userId 用户id
     * @return me.xnmk.community.entity.User
     */
    User getUserCache(int userId);

    /**
     * 更新缓存用户信息
     *
     * @param userId 用户id
     * @return me.xnmk.community.entity.User
     */
    User initUserCache(int userId);

    /**
     * 清除缓存
     *
     * @param userId 用户id
     */
    void clearUserCache(int userId);
}
