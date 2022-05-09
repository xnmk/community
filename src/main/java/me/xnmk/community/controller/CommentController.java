package me.xnmk.community.controller;

import me.xnmk.community.entity.Comment;
import me.xnmk.community.entity.Event;
import me.xnmk.community.enumeration.CommunityConstant;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.event.EventProducer;
import me.xnmk.community.service.CommentService;
import me.xnmk.community.service.DiscussPostService;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.CommentParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author:xnmk_zhan
 * @create:2022-04-25 15:47
 * @Description: Comment接口
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserThreadLocal userThreadLocal;
    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加评论
     *
     * @param discussPostId 帖子id
     * @param commentParams 评论参数
     * @return /discuss/detail/discussPostId
     */
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, CommentParams commentParams) {
        // commentParams -> comment
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentParams, comment);
        comment.setUserId(userThreadLocal.getUser().getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        // 触发评论事件：系统消息、存入 es
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(userThreadLocal.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == EntityTypes.ENTITY_TYPE_POST.getCode()) {
            // 评论实体为帖子
            DiscussPostVo target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == EntityTypes.ENTITY_TYPE_COMMENT.getCode()) {
            // 评论实体为评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == EntityTypes.ENTITY_TYPE_POST.getCode()) {
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getId())
                    .setEntityId(discussPostId)
                    .setEntityType(EntityTypes.ENTITY_TYPE_POST.getCode());
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
