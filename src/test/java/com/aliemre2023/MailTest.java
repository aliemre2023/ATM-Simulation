package com.aliemre2023;

import javax.mail.*;
import javax.mail.internet.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class MailTest {

    private Mail mail;
    private Session mockSession;
    private Transport mockTransport;

    @Before
    public void setUp() {
        mail = new Mail();
        mail.setupServerProperties();
        mockSession = mock(Session.class);
        mockTransport = mock(Transport.class);
        mail.newSession = mockSession;
    }

    @After
    public void tearDown() {
        mail = null;
    }

    @Test
    public void testDraftEmail() throws Exception {
        // Arrange
        String[] recipients = {"recipient1@gmail.com", "recipient2@example.com"};
        String subject = "Subject";
        String body = "<html><body><h1>Hello World!</h1></body></html>";

        // Act
        MimeMessage mimeMessage = mail.draftEmail(recipients, subject, body);

        // Assert
        assertEquals("Subject has a problem", subject, mimeMessage.getSubject());
        // extract the content from MimeMultipart
        MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
        BodyPart bodyPart = mimeMultipart.getBodyPart(0);
        assertEquals("Body has a problem", body, bodyPart.getContent().toString());
        assertEquals("Recipients have some problem", 2, mimeMessage.getAllRecipients().length);
    }

    @Test
    public void testSendEmail() throws Exception {
        // Arrange
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        Transport mockTransport = mock(Transport.class);
        when(mockMimeMessage.getAllRecipients()).thenReturn(new Address[] {
            new InternetAddress("recipient1@example.com")
        });

        doNothing().when(mockTransport).sendMessage(any(MimeMessage.class), any(Address[].class));
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);

        // Act
        mail.sendEmail();

        // Assert
        verify(mockTransport, times(1)).connect("smtp.gmail.com", mail.getNoreplyMail(), mail.getNoreplyPassword());
        verify(mockTransport, times(1)).sendMessage(any(MimeMessage.class), any(Address[].class));
        verify(mockTransport, times(1)).close();
    }
}
