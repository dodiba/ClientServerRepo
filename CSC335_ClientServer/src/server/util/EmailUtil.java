package server.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Random;

public class EmailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "susitoto7@gmail.com";
    private static final String SMTP_PASSWORD = "totozinho";
    
    public static boolean sendPasswordRecoveryEmail(String recipientEmail, String newPassword) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Password Recovery");
            
            String content = "Your new password is: " + newPassword + "\n\n" +
                           "Please change your password after logging in.";
            
            message.setText(content);
            
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
} 
