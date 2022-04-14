package me.xnmk.community.service;

import me.xnmk.community.entity.User;

public interface UserService {

    /**
     * 根据用户id查找用户
     * @param id
     * @return me.xnmk.community.entity.User
     */
    User findUserById(int id);
}
