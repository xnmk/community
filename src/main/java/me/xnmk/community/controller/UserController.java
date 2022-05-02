package me.xnmk.community.controller;

import me.xnmk.community.annotation.LoginRequired;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.FollowService;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-04-18 10:38
 * @Description: 用户接口
 */
@Configuration
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 跳转至用户设置页面
     *
     * @return 路径
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传头像到本地存储
     *
     * @param headerImage 图片
     * @param model       模板
     * @return ModelAndView or 首页路径
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        // 检查上传文件是否为空
        if (headerImage == null) {
            model.addAttribute("error", "你还没有选择图片");
            return "/site/setting";
        }
        // 检查上传文件格式
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }
        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放路径
        File dest = new File(uploadPath + "/" + filename);
        // 上传文件
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }
        // 更新当前用户的头像访问路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = userThreadLocal.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 获取头像
     *
     * @param fileName 图片名称
     * @param response 响应
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = fis.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     *
     * @param ticket           登录凭证
     * @param originalPassword 原密码
     * @param newPassword      新密码
     * @param model            模板
     * @return 重定向 or ModelAndView
     */
    @PostMapping("/password/modify")
    public String modifyPassword(@CookieValue("ticket") String ticket, String originalPassword, String newPassword, Model model) {
        Map<String, Object> map = userService.modifyPassword(originalPassword, newPassword);
        // 是否修改成功
        if (map.containsKey("oriPasswordMsg")) {
            model.addAttribute("oriPasswordMsg", map.get("oriPasswordMsg"));
            return "/site/setting";
        } else if (map.containsKey("newPasswordMsg")) {
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        } else {
            // 修改成功
            // 退出登录，修改凭证状态，重定向登录页面
            userService.logout(ticket);
            return "redirect:/login";
        }
    }

    /**
     * 用户主页
     *
     * @param userId 用户id
     * @param model 模板
     * @return ModelAndView
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User loginUser = userThreadLocal.getUser();
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, EntityTypes.ENTITY_TYPE_USER.getCode());
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(EntityTypes.ENTITY_TYPE_USER.getCode(), userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (loginUser != null) {
            hasFollowed = followService.hasFollowed(loginUser.getId(), EntityTypes.ENTITY_TYPE_USER.getCode(), userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
