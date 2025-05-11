package team03.monew.controller.interest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.dto.interest.InterestUpdateRequest;
import team03.monew.service.interest.InterestService;

@WebMvcTest(InterestController.class)
public class InterestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private InterestService interestService;

  @Nested
  @DisplayName("create() - 관심사 등록 테스트")
  class CreateTest {

    @Test
    @DisplayName("[success] 관심사 등록 성공")
    void successTest() throws Exception {

      // given
      InterestRegisterRequest request = new InterestRegisterRequest("test", List.of("keyword"));
      InterestDto interestDto = new InterestDto(UUID.randomUUID().toString(), request.name(),
          request.keywords(), 0, false);

      // mocking
      given(interestService.create(request)).willReturn(interestDto);

      // when & then
      mockMvc.perform(post("/api/interests")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
              .sessionAttr("role", "admin"))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").exists());
    }

//    @Test
//    @DisplayName("[fail] role이 admin이 아닐 때 실패")
//    void failTest() throws Exception {
//
//      // given
//      InterestRegisterRequest request = new InterestRegisterRequest("test", List.of("keyword"));
//
//      // when & then
//      mockMvc.perform(post("/api/interests")
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(request))
//              .sessionAttr("role", "user"))
//          .andExpect(status().isForbidden());
//    }
  }

  @Nested
  @DisplayName("update() - 관심사 정보 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("[success] 관심사 수정 성공")
    void successTest() throws Exception {

      // given
      UUID interestId = UUID.randomUUID();
      UUID userId = UUID.randomUUID();
      InterestUpdateRequest request = new InterestUpdateRequest(List.of("update", "keywords"));
      InterestDto interestDto = new InterestDto(interestId.toString(), "test",
          request.keywords(), 0, false);

      // mocking
      given(interestService.update(interestId, request, userId)).willReturn(interestDto);

      // when & then
      mockMvc.perform(patch("/api/interests/{interestId}", interestId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request))
              .header("MoNew-Request-User-ID", userId)
              .sessionAttr("role", "admin"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(interestId.toString()))
          .andExpect(jsonPath("$.keywords[0]").value("update"));
    }

//    @Test
//    @DisplayName("[fail] role이 admin이 아닐 때 실패")
//    void failTest() throws Exception {
//
//      // given
//      UUID interestId = UUID.randomUUID();
//      UUID userId = UUID.randomUUID();
//      InterestUpdateRequest request = new InterestUpdateRequest(List.of("update", "keywords"));
//
//      // when & then
//      mockMvc.perform(patch("/api/interests/{interestId}", interestId)
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(request))
//              .header("MoNew-Request-User-ID", userId)
//              .sessionAttr("role", "user"))
//          .andExpect(status().isForbidden());
//    }
  }

  @Nested
  @DisplayName("delete() - 관심사 물리 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("[success] 관심사 물리 삭제 성공")
    void successTest() throws Exception {

      // given
      UUID interestId = UUID.randomUUID();

      // when & then
      mockMvc.perform(delete("/api/interests/{interestId}", interestId)
              .contentType(MediaType.APPLICATION_JSON)
              .sessionAttr("role", "admin"))
          .andExpect(status().isNoContent());
    }

//    @Test
//    @DisplayName("[fail] role이 admin이 아닐 때 실패")
//    void failTest() throws Exception {
//
//      // given
//      UUID interestId = UUID.randomUUID();
//
//      // when & then
//      mockMvc.perform(delete("/api/interests/{interestId}", interestId)
//              .contentType(MediaType.APPLICATION_JSON)
//              .sessionAttr("role", "user"))
//          .andExpect(status().isForbidden());
//    }
  }

  @Test
  @DisplayName("find() - 관심사 목록 조회 테스트")
  void findTest() throws Exception {

    // given
    InterestFindRequest request = new InterestFindRequest("test", "name", "asc",
        null, null, 50);
    UUID userId = UUID.randomUUID();
    InterestDto interestDto = new InterestDto(UUID.randomUUID().toString(), "test",
        List.of("keyword"), 0, false);
    CursorPageResponse<InterestDto> response = new CursorPageResponse<>(List.of(interestDto),
        null, null, 1, 1, false);

    // mocking
    given(interestService.find(request, userId)).willReturn(response);

    // when & then
    mockMvc.perform(get("/api/interests")
            .param("keyword", request.keyword())
            .param("orderBy", request.orderBy())
            .param("direction", request.direction())
            .param("cursor", request.cursor())
            .param("after", request.after())
            .param("limit", String.valueOf(request.limit()))
            .header("MoNew-Request-User-ID", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").exists());
  }
}
