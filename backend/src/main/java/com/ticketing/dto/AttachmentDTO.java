package com.ticketing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttachmentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private UserDTO user;
    private LocalDateTime uploadedAt;
}
