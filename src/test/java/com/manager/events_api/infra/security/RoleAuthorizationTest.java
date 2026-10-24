package com.manager.events_api.infra.security;

import com.manager.events_api.domain.coupon.CouponController;
import com.manager.events_api.domain.coupon.CouponMapper;
import com.manager.events_api.domain.coupon.CouponService;
import com.manager.events_api.domain.event.EventController;
import com.manager.events_api.domain.event.EventMapper;
import com.manager.events_api.domain.event.EventService;
import com.manager.events_api.domain.user.Role;
import com.manager.events_api.domain.user.User;
import com.manager.events_api.domain.user.UserController;
import com.manager.events_api.domain.user.UserMapper;
import com.manager.events_api.domain.user.UserService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.UUID;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, UserController.class,
        EventController.class, CouponController.class})
@Import({SecurityConfig.class, TokenService.class})
class RoleAuthorizationTest {

    private static final String SECRET = "test-secret-with-more-than-32-characters-0123456789";
    private static final String ID = UUID.randomUUID().toString();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("api.security.token.secret", () -> SECRET);
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    UserDetailsService userDetailsService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    EventService eventService;
    @MockitoBean
    EventMapper eventMapper;
    @MockitoBean
    CouponService couponService;
    @MockitoBean
    CouponMapper couponMapper;
    @MockitoBean
    UserMapper userMapper;
  
    private ResultMatcher passedSecurity() {
        return status().is(not(anyOf(is(401), is(403))));
    }

    private String tokenFor(Role role) throws Exception {
        String email = role.name().toLowerCase() + "@example.com";
        User u = new User();
        u.setName(role.name());
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode("pw"));
        u.setRole(role);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(u);

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"pw\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private String bearer(Role role) throws Exception {
        return "Bearer " + tokenFor(role);
    }

    // ---------- /api/user ----------

    @Test
    void signupIsPublic() throws Exception {
        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(passedSecurity());
    }

    @Test
    void userListWithoutTokenIs401() throws Exception {
        mockMvc.perform(get("/api/user")).andExpect(status().isUnauthorized());
    }

    @Test
    void userRoutesForbiddenForViewerAndOrganizer() throws Exception {
        for (Role role : new Role[]{Role.VIEWER, Role.ORGANIZER}) {
            String auth = bearer(role);
            mockMvc.perform(get("/api/user").header("Authorization", auth)).andExpect(status().isForbidden());
            mockMvc.perform(put("/api/user/" + ID).header("Authorization", auth)
                    .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
            mockMvc.perform(delete("/api/user/" + ID).header("Authorization", auth)).andExpect(status().isForbidden());
        }
    }

    @Test
    void userRoutesAllowedForAdmin() throws Exception {
        String auth = bearer(Role.ADMIN);
        mockMvc.perform(get("/api/user").header("Authorization", auth)).andExpect(passedSecurity());
        mockMvc.perform(delete("/api/user/" + ID).header("Authorization", auth)).andExpect(passedSecurity());
    }

    // ---------- /api/event e /api/coupon ----------

    @Test
    void viewerCanReadButNotWrite() throws Exception {
        String auth = bearer(Role.VIEWER);
        for (String base : new String[]{"/api/event", "/api/coupon"}) {
            mockMvc.perform(get(base).header("Authorization", auth)).andExpect(passedSecurity());
            mockMvc.perform(post(base).header("Authorization", auth)
                    .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
            mockMvc.perform(put(base + "/" + ID).header("Authorization", auth)
                    .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
            mockMvc.perform(delete(base + "/" + ID).header("Authorization", auth)).andExpect(status().isForbidden());
        }
    }

    @Test
    void organizerAndAdminPassRouteRulesForWrites() throws Exception {
        for (Role role : new Role[]{Role.ORGANIZER, Role.ADMIN}) {
            String auth = bearer(role);
            for (String base : new String[]{"/api/event", "/api/coupon"}) {
                mockMvc.perform(post(base).header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(passedSecurity());
                mockMvc.perform(delete(base + "/" + ID).header("Authorization", auth)).andExpect(passedSecurity());
            }
        }
    }

    @Test
    void writesWithoutTokenAre401() throws Exception {
        mockMvc.perform(post("/api/event").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
