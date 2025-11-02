package com.ticketing.service;

import com.ticketing.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTicketCreatedEmail(Ticket ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(ticket.getCreator().getEmail());
            message.setSubject("Ticket #" + ticket.getId() + " Created");
            message.setText("Your ticket has been created successfully.\n\n" +
                    "Subject: " + ticket.getSubject() + "\n" +
                    "Priority: " + ticket.getPriority() + "\n" +
                    "Status: " + ticket.getStatus());
            mailSender.send(message);
        } catch (Exception e) {
        }
    }

    public void sendTicketAssignedEmail(Ticket ticket) {
        try {
            if (ticket.getAssignee() != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(ticket.getAssignee().getEmail());
                message.setSubject("Ticket #" + ticket.getId() + " Assigned to You");
                message.setText("A ticket has been assigned to you.\n\n" +
                        "Subject: " + ticket.getSubject() + "\n" +
                        "Priority: " + ticket.getPriority() + "\n" +
                        "Status: " + ticket.getStatus());
                mailSender.send(message);
            }
        } catch (Exception e) {
        }
    }

    public void sendTicketStatusChangedEmail(Ticket ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(ticket.getCreator().getEmail());
            message.setSubject("Ticket #" + ticket.getId() + " Status Updated");
            message.setText("Your ticket status has been updated.\n\n" +
                    "Subject: " + ticket.getSubject() + "\n" +
                    "New Status: " + ticket.getStatus());
            mailSender.send(message);
        } catch (Exception e) {
        }
    }
}
