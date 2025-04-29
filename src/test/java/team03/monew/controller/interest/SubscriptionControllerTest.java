package team03.monew.controller.interest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team03.monew.dto.interest.SubscriptionDto;
import team03.monew.service.interest.SubscriptionService;

@WebMvcTest(SubscriptionController.class)
public class SubscriptionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private SubscriptionService subscriptionService;

  @Test
  @DisplayName("create() - 관심사 구독 테스트")
  void createTest() throws Exception {

    // given
    UUID userId = UUID.randomUUID();
    UUID interestId = UUID.randomUUID();
    SubscriptionDto subscriptionDto = new SubscriptionDto(UUID.randomUUID().toString(),
        interestId.toString(),
        "test", List.of("keyword"), 1, Instant.now());

    // mocking
    given(subscriptionService.create(userId, interestId)).willReturn(subscriptionDto);

    // when & then
    mockMvc.perform(post("/api/interests/{interestId}/subscriptions", interestId)
            .header("MoNew-Request-User-ID", userId))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  @DisplayName("delete() - 관심사 구독 취소 테스트")
  void deleteTest() throws Exception {

    // given
    UUID userId = UUID.randomUUID();
    UUID interestId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/interests/{interestId}/subscriptions", interestId)
            .header("MoNew-Request-User-ID", userId))
        .andExpect(status().isNoContent());
  }
}
