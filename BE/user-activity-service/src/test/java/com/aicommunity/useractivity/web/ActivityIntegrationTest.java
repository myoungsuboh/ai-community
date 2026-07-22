package com.aicommunity.useractivity.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicommunity.common.security.JwtTokenProvider;
import com.aicommunity.common.security.Role;
import com.aicommunity.common.security.UserPrincipal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtTokenProvider tokenProvider;

    private String bearerFor(UUID userId) {
        String token = tokenProvider.createAccessToken(
                new UserPrincipal(userId, "u@ai.community", "회원", Role.MEMBER));
        return "Bearer " + token;
    }

    @Test
    void reactionToggle_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/v1/cards/{c}/reactions", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"LIKE\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void commentList_isPublic() throws Exception {
        mockMvc.perform(get("/api/v1/cards/{c}/comments", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void authenticatedUser_canToggleReaction() throws Exception {
        UUID user = UUID.randomUUID();
        UUID card = UUID.randomUUID();
        String auth = bearerFor(user);

        mockMvc.perform(post("/api/v1/cards/{c}/reactions", card)
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"LIKE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdded").value(true));

        // 다시 토글하면 해제
        mockMvc.perform(post("/api/v1/cards/{c}/reactions", card)
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"LIKE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAdded").value(false));
    }

    @Test
    void authenticatedUser_canComment() throws Exception {
        mockMvc.perform(post("/api/v1/cards/{c}/comments", UUID.randomUUID())
                        .header("Authorization", bearerFor(UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"좋은 카드네요!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("좋은 카드네요!"));
    }

    @Test
    void bookmarks_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users/{u}/bookmarks", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}
