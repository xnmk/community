package me.xnmk.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.MessageMapper;
import me.xnmk.community.entity.Message;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.MessageStatus;
import me.xnmk.community.service.MessageService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.SensitiveFilter;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.MessageVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-26 12:36
 * @Description: MessageServiceImpl
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserThreadLocal userThreadLocal;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<MessageVo> findConversations(int userId, PageParams pageParams) {
        List<Message> messageList = messageMapper.selectConversations(userId, pageParams.getOffset(), pageParams.getLimit());
        return copyList(messageList, true);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<MessageVo> findLetters(String conversationId, PageParams pageParams) {
        Page<Message> page = new Page<>(pageParams.getCurrent(), pageParams.getLimit());
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        // 排除删除状态
        queryWrapper.ne(Message::getStatus, 2);
        // 排除系统通知
        queryWrapper.ne(Message::getFromId, 1);
        queryWrapper.eq(Message::getConversationId, conversationId);
        queryWrapper.orderByDesc(Message::getId);
        messageMapper.selectPage(page, queryWrapper);
        List<Message> records = page.getRecords();
        return copyList(records, false);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public int addMessage(Message message) {
        // 过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, MessageStatus.MESSAGE_READ.getCode());
    }

    @Override
    public int deleteMessage(int MessageId) {
        List<Integer> list = new ArrayList<>();
        list.add(MessageId);
        return messageMapper.updateStatus(list, MessageStatus.MESSAGE_DELETE.getCode());
    }

    /**
     * @param messageList    会话、私信列表
     * @param isConversation 是否为会话
     * @return List<MessageVo>
     */
    public List<MessageVo> copyList(List<Message> messageList, boolean isConversation) {
        List<MessageVo> messageVoList = new ArrayList<>();
        for (Message message : messageList) {
            messageVoList.add(copy(message, isConversation));
        }
        return messageVoList;
    }

    /**
     * @param message        会话、私信
     * @param isConversation 是否为会话
     * @return MessageVo
     */
    public MessageVo copy(Message message, boolean isConversation) {
        MessageVo messageVo = new MessageVo();
        BeanUtils.copyProperties(message, messageVo);
        if (isConversation) {
            // 获取当前用户
            User user = userThreadLocal.getUser();
            // 会话内消息数量
            messageVo.setLetterCount(messageMapper.selectLetterCount(message.getConversationId()));
            // 会话内未读消息数量
            messageVo.setUnreadCount(messageMapper.selectLetterUnreadCount(user.getId(), message.getConversationId()));
            // 会话目标用户
            int targetUserId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
            messageVo.setTargetUser(userService.findUserById(targetUserId));
        } else {
            // 该条私信发送者
            messageVo.setFromUser(userService.findUserById(message.getFromId()));
        }
        return messageVo;
    }
}
