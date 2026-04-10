package com.example.projectmedbill;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.util.Properties;
import java.io.File;

public class EmailSender {

    public static void sendEmailWithAttachment(String recipientEmail, String subject, String body, String filePath) {
        final String username = "incrediblesabari02@gmail.com"; // Replace with your email
        final String password = "bclyyvqdwuudpxsz"; // Replace with your app-specific password

        // Mail server configuration
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465"); // SSL port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create the email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);

            // Create multipart message for body and attachment
            Multipart multipart = new MimeMultipart();

            // Body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);

            // Attachment part
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    System.out.println("Attaching file: " + filePath);  // Debugging line
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(filePath);
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName("bill.pdf"); // Change if needed
                    attachmentPart.setHeader("Content-Type", "application/pdf");
                    multipart.addBodyPart(attachmentPart);
                } else {
                    System.out.println("File not found: " + filePath);  // Debugging line
                }
            }

            // Complete message
            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();  // Log the exception
            System.out.println("Error: " + e.getMessage());
        }
    }
}
