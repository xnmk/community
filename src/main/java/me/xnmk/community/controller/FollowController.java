package me.xnmk.community.controller;

import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.FollowService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.FollowVo;
import me.xnmk.community.vo.Result;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-05-02 18:54
 * @Description: 关注接口
 */
@Controller
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    /**
     * 关注
     *
     * @param entityType 关注实体类型
     * @param entityId   关注实体id
     * @return me.xnmk.community.vo.Result
     */
    @PostMapping("/follow")
    @ResponseBody
    public Result follow(int entityType, int entityId) {
        User user = userThreadLocal.getUser();
        followService.follow(user.getId(), entityType, entityId);
        return Result.success("已关注！", null);
    }

    /**
     * 取消关注
     *
     * @param entityType 关注实体类型
     * @param entityId   关注实体id
     * @return me.xnmk.community.vo.Result
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public Result unfollow(int entityType, int entityId) {
        User user = userThreadLocal.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return Result.success("已取消关注！", null);
    }

    /**
     * 查询用户关注列表
     *
     * @param userId     用户id
     * @param pageParams 分页参数
     * @param model      模板
     * @return ModelAndView
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, PageParams pageParams, Model model) {
        User loginUser = userThreadLocal.getUser();
        User user = userService.findUserById(userId);
        // 用户是否存在
        if (user == null) throw new RuntimeException("该用户不存在！");
        model.addAttribute("user", user);
        // 分页设置
        pageParams.setLimit(5);
        pageParams.setPath("/followees/" + userId);
        pageParams.setRows((int) followService.findFolloweeCount(EntityTypes.ENTITY_TYPE_USER.getCode(), userId));
        // 查询
        List<FollowVo> followees = followService.findFollowees(userId, pageParams.getOffset(), pageParams.getLimit());
        model.addAttribute("followees", followees);

        return "/site/followee";
    }

    /**
     * 查询用户粉丝列表
     *
     * @param userId     用户id
     * @param pageParams 分页参数
     * @param model      模板
     * @return ModelAndView
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, PageParams pageParams, Model model) {
        User loginUser = userThreadLocal.getUser();
        User user = userService.findUserById(userId);
        // 用户是否存在
        if (user == null) throw new RuntimeException("该用户不存在！");
        model.addAttribute("user", user);
        // 分页设置
        pageParams.setLimit(5);
        pageParams.setPath("/followers/" + userId);
        pageParams.setRows((int) followService.findFollowerCount(EntityTypes.ENTITY_TYPE_USER.getCode(), userId));
        // 查询
        List<FollowVo> followers = followService.findFollowers(userId, pageParams.getOffset(), pageParams.getLimit());
        model.addAttribute("followers", followers);

        return "/site/follower";
    }
}
