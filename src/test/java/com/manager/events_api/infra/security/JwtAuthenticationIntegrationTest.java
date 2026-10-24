package com.manager.events_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.manager.events_api.domain.event.EventMapper;
import com.manager.events_api.domain.event.EventController;
import com.manager.events_api.domain.event.EventService;
import com.manager.events_api.domain.user.Role;
import com.manager.events_api.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.data.domain.Page.empty;

@WebMvcTest(controllers = {AuthController.class, EventController.class})
@Import({SecurityConfig.class, TokenService.class})
@TestPropertySource(properties = "api.security.token.expiration-hours=2")
class JwtAuthenticationIntegrationTest {

    private static final String SECRET = "test-secret-with-more-than-32-characters-0123456789";

    @DynamicPropertySource
    static void tokenProperties(DynamicPropertyRegistry registry) {
        registry.add("api.security.token.secret", () -> SECRET);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("correct-password"));
        user.setRole(Role.VIEWER);

        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(user);
        when(eventService.getUpComingEvents(anyInt(), anyInt(), nullable(String.class), nullable(String.class),
                nullable(String.class), nullable(java.time.OffsetDateTime.class), nullable(java.time.OffsetDateTime.class)))
                .thenReturn(empty());
    }

    @Test
    void validLoginReturnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"correct-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void wrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/event"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenAllowsProtectedRoute() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/api/event").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void expiredTokenReturnsUnauthorized() throws Exception {
        String token = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().minusSeconds(1))
                .sign(Algorithm.HMAC256(SECRET));

        mockMvc.perform(get("/api/event").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tamperedTokenReturnsUnauthorized() throws Exception {
        String[] parts = loginAndGetToken().split("\\.");
        parts[2] = (parts[2].charAt(0) == 'A' ? "B" : "A") + parts[2].substring(1);
        String tamperedToken = String.join(".", parts);

        mockMvc.perform(get("/api/event").header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"correct-password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.token");
    }

    @Test
    void manuallySignedValidTokenIsAccepted() throws Exception {
        String token = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(SECRET));

        assertEquals(user.getEmail(), tokenService.validateToken(token));

        mockMvc.perform(get("/api/event").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
