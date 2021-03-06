package me.xnmk.community.controller;

import me.xnmk.community.entity.Comment;
import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.entity.Event;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.CommentTypes;
import me.xnmk.community.enumeration.CommunityConstant;
import me.xnmk.community.enumeration.DiscussStatus;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.event.EventProducer;
import me.xnmk.community.service.*;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.RedisKeyUtil;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.CommentVo;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.Result;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-22 11:45
 * @Description: 帖子接口
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private UserThreadLocal userThreadLocal;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加帖子
     *
     * @param title   标题
     * @param content 内容
     * @return json(code, description)
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = userThreadLocal.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "你还没有权限登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPostService.addDiscussPost(discussPost);

        // 触发发帖事件：存入 es
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityId(discussPost.getId())
                .setEntityType(EntityTypes.ENTITY_TYPE_POST.getCode());
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, discussPost.getId());

        return CommunityUtil.getJsonString(200, "发布成功！");
    }

    /**
     * 帖子信息
     *
     * @param discussPortId 帖子id
     * @param model         模板
     * @param pageParams    分页参数
     * @return ModelAndView
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPortId, Model model, PageParams pageParams) {
        User user = userThreadLocal.getUser();
        // 帖子
        DiscussPostVo discussPost = discussPostService.findDiscussPostById(discussPortId);
        model.addAttribute("post", discussPost);
        // 作者
        User author = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", author);
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(EntityTypes.ENTITY_TYPE_POST.getCode(), discussPortId);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus = user == null ? 0 : likeService.findEntityLikeStatus(user.getId(), EntityTypes.ENTITY_TYPE_POST.getCode(), discussPost.getId());
        model.addAttribute("likeStatus", likeStatus);
        // 评论
        pageParams.setLimit(5);
        pageParams.setPath("/discuss/detail/" + discussPortId);
        pageParams.setRows(discussPost.getCommentCount());
        List<CommentVo> commentVoList = commentService.findCommentVosByEntity(
                CommentTypes.ENTITY_TYPE_POST.getCode(), discussPortId, pageParams);

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

    /**
     * 置顶
     *
     * @param id 帖子id
     * @return Result
     */
    @PostMapping("/top")
    @ResponseBody
    public Result setTop(int id) {
        User user = userThreadLocal.getUser();
        discussPostService.updateType(id, DiscussStatus.DISCUSS_TYPES_TOP.getCode());

        // 触发发帖事件：存入 es
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityId(id)
                .setEntityType(EntityTypes.ENTITY_TYPE_POST.getCode());
        eventProducer.fireEvent(event);

        return Result.success(200);
    }

    /**
     * 加精
     *
     * @param id 帖子id
     * @return Result
     */
    @PostMapping("/essence")
    @ResponseBody
    public Result setEssence(int id) {
        User user = userThreadLocal.getUser();
        discussPostService.updateStatus(id, DiscussStatus.DISCUSS_STATUS_ESSENCE.getCode());

        // 触发发帖事件：存入 es
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityId(id)
                .setEntityType(EntityTypes.ENTITY_TYPE_POST.getCode());
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return Result.success(200);
    }

    /**
     * 拉黑
     *
     * @param id 帖子id
     * @return Result
     */
    @PostMapping("/delete")
    @ResponseBody
    public Result setDelete(int id) {
        User user = userThreadLocal.getUser();
        discussPostService.updateStatus(id, DiscussStatus.DISCUSS_STATUS_DELETE.getCode());

        // 触发删帖事件：存入 es
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(user.getId())
                .setEntityId(id)
                .setEntityType(EntityTypes.ENTITY_TYPE_POST.getCode());
        eventProducer.fireEvent(event);

        return Result.success(200);
    }
}
