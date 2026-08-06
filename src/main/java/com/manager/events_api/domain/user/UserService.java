package com.manager.events_api.domain.user;

import com.manager.events_api.infra.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User createUser(UserRequestDTO data) {
        ensureEmailIsAvailable(data.email());

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setRole(parseRole(data.role()));

        return userRepository.save(newUser);
    }

    private void ensureEmailIsAvailable(String data) {
        boolean exists = userRepository.existsByEmail(data);
        if (exists) {
            throw new BusinessException("User with email " + data + " already exists");
        }

    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role: " + role);
        }
    }

    public Page<UserResponseDTO> findUsers(String name, Pageable pageable) {
        Page<User> users;
        if (name == null || name.isBlank()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }
        return users.map(userMapper::toUserResponseDTO);
    }

    @Transactional
    public User updateUser(UUID userId, @Valid UserUpdateRequestDTO data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User with id " + userId + " not found"));
        ensureEmailIsAvailableInUpdate(data.email(), userId);
        userMapper.updateUserFromDTO(data, user);
        return userRepository.save(user);
    }

    private void ensureEmailIsAvailableInUpdate(String email, UUID userId) {
        boolean exists = userRepository.existsByEmailAndIdNot(email, userId);
        if (exists) {
            throw new BusinessException("User with email " + email + " already exists");
        }
    }
}
