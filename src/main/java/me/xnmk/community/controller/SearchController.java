package me.xnmk.community.controller;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.service.ElasticsearchService;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-05-09 17:02
 * @Description: SearchController
 */
@Controller
public class SearchController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping("/search")
    public String search(String keyword, PageParams pageParams, Model model) {
        // 设置分页信息
        pageParams.setPath("/search?keyword=" + keyword);
        // 帖子数量
        long postCount = elasticsearchService.getDiscussPostCount(keyword);
        pageParams.setRows((int) postCount);

        // 搜索帖子
        if (postCount != 0) {
            // 由于 es 搜索接口分页页码参数是从 0 开始，要减 1
            List<DiscussPostVo> discussPosts = elasticsearchService.searchDiscussPost(keyword, pageParams.getCurrent() - 1, pageParams.getLimit());
            model.addAttribute("discussPosts", discussPosts);
        }

        model.addAttribute("keyword", keyword);

        return "/site/search";
    }
}
