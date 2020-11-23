package com.sutpc.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;


@Component
public class MailService {

    /**
     * JavaMailSender是springboot在MailSenderPropertiesConfiguration中配置号的，
     * 在Mail自动配置类MailSenderAutoConfiguration中导入。
     */
    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String formEmail;

    /**
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Async
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(formEmail);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setCc(formEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }


    /**
     * 发送带附件的邮件
     *
     * @param from
     * @param to
     * @param subject
     * @param content
     * @param file
     */
    @Async
    public void sendAttachFileMail(String from, String to, String subject, String content, File file) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            //参数2表示构造一个multipart message类型的邮件，multipart message类新的的邮件包含多个正文、附件、内嵌资源
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            //添加附件
            helper.addAttachment(file.getName(), file);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
