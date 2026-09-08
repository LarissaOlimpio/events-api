package com.manager.events_api.infra.security;

import com.manager.events_api.domain.user.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository repository;

    public AuthorizationService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return repository.findByEmail(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found")
                );
    }
}

