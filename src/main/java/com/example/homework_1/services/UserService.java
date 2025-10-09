package com.example.homework_1.services;

import com.example.homework_1.dto.UserDto;
import com.example.homework_1.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public synchronized List<UserDto> getAllUsers() {
        List<UserDto> dtos = new ArrayList<>();
        for (User user : users.values()) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dtos.add(dto);
        }
        return dtos;
    }

    public synchronized UserDto getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public synchronized UserDto createUser(UserDto dto) {
        dto.validate();
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        users.put(user.getId(), user);
        dto.setId(user.getId());
        return dto;
    }

    public synchronized UserDto updateUser(Long id, UserDto dto) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        dto.validate();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        users.put(id, user);
        dto.setId(id);
        return dto;
    }

    public synchronized UserDto patchUser(Long id, Map<String, Object> updates) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        if (updates.containsKey("name")) {
            String name = (String) updates.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            user.setName(name);
        }
        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            user.setEmail(email);
        }
        users.put(id, user);
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public synchronized void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        users.remove(id);
    }
}
