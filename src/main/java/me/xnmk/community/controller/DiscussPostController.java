package me.xnmk.community.controller;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.entity.User;
import me.xnmk.community.service.DiscussPortService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * @author:xnmk_zhan
 * @create:2022-04-22 11:45
 * @Description: 帖子接口
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPortService discussPortService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserThreadLocal userThreadLocal;

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
        discussPortService.addDiscussPost(discussPost);

        return CommunityUtil.getJsonString(200, "发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPortId, Model model) {
        // 帖子
        DiscussPost discussPost = discussPortService.findDiscussPostById(discussPortId);
        model.addAttribute("post", discussPost);
        // 作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}
