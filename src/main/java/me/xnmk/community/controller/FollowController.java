package me.xnmk.community.controller;

import me.xnmk.community.entity.User;
import me.xnmk.community.service.FollowService;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author:xnmk_zhan
 * @create:2022-05-02 18:54
 * @Description: 关注接口
 */
@Controller
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    /**
     * 关注
     * @param entityType 关注实体类型
     * @param entityId 关注实体id
     * @return me.xnmk.community.vo.Result
     */
    @PostMapping("/follow")
    @ResponseBody
    public Result follow(int entityType, int entityId) {
        User user = userThreadLocal.getUser();
        followService.follow(user.getId(), entityType, entityId);
        return Result.success("已关注！", null);
    }

    /**
     * 取消关注
     * @param entityType 关注实体类型
     * @param entityId 关注实体id
     * @return me.xnmk.community.vo.Result
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public Result unfollow(int entityType, int entityId) {
        User user = userThreadLocal.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return Result.success("已取消关注！", null);
    }
}
