package team03.monew.service.interest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.entity.interest.Interest;
import team03.monew.mapper.InterestMapper;
import team03.monew.repository.InterestRepository;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private final InterestMapper interestMapper;

  @Override
  public InterestDto create(InterestRegisterRequest request) {

    // request 쪼개기
    String name = request.name();
    List<String> keywords = request.keywords();

    // 예외처리 - name이 80% 이상 유사한 관심사가 있는 경우 관심사 등록 불가
    // TODO: 서비스 단에 findAll() 메서드 작성 시 수정
    List<Interest> interests = interestRepository.findAll();
    for (Interest interest : interests) {
      calculateSimilarity(name, interest.getName());
    }

    // Interest 생성
    Interest interest = new Interest(name);

    // interest에 keyword 추가
    for (String keyword : keywords) {
      interest.addKeyword(keyword);
    }

    // 예외처리 - 키워드 하나 이상 있어야 함
    if (interest.getKeywords().isEmpty()) {
      throw new IllegalArgumentException("키워드는 최소 1개 이상이어야 합니다.");
    }

    // 레포지토리 저장
    interestRepository.save(interest);

    // 결과물 반환
    return interestMapper.toDto(interest, false);
  }

  private void calculateSimilarity(String a, String b) {
    int maxLength = Math.max(a.length(), b.length());

    LevenshteinDistance ld = new LevenshteinDistance();

    double result = 0;
    double temp = ld.apply(a, b);   // 두 문자열 간의 최소 편집 거리 계산 - a를 b로 만들기 위해 필요한 최소 삽입/삭제/수정 횟수
    result = (maxLength - temp) / maxLength;

    if (result >= 0.8) {
      throw new IllegalArgumentException(
          "작성하신 관심사 " + a + "가 이미 존재하는 관심사인 " + b + "와 80% 이상(" + (result * 100) + "%) 유사합니다.");
    }
  }
}