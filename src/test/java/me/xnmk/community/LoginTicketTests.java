package me.xnmk.community;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import me.xnmk.community.dao.LoginTicketMapper;
import me.xnmk.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-04-17 15:12
 * @Description: LoginTicketsMapper 测试
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketTests {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 插入登录凭证信息
     */
    @Test
    public void testInsertLoginTickets() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insert(loginTicket);
    }

    /**
     * 查询并修改登录凭证状态
     */
    @Test
    public void testSelectLoginTicket() {
        LambdaQueryWrapper<LoginTicket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginTicket::getTicket, "abc");
        LoginTicket loginTicket = loginTicketMapper.selectOne(queryWrapper);
        System.out.println(loginTicket);

        LambdaUpdateWrapper<LoginTicket> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LoginTicket::getTicket, "abc");
        loginTicket.setStatus(1);
        loginTicketMapper.update(loginTicket, updateWrapper);
    }
}
