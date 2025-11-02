package com.ticketing.controller;

import com.ticketing.dto.TicketResponse;
import com.ticketing.dto.UserDTO;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.service.TicketService;
import com.ticketing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<TicketResponse> forceUpdateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Ticket.Status status = Ticket.Status.valueOf(request.get("status"));
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }

    @PutMapping("/tickets/{id}/assign")
    public ResponseEntity<TicketResponse> forceAssignTicket(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(ticketService.assignTicket(id, request.get("assigneeId")));
    }
}
