package com.aliemre2023;

import javax.mail.*;
import javax.mail.internet.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.powermock.api.mockito.PowerMockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Mail.class, Transport.class })
public class MailTest {

    private Mail mail;
    private Session mockSession;
    private Transport mockTransport;

    @Before
    public void setUp() throws Exception {
        // Create a spy for the Mail class
        mail = spy(new Mail());
        
        // Mock the setupServerProperties method
        doNothing().when(mail, "setupServerProperties");
        
        // Mock the Session and Transport objects
        mockSession = mock(Session.class);
        mockTransport = mock(Transport.class);
        
        // Mock the createNewSession method to return the mocked session
        when(mail, "createNewSession").thenReturn(mockSession);

        // Set the newSession field in the Mail object to the mocked session
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
        MimeMessage mimeMessage = Whitebox.invokeMethod(mail, "draftEmail", recipients, subject, body);

        // Assert
        assertEquals("Subject has a problem", subject, mimeMessage.getSubject());

        // Extract the content from MimeMultipart
        MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
        BodyPart bodyPart = mimeMultipart.getBodyPart(0);
        assertEquals("Body has a problem", body, bodyPart.getContent().toString());
        assertEquals("Recipients have some problem", 2, mimeMessage.getAllRecipients().length);
    }

    @Test
    public void testSendEmail() throws Exception {
        // Arrange
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        
        // Mock the recipients of the MimeMessage
        when(mockMimeMessage.getAllRecipients()).thenReturn(new Address[] {
            new InternetAddress("recipient1@example.com")
        });

        // Mock the sendMessage method of the Transport object
        doNothing().when(mockTransport).sendMessage(any(MimeMessage.class), any(Address[].class));
        
        // Mock the getTransport method of the Session object
        when(mockSession.getTransport("smtp")).thenReturn(mockTransport);

        // Act
        doNothing().when(mail, "sendMail");

        // Assert
        verify(mockTransport, times(1)).connect("smtp.gmail.com", mail.getNoreplyMail(), mail.getNoreplyPassword());
        verify(mockTransport, times(1)).sendMessage(any(MimeMessage.class), any(Address[].class));
        verify(mockTransport, times(1)).close();
    }
}
