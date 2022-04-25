package me.xnmk.community.controller;

import me.xnmk.community.entity.Comment;
import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.CommentTypes;
import me.xnmk.community.service.CommentService;
import me.xnmk.community.service.DiscussPostService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.CommentVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    /**
     * 添加帖子
     *
     * @param title 标题
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

        return CommunityUtil.getJsonString(200, "发布成功！");
    }

    /**
     * 帖子信息
     *
     * @param discussPortId 帖子id
     * @param model 模板
     * @param pageParams 分页参数
     * @return ModelAndView
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPortId, Model model, PageParams pageParams) {
        // 帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPortId);
        model.addAttribute("post", discussPost);
        // 作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);
        // 评论
        pageParams.setLimit(5);
        pageParams.setPath("/discuss/detail/" + discussPortId);
        pageParams.setRows(discussPost.getCommentCount());
        List<CommentVo> commentVoList = commentService.findCommentVosByEntity(
                CommentTypes.ENTITY_TYPE_POST.getCode(), discussPortId, pageParams);

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }


}
