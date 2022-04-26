package me.xnmk.community.vo;


import me.xnmk.community.entity.User;

import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-04-26 12:40
 * @Description: MessageVo（包括会话、私信）
 */
public class MessageVo {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private Date createTime;
    // 会话内消息数量
    private int letterCount;
    // 会话内未读消息数量
    private int unreadCount;
    // 会话目标用户
    private User targetUser;
    // 私信内fromUser
    private User fromUser;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getLetterCount() {
        return letterCount;
    }

    public void setLetterCount(int letterCount) {
        this.letterCount = letterCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
}
