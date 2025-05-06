package com.platform.report.infrastructure.service;

import com.platform.report.domain.model.report.ReportContent;
import com.platform.report.infrastructure.exception.DistributionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

/**
 * 邮件分发服务
 * 用于通过邮件分发报表
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDistributionService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 发送报表邮件
     *
     * @param reportContent 报表内容
     * @param to 收件人列表
     * @param cc 抄送人列表
     * @param subject 邮件主题
     * @param body 邮件正文
     * @param properties 其他属性
     * @return 是否发送成功
     */
    public boolean sendReportEmail(ReportContent reportContent, List<String> to, List<String> cc,
                                  String subject, String body, Map<String, Object> properties) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to.toArray(new String[0]));
            
            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.toArray(new String[0]));
            }
            
            helper.setSubject(subject);
            helper.setText(body, true); // 设置为HTML格式
            
            // 添加附件
            ByteArrayResource resource = new ByteArrayResource(reportContent.getContent());
            helper.addAttachment(reportContent.getFileName(), resource, reportContent.getContentType());
            
            mailSender.send(message);
            log.info("Report email sent successfully to {}", String.join(", ", to));
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send report email", e);
            throw new DistributionException("Failed to send report email: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送通知邮件
     *
     * @param to 收件人列表
     * @param subject 邮件主题
     * @param body 邮件正文
     * @return 是否发送成功
     */
    public boolean sendNotificationEmail(List<String> to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, true); // 设置为HTML格式
            
            mailSender.send(message);
            log.info("Notification email sent successfully to {}", String.join(", ", to));
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send notification email", e);
            throw new DistributionException("Failed to send notification email: " + e.getMessage(), e);
        }
    }
}
