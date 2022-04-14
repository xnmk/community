package me.xnmk.community.controller;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.entity.User;
import me.xnmk.community.service.DiscussPortService;
import me.xnmk.community.service.UserService;
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
    private DiscussPortService discussPortService;

    @GetMapping("/index")
    public String getIndexPage(Model model, PageParams pageParams) {
        // SpringMVC会自动实例化Model和PageParams，并将pageParams注入Model.
        // 所以，在thymeleaf种可以直接访问PageParams对象种的数据
        pageParams.setRows(discussPortService.findDiscussPortRows(0));
        pageParams.setPath("/index");

        List<DiscussPost> discussPostList = discussPortService.findDiscussPosts(0, pageParams);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (discussPostList != null) {
            for (DiscussPost discussPost : discussPostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }
}
