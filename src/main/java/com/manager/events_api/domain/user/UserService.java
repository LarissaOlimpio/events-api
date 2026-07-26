package com.manager.events_api.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRequestDTO data) {
        ensureEmailIsAvaliable(data.email());
        Role role = parseRole(data.role());

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(passwordEncoder.encode(data.password()));
        newUser.setRole(Role.valueOf(data.role()));

        return userRepository.save(newUser);
    }

    private void ensureEmailIsAvaliable(String data) {
        return;
    }

    private Role parseRole(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
