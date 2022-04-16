package me.xnmk.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 16:10
 * @Description: 邮箱客户端
 */
@Component
public class MailClient {

    public static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     * @param to 目标邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // MimeMessageHelper 用于帮助构建 MimeMessage
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            // 参数true表示支持html文本
            messageHelper.setText(content, true);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮箱失败:" + e.getMessage());
        }
    }
}
