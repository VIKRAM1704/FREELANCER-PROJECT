package com.freelancenexus.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Async
    public void sendProjectCreatedEmail(String clientEmail, String projectTitle) {
        log.info("=".repeat(80));
        log.info("📧 EMAIL SIMULATION - PROJECT CREATED");
        log.info("=".repeat(80));
        log.info("To: {}", clientEmail);
        log.info("Subject: Your Project '{}' Has Been Posted Successfully!", projectTitle);
        log.info("Body:");
        log.info("Dear Client,");
        log.info("");
        log.info("Your project '{}' has been successfully posted on Freelance Nexus!", projectTitle);
        log.info("Freelancers can now view and submit proposals for your project.");
        log.info("");
        log.info("What's Next?");
        log.info("- Review proposals as they come in");
        log.info("- Compare freelancer profiles and ratings");
        log.info("- Select the best candidate for your project");
        log.info("");
        log.info("Visit your dashboard to manage your project.");
        log.info("");
        log.info("Best regards,");
        log.info("Freelance Nexus Team");
        log.info("Sent at: {}", LocalDateTime.now().format(formatter));
        log.info("=".repeat(80));
    }

    @Async
    public void sendProposalReceivedEmail(String clientEmail, String freelancerName, String projectTitle) {
        log.info("=".repeat(80));
        log.info("📧 EMAIL SIMULATION - NEW PROPOSAL RECEIVED");
        log.info("=".repeat(80));
        log.info("To: {}", clientEmail);
        log.info("Subject: New Proposal Received for '{}'", projectTitle);
        log.info("Body:");
        log.info("Dear Client,");
        log.info("");
        log.info("Great news! {} has submitted a proposal for your project '{}'.", 
                freelancerName, projectTitle);
        log.info("");
        log.info("Proposal Details:");
        log.info("- Freelancer: {}", freelancerName);
        log.info("- Project: {}", projectTitle);
        log.info("");
        log.info("Action Required:");
        log.info("Review the proposal and freelancer profile to make an informed decision.");
        log.info("");
        log.info("View Proposal: [Dashboard Link]");
        log.info("");
        log.info("Best regards,");
        log.info("Freelance Nexus Team");
        log.info("Sent at: {}", LocalDateTime.now().format(formatter));
        log.info("=".repeat(80));
    }

    @Async
    public void sendProposalAcceptedEmail(String freelancerEmail, String projectTitle) {
        log.info("=".repeat(80));
        log.info("📧 EMAIL SIMULATION - PROPOSAL ACCEPTED / PROJECT ASSIGNED");
        log.info("=".repeat(80));
        log.info("To: {}", freelancerEmail);
        log.info("Subject: Congratulations! Your Proposal for '{}' Has Been Accepted", projectTitle);
        log.info("Body:");
        log.info("Dear Freelancer,");
        log.info("");
        log.info("Congratulations! Your proposal has been accepted for the project '{}'.", projectTitle);
        log.info("");
        log.info("Next Steps:");
        log.info("1. Review the project requirements carefully");
        log.info("2. Communicate with the client to clarify any details");
        log.info("3. Start working on the project");
        log.info("4. Submit deliverables as per the agreed timeline");
        log.info("");
        log.info("Project Details: [View in Dashboard]");
        log.info("");
        log.info("Remember to maintain professional communication and deliver quality work.");
        log.info("");
        log.info("Good luck with your project!");
        log.info("");
        log.info("Best regards,");
        log.info("Freelance Nexus Team");
        log.info("Sent at: {}", LocalDateTime.now().format(formatter));
        log.info("=".repeat(80));
    }

    @Async
    public void sendPaymentCompletedEmail(String userEmail, Double amount, String transactionId, String currency) {
        log.info("=".repeat(80));
        log.info("📧 EMAIL SIMULATION - PAYMENT COMPLETED");
        log.info("=".repeat(80));
        log.info("To: {}", userEmail);
        log.info("Subject: Payment Confirmation - Transaction #{}", transactionId);
        log.info("Body:");
        log.info("Dear User,");
        log.info("");
        log.info("Your payment has been processed successfully!");
        log.info("");
        log.info("Transaction Details:");
        log.info("- Amount: {} {}", amount, currency);
        log.info("- Transaction ID: {}", transactionId);
        log.info("- Date: {}", LocalDateTime.now().format(formatter));
        log.info("- Status: COMPLETED");
        log.info("");
        log.info("This payment has been credited to the recipient's account.");
        log.info("");
        log.info("View Transaction History: [Dashboard Link]");
        log.info("");
        log.info("If you have any questions, please contact our support team.");
        log.info("");
        log.info("Thank you for using Freelance Nexus!");
        log.info("");
        log.info("Best regards,");
        log.info("Freelance Nexus Team");
        log.info("Sent at: {}", LocalDateTime.now().format(formatter));
        log.info("=".repeat(80));
    }

    @Async
    public void sendGenericEmail(String to, String subject, String body) {
        log.info("=".repeat(80));
        log.info("📧 EMAIL SIMULATION - GENERIC");
        log.info("=".repeat(80));
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Body:");
        log.info("{}", body);
        log.info("Sent at: {}", LocalDateTime.now().format(formatter));
        log.info("=".repeat(80));
    }

    public void sendPaymentReceivedEmail(String receiverEmail, Double amount, String transactionId, String projectTitle, String currency) {
        log.info("=".repeat(80));
        log.info("📧 EMAIL SIMULATION - PAYMENT RECEIVED");
        log.info("=".repeat(80));
        log.info("To: {}", receiverEmail);
        log.info("Subject: Payment Received - {} {}", amount, currency);
        log.info("Body:");
        log.info("Dear Freelancer,");
        log.info("");
        log.info("Great news! You've received a payment for your work.");
        log.info("");
        log.info("Payment Details:");
        log.info("- Amount: {} {}", amount, currency);
        log.info("- Project: {}", projectTitle);
        log.info("- Transaction ID: {}", transactionId);
        log.info("- Date: {}", LocalDateTime.now().format(formatter));
        log.info("");
        log.info("The funds are now available in your account.");
        log.info("");
        log.info("View Balance: [Dashboard Link]");
        log.info("");
        log.info("Thank you for your excellent work!");
        log.info("");
        log.info("Best regards,");
        log.info("Freelance Nexus Team");
        log.info("Sent at: {}", LocalDateTime.now().format(formatter));
        log.info("=".repeat(80));
    }
}