package com.manager.events_api.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.manager.events_api.infra.exceptions.BusinessException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private User userWithId(UUID id, Role role) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setRole(role);
        return user;
    }

    @Test
    void shouldAlwaysSaveNewUserAsViewer() {
        UserRequestDTO request = new UserRequestDTO("Test User", "test@example.com", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(Role.VIEWER, userCaptor.getValue().getRole());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
        verify(userRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void shouldRejectDuplicateEmailWithoutEncodingOrSaving() {
        UserRequestDTO request = new UserRequestDTO("Test User", "test@example.com", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.createUser(request));

        verify(passwordEncoder, never()).encode(request.password());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void shouldAllowUserToUpdateOwnData() {
        UUID id = UUID.randomUUID();
        User user = userWithId(id, Role.VIEWER);
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO("teste", "test@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(id, dto, user);

        assertEquals(user, result);
        verify(userMapper).updateUserFromDTO(dto, user);
        verify(userRepository).save(user);
    }

    @Test
    void shouldAllowAdminToUpdateAnotherUser() {
        UUID targetId = UUID.randomUUID();
        User target = userWithId(targetId, Role.VIEWER);
        User admin = userWithId(UUID.randomUUID(), Role.ADMIN);
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO("teste", "test@example.com");
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(userRepository.save(target)).thenReturn(target);

        User result = userService.updateUser(targetId, dto, admin);

        assertEquals(target, result);
        verify(userMapper).updateUserFromDTO(dto, target);
        verify(userRepository).save(target);
    }

    @Test
    void shouldRejectNonAdminUpdatingAnotherUser() {
        UUID targetId = UUID.randomUUID();
        User currentUser = userWithId(UUID.randomUUID(), Role.ORGANIZER);
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO("teste", "test@example.com");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.updateUser(targetId, dto, currentUser));

        assertEquals("You do not have permission to update this user", ex.getMessage());
        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void shouldRejectUpdateForMissingUserWithoutSaving() {
        UUID targetId = UUID.randomUUID();
        User admin = userWithId(UUID.randomUUID(), Role.ADMIN);
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO("teste", "test@example.com");
        when(userRepository.findById(targetId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> userService.updateUser(targetId, dto, admin));

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldUpdateUserRole() {
        UUID id = UUID.randomUUID();
        User target = userWithId(id, Role.VIEWER);
        User currentUser = userWithId(UUID.randomUUID(), Role.ADMIN);
        when(userRepository.findById(id)).thenReturn(Optional.of(target));
        when(userRepository.save(target)).thenReturn(target);

        User result = userService.updateRole(id, Role.ORGANIZER, currentUser);

        assertEquals(Role.ORGANIZER, result.getRole());
        verify(userRepository).save(target);
    }

    @Test
    void shouldRejectRoleUpdateForMissingUserWithoutSaving() {
        UUID id = UUID.randomUUID();
        User currentUser = userWithId(UUID.randomUUID(), Role.ADMIN);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> userService.updateRole(id, Role.ORGANIZER, currentUser));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectChangingOwnRoleWithoutSaving() {
        UUID id = UUID.randomUUID();
        User currentUser = userWithId(id, Role.ADMIN);

        assertThrows(BusinessException.class,
                () -> userService.updateRole(id, Role.VIEWER, currentUser));

        verify(userRepository, never()).save(any(User.class));
    }
}

