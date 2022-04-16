package me.xnmk.community.service;

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
}
