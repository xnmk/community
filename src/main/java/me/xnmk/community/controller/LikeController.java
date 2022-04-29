package me.xnmk.community.controller;

import me.xnmk.community.entity.User;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.LikeVo;
import me.xnmk.community.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author:xnmk_zhan
 * @create:2022-04-28 10:15
 * @Description: 点赞接口
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    @PostMapping("/like")
    @ResponseBody
    public Result like(int entityType, int entityId, int entityUserId){
        User user = userThreadLocal.getUser();
        if (user == null) {
            Result.fail(503, "你还没登录，登录后才可进行点赞");
        }
        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        LikeVo likeVo = new LikeVo();
        likeVo.setLikeCount(likeCount);
        likeVo.setLikeStatus(likeStatus);

        return Result.success(likeVo);
    }
}
