package com.manager.events_api.domain.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO data, UriComponentsBuilder uriBuilder) {
        User newUser = this.userService.createUser(data);
        UserResponseDTO response = userMapper.toUserResponseDTO(newUser);
        var uri = uriBuilder.path("/api/user/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public Page<UserResponseDTO> findUsers(@RequestParam(required = false) String name, @PageableDefault(
            size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.findUsers(name, pageable);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID userId, @RequestBody @Valid UserUpdateRequestDTO data) {
        User updatedUser = this.userService.updateUser(userId, data);
        return ResponseEntity.ok(userMapper.toUserResponseDTO(updatedUser));
    }


}
