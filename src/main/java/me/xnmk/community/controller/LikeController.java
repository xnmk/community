package me.xnmk.community.controller;

import me.xnmk.community.entity.Event;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.CommunityConstant;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.event.EventProducer;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.util.RedisKeyUtil;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.LikeVo;
import me.xnmk.community.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author:xnmk_zhan
 * @create:2022-04-28 10:15
 * @Description: 点赞接口
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;
    @Autowired
    private UserThreadLocal userThreadLocal;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     *
     * @param entityType 点赞目标实体类型
     * @param entityId 点赞目标实体id
     * @param entityUserId 点赞目标实体所属用户id
     * @param postId 点赞发生的帖子id
     * @return Result
     */
    @PostMapping("/like")
    @ResponseBody
    public Result like(int entityType, int entityId, int entityUserId, int postId){
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
        // 返回数据
        LikeVo likeVo = new LikeVo();
        likeVo.setLikeCount(likeCount);
        likeVo.setLikeStatus(likeStatus);

        // 触发事件：系统消息
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        if (entityType == EntityTypes.ENTITY_TYPE_POST.getCode()) {
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return Result.success(likeVo);
    }
}
