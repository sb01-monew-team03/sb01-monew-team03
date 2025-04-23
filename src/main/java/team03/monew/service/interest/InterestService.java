package team03.monew.service.interest;

import java.util.Optional;
import java.util.UUID;
import team03.monew.dto.common.CursorPageResponse;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestFindRequest;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.dto.interest.InterestUpdateRequest;
import team03.monew.entity.interest.Interest;

public interface InterestService {

  // 관심사 등록
  InterestDto create(InterestRegisterRequest request);

  // 관심사 수정
  InterestDto update(UUID interestId, InterestUpdateRequest request);

  // 관심사 삭제
  void delete(UUID interestId);

  // 관심사 검색
  CursorPageResponse<InterestDto> find(InterestFindRequest request);

  // 관심사 구독자 수 증가
  void increaseSubscriberCount(Interest interest);

  // 관심사 구독자 수 감소
  void decreaseSubscriberCount(Interest interest);

  // 관심사 엔티티 반환
  Optional<Interest> getInterestEntity(UUID interestId);
}