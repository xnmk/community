package me.xnmk.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.MessageMapper;
import me.xnmk.community.entity.Message;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.CommunityConstant;
import me.xnmk.community.enumeration.MessageStatus;
import me.xnmk.community.service.MessageService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.SensitiveFilter;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.MessageVo;
import me.xnmk.community.vo.SystemMessageVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:xnmk_zhan
 * @create:2022-04-26 12:36
 * @Description: MessageServiceImpl
 */
@Service
public class MessageServiceImpl implements MessageService, CommunityConstant {

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

    @Override
    public SystemMessageVo findLatestNotice(int userId, String topic) {
        Message message = messageMapper.selectLatestNotice(userId, topic);
        if (message == null) return null;
        return copySystemMessage(message, false);
    }

    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    @Override
    public List<SystemMessageVo> findNotices(int userId, String topic, PageParams pageParams) {
        Page<Message> page = new Page<>(pageParams.getCurrent(), pageParams.getLimit());
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Message::getStatus, MessageStatus.MESSAGE_DELETE.getCode());
        queryWrapper.eq(Message::getFromId, SYSTEM_USER_ID);
        queryWrapper.eq(Message::getToId, userId);
        queryWrapper.eq(Message::getConversationId, topic);
        queryWrapper.orderByDesc(Message::getCreateTime);
        messageMapper.selectPage(page, queryWrapper);
        return copySystemMessageList(page.getRecords(), true);
    }

    /**
     * @param messageList    会话、私信
     * @param isConversation 是否为会话
     * @return List<MessageVo>
     */
    private List<MessageVo> copyList(List<Message> messageList, boolean isConversation) {
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
    private MessageVo copy(Message message, boolean isConversation) {
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

    private List<SystemMessageVo> copySystemMessageList(List<Message> messages, boolean isDetail) {
        List<SystemMessageVo> systemMessageVoList = new ArrayList<>();
        for (Message message : messages) {
            systemMessageVoList.add(copySystemMessage(message, isDetail));
        }
        return systemMessageVoList;
    }

    private SystemMessageVo copySystemMessage(Message message, boolean isDetail) {
        SystemMessageVo systemMessageVo = new SystemMessageVo();
        BeanUtils.copyProperties(message, systemMessageVo);
        // 获得 content 存储的数据
        String content = HtmlUtils.htmlUnescape(message.getContent());
        Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
        // 消息发起实体类型、id、所属用户、发生所在帖子id
        systemMessageVo.setUser(userService.findUserById((Integer) data.get("userId")));
        systemMessageVo.setEntityType((Integer) data.get("entityType"));
        systemMessageVo.setEntityId((Integer) data.get("entityId"));
        Object postId = data.get("postId");
        if (postId != null) systemMessageVo.setPostId((Integer) postId);
        // 是否在通知详情列表下
        if (isDetail) {
            // 通知的作者（即系统）
            systemMessageVo.setFromUser(userService.findUserById(message.getFromId()));
        } else {
            // 该主题所有消息数量、未读消息数量
            systemMessageVo.setCount(messageMapper.selectNoticeCount(message.getToId(), message.getConversationId()));
            systemMessageVo.setUnreadCount(messageMapper.selectNoticeUnreadCount(message.getToId(), message.getConversationId()));
        }
        return systemMessageVo;
    }
}
