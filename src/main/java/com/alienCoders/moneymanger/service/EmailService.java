package com.alienCoders.moneymanger.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    final private JavaMailSender mailSender;

    @Value("${brevo.sender.email}")
    private String fromEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Value("${brevo.api.key}")
    private String brevoApiKey;


    private final ObjectMapper objectMapper=new ObjectMapper();

    public void sendEmail(String to, String subject, String body) {
    try(CloseableHttpClient client= HttpClients.createDefault()) {
        HttpPost post=new HttpPost("https://api.brevo.com/v3/smtp/email");
        post.setHeader("accept","application/json");
        post.setHeader("content-type","application/json");
        post.setHeader("api-key",brevoApiKey);

        Map<String,Object> email=new HashMap<>();
        Map<String,String> sender=new HashMap<>();
        List<Map<String, String>> toList = List.of(Map.of("email", to));
        sender.put("name",senderName);
        sender.put("email",fromEmail);
        email.put("sender",sender);
        email.put("to",toList);
        email.put("subject",subject);
        email.put("htmlContent",body);
        String json=objectMapper.writeValueAsString(email);
        post.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

        client.execute(post,classicHttpResponse -> {
            int status=classicHttpResponse.getCode();
            if(status>=200 && status<=300){
                System.out.println("Email sent successfully to "+to);
            }
            else {
                throw new RuntimeException("Failed to send email: HTTP "+status);
            }
            return null;
        });
    }catch (Exception e){
        throw new RuntimeException(e.getMessage());
    }
    }

    public void sendEmailWithAttachment(
            String to,
            String subject,
            String htmlBody,
            byte[] attachmentBytes,
            String fileName
    ){
        try(CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost post=new HttpPost("https://api.brevo.com/v3/smtp/email");
            post.setHeader("accept","application/json");
            post.setHeader("content-type","application/json");
            post.setHeader("api-key",brevoApiKey);

            String base64Attachment= Base64.encodeBase64String(attachmentBytes);
            Map<String,Object> emailData=new HashMap<>();

            Map<String,String> sender=Map.of("name",senderName,"email",fromEmail);

            List<Map<String,String>> toList=List.of(Map.of("email",to));

            Map<String,Object> attachement=new HashMap<>();
            attachement.put("name",fileName);
            attachement.put("content",base64Attachment);
            emailData.put("sender",sender);
            emailData.put("to",toList);
            emailData.put("subject",subject);
            emailData.put("htmlContent",htmlBody);
            emailData.put("attachment",List.of(attachement));

            String json=objectMapper.writeValueAsString(emailData);
            post.setEntity(new StringEntity(json,StandardCharsets.UTF_8));

            client.execute(post,httpResponse->{
                int status=httpResponse.getCode();
                if(status>=200 && status<300){
                    System.out.println("Email sent with attachment: "+to);
                }
                else {
                    throw new RuntimeException("Failed to send email: Http "+status);
                }
                return null;
            });

        }
        catch (Exception e){
            throw new RuntimeException("Email sending failed: "+e.getMessage());
        }
    }


}
