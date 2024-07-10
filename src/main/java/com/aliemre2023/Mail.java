// mvn exec:java -Dexec.mainClass="com.aliemre2023.Mail"
// from root

package com.aliemre2023;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Mail {
    private String noreply_mail = "noreply@gmail.com";
    private String noreply_password = "****";
    private String noreply_appSpessificPassword = "****";

    Session newSession = null;
    MimeMessage mimeMessage = null;

    public void run(String[] mails, String subject, String body) throws Exception {
        Mail mail = new Mail();
        mail.setupServerProperties();
        mail.draftEmail(mails, subject, body);
        mail.sendEmail();
    }

    public void setupServerProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        newSession = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // your email and application spessific password from gmail website
                return new PasswordAuthentication(noreply_mail, noreply_appSpessificPassword);
            }
        });
    }

    public MimeMessage draftEmail(String[] mails, String subject, String body) throws Exception {
        String[] emailReceipents = mails;
        String emailSubject = subject;
        String emailBody = body;
        mimeMessage = new MimeMessage(newSession);

        for(int i = 0; i < emailReceipents.length; i++){
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceipents[i]));
        }
        mimeMessage.setSubject(emailSubject);

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(emailBody, "html/text");
        MimeMultipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(bodyPart);
        mimeMessage.setContent(multiPart);
        return mimeMessage;
    }

    public void sendEmail() throws Exception {
        String fromUser = noreply_mail;
        String fromUserPassword = noreply_password; 
        String emailHost = "smtp.gmail.com";
        Transport transport = newSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserPassword);
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();

        // System.out.println("Email successfully sent.");
    }

    public String getNoreplyMail() {
        return noreply_mail;
    }

    public String getNoreplyPassword() {
        return noreply_password;
    }
}
