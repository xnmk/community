package me.xnmk.community.controller;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.CommentTypes;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.DiscussPostService;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:29
 * @Description: 首页接口
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 访问主页并将帖子信息返回
     *
     * @param model      模板
     * @param pageParams 分页信息
     * @return ModelAndView
     */
    @GetMapping("/index")
    public String getIndexPage(Model model, PageParams pageParams) {
        // SpringMVC会自动实例化Model和PageParams，并将pageParams注入Model.
        // 所以，在thymeleaf种可以直接访问PageParams对象种的数据
        pageParams.setRows(discussPostService.findDiscussPortRows(0));
        pageParams.setPath("/index");

        List<DiscussPostVo> discussPostList = discussPostService.findDiscussPosts(0, pageParams, false);
        // 存放用户及帖子信息
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (discussPostList != null) {
            for (DiscussPostVo discussPost : discussPostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                long likeCount = likeService.findEntityLikeCount(EntityTypes.ENTITY_TYPE_POST.getCode(), discussPost.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    /**
     * 跳转至错误界面
     *
     * @return ModelAndView
     */
    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    /**
     * 跳转无权限提示页面
     *
     * @return  ModelAndView
     */
    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
