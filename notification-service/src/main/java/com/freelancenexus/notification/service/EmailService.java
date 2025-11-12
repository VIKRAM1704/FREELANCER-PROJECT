package com.freelancenexus.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("Suchitranv5 <suchitranv5@gmail.com>");
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email successfully sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    public void sendProjectCreatedEmail(String clientEmail, String projectTitle) {
        String subject = "Your Project '" + projectTitle + "' Has Been Posted Successfully!";
        String body = "Dear Client,\n\n" +
                "Your project '" + projectTitle + "' has been successfully posted on Freelance Nexus!\n" +
                "Freelancers can now view and submit proposals for your project.\n\n" +
                "What's Next?\n" +
                "- Review proposals as they come in\n" +
                "- Compare freelancer profiles and ratings\n" +
                "- Select the best candidate for your project\n\n" +
                "Visit your dashboard to manage your project.\n\n" +
                "Best regards,\nFreelance Nexus Team\nSent at: " + LocalDateTime.now().format(formatter);
        sendEmail(clientEmail, subject, body);
    }

    @Async
    public void sendProposalReceivedEmail(String clientEmail, String freelancerName, String projectTitle) {
        String subject = "New Proposal Received for '" + projectTitle + "'";
        String body = "Dear Client,\n\n" +
                "Great news! " + freelancerName + " has submitted a proposal for your project '" + projectTitle + "'.\n\n" +
                "Proposal Details:\n" +
                "- Freelancer: " + freelancerName + "\n" +
                "- Project: " + projectTitle + "\n\n" +
                "Action Required:\n" +
                "Review the proposal and freelancer profile to make an informed decision.\n\n" +
                "View Proposal: [Dashboard Link]\n\n" +
                "Best regards,\nFreelance Nexus Team\nSent at: " + LocalDateTime.now().format(formatter);
        sendEmail(clientEmail, subject, body);
    }

    @Async
    public void sendProposalAcceptedEmail(String freelancerEmail, String projectTitle) {
        String subject = "Congratulations! Your Proposal for '" + projectTitle + "' Has Been Accepted";
        String body = "Dear Freelancer,\n\n" +
                "Congratulations! Your proposal has been accepted for the project '" + projectTitle + "'.\n\n" +
                "Next Steps:\n" +
                "1. Review the project requirements carefully\n" +
                "2. Communicate with the client to clarify any details\n" +
                "3. Start working on the project\n" +
                "4. Submit deliverables as per the agreed timeline\n\n" +
                "Project Details: [View in Dashboard]\n\n" +
                "Remember to maintain professional communication and deliver quality work.\n\n" +
                "Good luck with your project!\n\n" +
                "Best regards,\nFreelance Nexus Team\nSent at: " + LocalDateTime.now().format(formatter);
        sendEmail(freelancerEmail, subject, body);
    }

    @Async
    public void sendPaymentCompletedEmail(String userEmail, Double amount, String transactionId, String currency) {
        String subject = "Payment Confirmation - Transaction #" + transactionId;
        String body = "Dear User,\n\n" +
                "Your payment has been processed successfully!\n\n" +
                "Transaction Details:\n" +
                "- Amount: " + amount + " " + currency + "\n" +
                "- Transaction ID: " + transactionId + "\n" +
                "- Date: " + LocalDateTime.now().format(formatter) + "\n" +
                "- Status: COMPLETED\n\n" +
                "This payment has been credited to the recipient's account.\n\n" +
                "View Transaction History: [Dashboard Link]\n\n" +
                "Thank you for using Freelance Nexus!\n\n" +
                "Best regards,\nFreelance Nexus Team\nSent at: " + LocalDateTime.now().format(formatter);
        sendEmail(userEmail, subject, body);
    }

    @Async
    public void sendPaymentReceivedEmail(String receiverEmail, Double amount, String transactionId, String projectTitle, String currency) {
        String subject = "Payment Received - " + amount + " " + currency;
        String body = "Dear Freelancer,\n\n" +
                "Great news! You've received a payment for your work.\n\n" +
                "Payment Details:\n" +
                "- Amount: " + amount + " " + currency + "\n" +
                "- Project: " + projectTitle + "\n" +
                "- Transaction ID: " + transactionId + "\n" +
                "- Date: " + LocalDateTime.now().format(formatter) + "\n\n" +
                "The funds are now available in your account.\n\n" +
                "View Balance: [Dashboard Link]\n\n" +
                "Thank you for your excellent work!\n\n" +
                "Best regards,\nFreelance Nexus Team\nSent at: " + LocalDateTime.now().format(formatter);
        sendEmail(receiverEmail, subject, body);
    }

    @Async
    public void sendGenericEmail(String to, String subject, String bodyContent) {
        String body = bodyContent + "\n\nSent at: " + LocalDateTime.now().format(formatter);
        sendEmail(to, subject, body);
    }
}
