package me.xnmk.community.service.impl;

import me.xnmk.community.dao.UserMapper;
import me.xnmk.community.entity.User;
import me.xnmk.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:15
 * @Description: UserService接口实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
