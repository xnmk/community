package me.xnmk.community.service;

import me.xnmk.community.entity.Message;
import me.xnmk.community.vo.MessageVo;
import me.xnmk.community.vo.SystemMessageVo;
import me.xnmk.community.vo.param.PageParams;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-26 12:35
 * @Description: MessageService
 */
public interface MessageService {

    /**
     * 查询当前用户的会话列表，针对每个会话只返回最新的一条私信
     *
     * @param userId     用户id
     * @param pageParams 分页参数
     * @return List<MessageVo>
     */
    List<MessageVo> findConversations(int userId, PageParams pageParams);

    /**
     * 查询当前用户的会话数量
     *
     * @param userId 用户id
     * @return int
     */
    int findConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     *
     * @param conversationId 会话id
     * @param pageParams     分页参数
     * @return List<MessageVo>
     */
    List<MessageVo> findLetters(String conversationId, PageParams pageParams);

    /**
     * 查询某个会话包含的私信数量
     *
     * @param conversationId 会话id
     * @return int
     */
    int findLetterCount(String conversationId);

    /**
     * 查询未读私信数量
     *
     * @param userId         用户id
     * @param conversationId 会话id(可携带)
     * @return int
     */
    int findLetterUnreadCount(int userId, String conversationId);

    /**
     * 发送私信
     *
     * @param message 私信实体
     * @return int
     */
    int addMessage(Message message);

    /**
     * 私信已读
     *
     * @param ids 未读私信列表
     * @return it
     */
    int readMessage(List<Integer> ids);

    /**
     * 根据消息id删除消息
     *
     * @param MessageId 消息id
     * @return int
     */
    int deleteMessage(int MessageId);

    /**
     * 查询某个主题下的最新系统消息
     *
     * @param userId 用户id
     * @param topic  主题
     * @return me.xnmk.community.entity.Message
     */
    SystemMessageVo findLatestNotice(int userId, String topic);

    int findNoticeCount(int userId, String topic);

    /**
     * 返回未读的系统消息数量（可设置主题）
     *
     * @param userId 用户id
     * @param topic  主题
     * @return int
     */
    int findNoticeUnreadCount(int userId, String topic);

    /**
     * 返回系统消息详情列表
     *
     * @param userId 用户id
     * @param topic 主题
     * @param pageParams 分页参数
     * @return List<SystemMessageVo>
     */
    List<SystemMessageVo> findNotices(int userId, String topic, PageParams pageParams);
}
