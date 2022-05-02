package me.xnmk.community.vo;

import me.xnmk.community.entity.User;

import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-05-02 20:55
 * @Description: FollowVo
 */
public class FollowVo {

    // 用户信息
    private User user;
    // 关注时间
    private Date followTime;
    // 当前用户是否关注
    private boolean hasFollowed;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getFollowTime() {
        return followTime;
    }

    public void setFollowTime(Date followTime) {
        this.followTime = followTime;
    }

    public boolean isHasFollowed() {
        return hasFollowed;
    }

    public void setHasFollowed(boolean hasFollowed) {
        this.hasFollowed = hasFollowed;
    }
}
