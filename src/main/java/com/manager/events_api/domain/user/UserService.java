package com.manager.events_api.domain.user;

import com.manager.events_api.infra.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.manager.events_api.domain.user.Role.ADMIN;

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
        newUser.setRole(Role.VIEWER);

        return userRepository.save(newUser);
    }

    private void ensureEmailIsAvailable(String data) {
        boolean exists = userRepository.existsByEmail(data);
        if (exists) {
            throw new BusinessException("User with email " + data + " already exists");
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
    public User updateUser(UUID userId, @Valid UserUpdateRequestDTO data, User currentUser) {
        if (!currentUser.getId().equals(userId)
                && currentUser.getRole() != ADMIN) {

            throw new BusinessException(
                    "You do not have permission to update this user");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User with id " + userId + " not found"));
        ensureEmailIsAvailableInUpdate(data.email(), userId);
        userMapper.updateUserFromDTO(data, user);
        return userRepository.save(user);
    }

    @Transactional
    public User updateRole(UUID id, Role newRole, User currentUser) {
        if (currentUser.getRole() != ADMIN) {
            throw new BusinessException("Only administrators can change user roles");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User with id " + id + " not found"));
        if (id.equals(currentUser.getId())) {
            throw new BusinessException("Admin cannot change their own role");
        }
        user.setRole(newRole);
        return userRepository.save(user);
    }

    private void ensureEmailIsAvailableInUpdate(String email, UUID userId) {
        boolean exists = userRepository.existsByEmailAndIdNot(email, userId);
        if (exists) {
            throw new BusinessException("User with email " + email + " already exists");
        }
    }

    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User with id " + userId + " not found"));
        userRepository.delete(user);
    }
}
