package com.ticketing.dto;

import com.ticketing.model.Ticket;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketResponse {
    private Long id;
    private String subject;
    private String description;
    private Ticket.Priority priority;
    private Ticket.Status status;
    private UserDTO creator;
    private UserDTO assignee;
    private List<CommentDTO> comments;
    private List<AttachmentDTO> attachments;
    private Integer rating;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
}
