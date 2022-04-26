package me.xnmk.community.controller;

import me.xnmk.community.entity.User;
import me.xnmk.community.service.MessageService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.MessageVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-26 12:54
 * @Description: Message接口
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserThreadLocal userThreadLocal;

    /**
     * 会话列表
     *
     * @param model      模板
     * @param pageParams 分页参数
     * @return ModelAndView
     */
    @GetMapping("/letter/list")
    public String getLitterList(Model model, PageParams pageParams) {
        User user = userThreadLocal.getUser();
        // 分页信息
        pageParams.setLimit(5);
        pageParams.setPath("/letter/list");
        pageParams.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<MessageVo> conversationList = messageService.findConversations(user.getId(), pageParams);
        model.addAttribute("conversations", conversationList);
        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    /**
     * 私信列表
     *
     * @param conversationId 会话id
     * @param pageParams 分页参数
     * @param model 模板
     * @return ModelAndView
     */
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, PageParams pageParams, Model model) {
        User user = userThreadLocal.getUser();
        // 分页设置
        pageParams.setLimit(5);
        pageParams.setPath("/letter/detail/" + conversationId);
        pageParams.setRows(messageService.findLetterCount(conversationId));
        // 私信列表
        List<MessageVo> letters = messageService.findLetters(conversationId, pageParams);
        model.addAttribute("letters", letters);
        // 私信目标
        String[] ids = conversationId.split("_");
        int id1 = Integer.parseInt(ids[0]);
        int id2 = Integer.parseInt(ids[1]);
        if (user.getId() == id1) {
            model.addAttribute("targetUser", userService.findUserById(id2));
        } else {
            model.addAttribute("targetUser", userService.findUserById(id1));
        }

        return "/site/letter-detail";
    }
}
