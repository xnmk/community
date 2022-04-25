package me.xnmk.community.controller;

import me.xnmk.community.entity.Comment;
import me.xnmk.community.service.CommentService;
import me.xnmk.community.util.UserThreadLocal;
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
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserThreadLocal userThreadLocal;

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
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
