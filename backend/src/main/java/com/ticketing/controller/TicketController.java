package com.ticketing.controller;

import com.ticketing.dto.CommentDTO;
import com.ticketing.dto.TicketRequest;
import com.ticketing.dto.TicketResponse;
import com.ticketing.dto.AttachmentDTO;
import com.ticketing.model.Ticket;
import com.ticketing.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getUserTickets() {
        return ResponseEntity.ok(ticketService.getUserTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Ticket.Status status = Ticket.Status.valueOf(request.get("status"));
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(ticketService.assignTicket(id, request.get("assigneeId")));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(ticketService.addComment(id, request.get("content")));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<TicketResponse> rateTicket(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Integer rating = (Integer) request.get("rating");
        String feedback = (String) request.get("feedback");
        return ResponseEntity.ok(ticketService.rateTicket(id, rating, feedback));
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<AttachmentDTO> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(ticketService.uploadAttachment(id, file));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TicketResponse>> searchTickets(@RequestParam String keyword) {
        return ResponseEntity.ok(ticketService.searchTickets(keyword));
    }

    @GetMapping("/filter/status")
    public ResponseEntity<List<TicketResponse>> filterByStatus(@RequestParam Ticket.Status status) {
        return ResponseEntity.ok(ticketService.filterTicketsByStatus(status));
    }

    @GetMapping("/filter/priority")
    public ResponseEntity<List<TicketResponse>> filterByPriority(@RequestParam Ticket.Priority priority) {
        return ResponseEntity.ok(ticketService.filterTicketsByPriority(priority));
    }
}
