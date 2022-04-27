package me.xnmk.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.xnmk.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-25 21:14
 * @Description: MessageMapper
 */
@Mapper
@Component
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询当前用户的会话列表，针对每个会话只返回最新的一条私信
     *
     * @param userId 用户id
     * @param offset 起始
     * @param limit  每页个数
     * @return List<Message>
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     *
     * @param userId 用户id
     * @return int
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话包含的私信数量
     *
     * @param conversationId 会话id
     * @return int
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信数量
     *
     * @param userId         用户id
     * @param conversationId 会话id(可携带)
     * @return int
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    /**
     * 更新消息状态
     *
     * @param ids    消息列表
     * @param status 更新的消息状态
     * @return int
     */
    int updateStatus(List<Integer> ids, int status);
}
