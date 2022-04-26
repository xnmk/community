package me.xnmk.community;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.MessageMapper;
import me.xnmk.community.entity.Message;
import org.aspectj.weaver.ast.And;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-25 22:50
 * @Description: Message接口测试
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageTests {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectConversations(){
        int userId = 111;
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
    }

    @Test
    public void testSelectConversationCount(){
        int userId = 111;
        int i = messageMapper.selectConversationCount(userId);
        System.out.println(i);
    }

    @Test
    public void testSelectLetters(){
        String conversationId = "111_112";
        Page<Message> page = new Page<>(1, 10);
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Message::getStatus, 2);
        queryWrapper.ne(Message::getFromId, 1);
        queryWrapper.eq(Message::getConversationId, conversationId);
        queryWrapper.orderByDesc(Message::getId);
        messageMapper.selectPage(page, queryWrapper);
        List<Message> records = page.getRecords();
        for (Message record : records) {
            System.out.println(record);
        }
    }

    @Test
    public void testSelectLetterCount(){
        String conversationId = "111_112";
        int count = messageMapper.selectLetterCount(conversationId);
        System.out.println(count);
    }

    @Test
    public void testSelectLetterUnreadCount(){
        int userId = 131;
        String conversationId = "111_131";
        int count = messageMapper.selectLetterUnreadCount(userId, conversationId);
        System.out.println(count);
    }
}
