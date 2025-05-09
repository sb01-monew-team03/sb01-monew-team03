package team03.monew.controller.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team03.monew.dto.article.ArticleViewDto;
import team03.monew.dto.comments.CommentActivityDto;
import team03.monew.dto.comments.CommentLikeActivityDto;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.dto.user.ActivityDto;
import team03.monew.service.activity.ActivityService;
import team03.monew.util.exception.user.UserNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ActivityController.class)
class ActivityControllerTest {

  @MockitoBean
  private ActivityService activityService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("사용자 ID로 활동 내역을 조회한다")
  void find() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();

    UUID interestId = UUID.randomUUID();
    SubscriptionDto subscriptionDto = new SubscriptionDto(
        UUID.randomUUID().toString(),
        interestId.toString(),
        "Technology",
        List.of("Backend", "AI"),
        100L,
        now
    );

    UUID articleId = UUID.randomUUID();
    CommentActivityDto commentActivityDto = new CommentActivityDto(
        UUID.randomUUID(),
        articleId,
        "Test Article",
        userId,
        "testuser",
        "Test Comment",
        10L,
        now
    );

    UUID commentId = UUID.randomUUID();
    UUID commentUserId = UUID.randomUUID();
    CommentLikeActivityDto commentLikeActivityDto = new CommentLikeActivityDto(
        UUID.randomUUID(),
        now,
        commentId,
        articleId,
        "Test Article",
        commentUserId,
        "testuser",
        "Test Comment",
        4,
        now
    );

    ArticleViewDto articleViewDto = new ArticleViewDto(
        UUID.randomUUID(),
        userId,
        now,
        articleId,
        "NAVER",
        "naver.com",
        "Test Article",
        now,
        "test article",
        5L,
        70L
    );

    ActivityDto userActivityDto = new ActivityDto(
        userId,
        "test@example.com",
        "testUser",
        now,
        List.of(subscriptionDto),
        List.of(commentActivityDto),
        List.of(commentLikeActivityDto),
        List.of(articleViewDto)
    );

    given(activityService.findUserActivity(userId)).willReturn(userActivityDto);

    // When & Then
    mockMvc.perform(get("/api/user-activities/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.nickname").value("testUser"))
        .andExpect(jsonPath("$.subscriptions").isArray())
        .andExpect(jsonPath("$.subscriptions.length()").value(1))
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments.length()").value(1))
        .andExpect(jsonPath("$.commentLikes").isArray())
        .andExpect(jsonPath("$.commentLikes.length()").value(1))
        .andExpect(jsonPath("$.articleViews").isArray())
        .andExpect(jsonPath("$.articleViews.length()").value(1));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 ID로 조회 시 404 응답을 반환한다")
  void findWithNonExistingUserId() throws Exception {
    // Given
    UUID nonExistingUserId = UUID.randomUUID();
    given(activityService.findUserActivity(nonExistingUserId))
        .willThrow(UserNotFoundException.withId(nonExistingUserId));

    // When & Then
    mockMvc.perform(get("/api/user-activities/{userId}", nonExistingUserId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}