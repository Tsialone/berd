package com.berd.dev.mappers;

import java.util.List;

import com.berd.dev.dtos.UserDto;
import com.berd.dev.models.User;

public class UserMapper {
    public static UserDto toDto (User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setMaskedEmail(user.getMaskedEmail());
        dto.setActive(user.isActive());
        return dto;
    }

    public static List<UserDto> toDtos (List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
