package me.xnmk.community.util;

import me.xnmk.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author:xnmk_zhan
 * @create:2022-04-18 09:57
 * @Description: 使用ThreadLocal持有用户信息
 */
@Component
public class UserThreadLocal {

    private ThreadLocal<User> localUser = new ThreadLocal<>();

    public void setUser(User user){
        localUser.set(user);
    }

    public User getUser(){
        return localUser.get();
    }

    public void clear(){
        localUser.remove();
    }
}
