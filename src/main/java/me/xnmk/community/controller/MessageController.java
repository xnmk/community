package me.xnmk.community.controller;

import me.xnmk.community.entity.Message;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.MessageStatus;
import me.xnmk.community.service.MessageService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.CommunityUtil;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.MessageVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
     * @param pageParams     分页参数
     * @param model          模板
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
        // 设置私信已读
        List<Integer> idList = new ArrayList<>();
        if (letters != null) {
            for (MessageVo letter : letters) {
                if (user.getId() == letter.getToId() && letter.getStatus() == MessageStatus.MESSAGE_UNREAD.getCode()) {
                    idList.add(letter.getId());
                }
            }
        }
        if (!idList.isEmpty()) {
            messageService.readMessage(idList);
        }

        return "/site/letter-detail";
    }

    /**
     * 发送私信给指定用户
     *
     * @param toName  私信目标
     * @param content 内容
     * @return ajax
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User toUser = userService.findUserByName(toName);
        if (toName == null) {
            return CommunityUtil.getJsonString(1, "目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(userThreadLocal.getUser().getId());
        message.setToId(toUser.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        messageService.addMessage(message);

        return CommunityUtil.getJsonString(0);
    }

    @PostMapping("/letter/delete")
    @ResponseBody
    public String deleteLetter(int id){
        messageService.deleteMessage(id);
        return CommunityUtil.getJsonString(0);
    }
}
