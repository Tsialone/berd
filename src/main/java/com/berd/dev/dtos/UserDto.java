package com.berd.dev.dtos;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String maskedEmail;
    private boolean active;
}
