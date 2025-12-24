package com.rehome.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.rehome.main.dto.request.ChangeCaseStatusRequest;
import com.rehome.main.dto.request.CustomerServiceFormRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendContactConfirmMail(CustomerServiceFormRequest form, String questionTypeName) {

        // 建立支援 HTML 的信件
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // 第二個參數 true 代表這是一封(包含 HTML) 的信件
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("2025rehome@gmail.com");
            helper.setTo(form.getCmail());
            helper.setSubject("ReHome 客服中心 - 我們已收到您的問題");

            String headerStyle = "style='background-color: #D5C8B8; font-weight: bolder; color: #504033;  white-space: nowrap; width: 120px;'";

            // 撰寫 HTML 內容
            String htmlContent = String.format(
                    "<html>"
                    + "<body>"
                    + "<h3>親愛的 %s 您好：</h3>"
                    + "<h4>我們已收到您的聯絡表單，客服人員將於 1-3 個工作天內回覆您。</h4>"
                    + "<br>"
                    + "<table border='1' style='border-collapse: collapse; width: 100%%; max-width: 600px; border: 2px solid #756B61; font-size: 16px; background-color: #E9E2D8; text-align:center;'>"
                    + "<tr style='background-color: #504033; color: #fffbf7;'>"
                    + "<td colspan='2'>您所填寫的表單內容</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td " + headerStyle + ">問題種類</td>"
                    + "<td>%s</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td " + headerStyle + ">問題標題</td>"
                    + "<td>%s</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td " + headerStyle + ">問題敘述</td>"
                    + "<td style='white-space: pre-wrap;word-break: break-all;'>%s</td>"
                    + "</tr>"
                    + "</table>"
                    + "<br><br>"
                    + "<h4>ReHome 團隊 敬上</h4>"
                    + "</body>"
                    + "</html>",
                    form.getCname(), // 對應第一個 %s
                    questionTypeName, // 對應表格內的種類
                    form.getQuestionTitle(),// 對應表格內的標題
                    form.getQuestionInfo() // 對應表格內的敘述
            );

            // 第二個參數 true 表示內容是 HTML
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
            // 可以在這裡處理寄信失敗的邏輯
        }
    }
    
    @Async
    public void sendReplyMail(String cname, String cmail, String qtitle, String reply) {
        // 建立支援 HTML 的信件
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // 第二個參數 true 代表這是一封(包含 HTML) 的信件
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("2025rehome@gmail.com");
            helper.setTo(cmail);
            helper.setSubject("ReHome 客服中心回覆 - 關於您的[" + qtitle + "]問題");

            String headerStyle = "style='background-color: #D5C8B8; font-weight: bolder; color: #504033;  white-space: nowrap; width: 120px;'";

            // 撰寫 HTML 內容
            String htmlContent = String.format(
                    "<html>"
                    + "<body>"
                    + "<h3>親愛的 %s 您好：</h3>"
                    + "<h4>以下為您先前問題的回覆</h4>"
                    + "<br>"
                    + "<table border='1' style='border-collapse: collapse; width: 100%%; max-width: 600px; border: 2px solid #756B61; font-size: 16px; background-color: #E9E2D8; text-align:center;'>"
                    + "<tr style='background-color: #504033; color: #fffbf7;'>"
                    + "<td colspan='2'>客服回覆內容</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td " + headerStyle + ">您的問題標題</td>"
                    + "<td>%s</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td " + headerStyle + ">客服回覆</td>"
                    + "<td>%s</td>"
                    + "</tr>"
                    + "</table>"
                    + "<br>"
                    + "<h4>ReHome 團隊 敬上</h4>"
                    + "</body>"
                    + "</html>",
                    cname, // 對應第一個 %s
                    qtitle, // 對應表格內的種類
                    reply // 對應表格內的敘述
            );

            // 第二個參數 true 表示內容是 HTML
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
            //寄信失敗的邏輯

        }
    }

    @Async
    public void sendReviewResult(String cEmail, String userName, String caseNumber, String caseType, ChangeCaseStatusRequest reviewResult) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("2025rehome@gmail.com"); // 寄件人
        message.setTo(cEmail); // 收件人 (從表單來的)
        if (reviewResult.getStatusId() == 2) {
            message.setSubject("ReHome - 您的案件編號:" + caseNumber + " - 審核成功");
            message.setText("親愛的 " + userName + " 您好：\n\n"

                    + "您先前所送出待審核的["+ caseType +"]案件\n"
                    + "案件編號:" + caseNumber + "\n"
                    + "審核結果:成功\n"
                    + "您可以登入網站查看您的案件\n\n"
                    + "ReHome 團隊 敬上");

        } else if (reviewResult.getStatusId() == 3) {
            message.setSubject("ReHome - 您的案件編號:" + caseNumber + " - 審核失敗");
            message.setText("親愛的 " + userName + " 您好：\n\n"

                    + "您先前所送出待審核的["+ caseType +"]案件\n"
                    + "案件編號:" + caseNumber + "\n"
                    + "審核結果:失敗\n"
                    + "失敗原因:" + reviewResult.getRejectReason() + "\n"
                    + "您可以登入網站查看您的案件或是聯絡客服\n\n"
                    + "ReHome 團隊 敬上");

        }

        mailSender.send(message);
    }
}
