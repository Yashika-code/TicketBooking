package com.ticketing.service;

import com.ticketing.dto.*;
import com.ticketing.model.Comment;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.model.Attachment;
import com.ticketing.repository.CommentRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import com.ticketing.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private final String UPLOAD_DIR = "uploads";

    public TicketResponse createTicket(TicketRequest request) {
        User currentUser = userService.getCurrentUser();

        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setCreator(currentUser);

        Ticket savedTicket = ticketRepository.save(ticket);
        
        emailService.sendTicketCreatedEmail(savedTicket);

        return convertToResponse(savedTicket);
    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getUserTickets() {
        User currentUser = userService.getCurrentUser();
        return ticketRepository.findByCreator(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = userService.getCurrentUser();
        if (!hasAccessToTicket(ticket, currentUser)) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponse(ticket);
    }

    public TicketResponse updateTicketStatus(Long id, Ticket.Status status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = userService.getCurrentUser();
        if (!hasAccessToTicket(ticket, currentUser)) {
            throw new RuntimeException("Access denied");
        }

        ticket.setStatus(status);
        if (status == Ticket.Status.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (status == Ticket.Status.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        
        emailService.sendTicketStatusChangedEmail(updatedTicket);

        return convertToResponse(updatedTicket);
    }

    public TicketResponse assignTicket(Long ticketId, Long assigneeId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != User.Role.ADMIN && currentUser.getRole() != User.Role.SUPPORT_AGENT) {
            throw new RuntimeException("Access denied");
        }

        ticket.setAssignee(assignee);
        if (ticket.getStatus() == Ticket.Status.OPEN) {
            ticket.setStatus(Ticket.Status.IN_PROGRESS);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        
        emailService.sendTicketAssignedEmail(updatedTicket);

        return convertToResponse(updatedTicket);
    }

    public CommentDTO addComment(Long ticketId, String content) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = userService.getCurrentUser();
        if (!hasAccessToTicket(ticket, currentUser)) {
            throw new RuntimeException("Access denied");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTicket(ticket);
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);

        return convertCommentToDTO(savedComment);
    }

    public TicketResponse rateTicket(Long ticketId, Integer rating, String feedback) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = userService.getCurrentUser();
        if (!ticket.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only ticket creator can rate");
        }

        if (ticket.getStatus() != Ticket.Status.RESOLVED && ticket.getStatus() != Ticket.Status.CLOSED) {
            throw new RuntimeException("Can only rate resolved or closed tickets");
        }

        ticket.setRating(rating);
        ticket.setFeedback(feedback);

        Ticket updatedTicket = ticketRepository.save(ticket);
        return convertToResponse(updatedTicket);
    }

    public AttachmentDTO uploadAttachment(Long ticketId, MultipartFile file) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User currentUser = userService.getCurrentUser();
        if (!hasAccessToTicket(ticket, currentUser)) {
            throw new RuntimeException("Access denied");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(filePath.toString());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setTicket(ticket);
        attachment.setUser(currentUser);

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return convertAttachmentToDTO(savedAttachment);
    }

    public List<TicketResponse> searchTickets(String keyword) {
        return ticketRepository.searchByKeyword(keyword).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> filterTicketsByStatus(Ticket.Status status) {
        return ticketRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> filterTicketsByPriority(Ticket.Priority priority) {
        return ticketRepository.findByPriority(priority).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private boolean hasAccessToTicket(Ticket ticket, User user) {
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }
        if (user.getRole() == User.Role.SUPPORT_AGENT) {
            return true;
        }
        return ticket.getCreator().getId().equals(user.getId());
    }

    private TicketResponse convertToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setSubject(ticket.getSubject());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());
        response.setCreator(userService.convertToDTO(ticket.getCreator()));
        response.setAssignee(ticket.getAssignee() != null ? userService.convertToDTO(ticket.getAssignee()) : null);
        response.setComments(ticket.getComments().stream()
                .map(this::convertCommentToDTO)
                .collect(Collectors.toList()));
        response.setAttachments(ticket.getAttachments().stream()
                .map(this::convertAttachmentToDTO)
                .collect(Collectors.toList()));
        response.setRating(ticket.getRating());
        response.setFeedback(ticket.getFeedback());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setResolvedAt(ticket.getResolvedAt());
        response.setClosedAt(ticket.getClosedAt());
        return response;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUser(userService.convertToDTO(comment.getUser()));
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    private AttachmentDTO convertAttachmentToDTO(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setFileType(attachment.getFileType());
        dto.setFileSize(attachment.getFileSize());
        dto.setUser(userService.convertToDTO(attachment.getUser()));
        dto.setUploadedAt(attachment.getUploadedAt());
        return dto;
    }
}
