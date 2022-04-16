package me.xnmk.community;

import me.xnmk.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 16:22
 * @Description: 邮箱功能测试
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 发送文本邮件
     */
    @Test
    public void testSendTextMail() {
        mailClient.sendMail("xnmk_zhan@163.com", "TEST", "Welcome.");
    }

    /**
     * 发送html邮件
     */
    @Test
    public void testSendHTMLMail() {
        Context context = new Context();
        context.setVariable("username", "xnmk");

        String content = templateEngine.process("/mail/demo", context);
        // System.out.println(content);

        mailClient.sendMail("xnmk_zhan@163.com", "HTML", content);
    }
}
