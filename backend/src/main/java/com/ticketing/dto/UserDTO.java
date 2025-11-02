package com.ticketing.dto;

import com.ticketing.model.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private User.Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}
