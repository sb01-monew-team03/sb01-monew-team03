package team03.monew.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // user 에러 코드
  USER_NOT_FOUND("USER_001", "User not found", HttpStatus.NOT_FOUND),
  INVALID_USER_CREDENTIALS("USER_002", "Wrong password", HttpStatus.UNAUTHORIZED),
  DUPLICATE_USER("USER_003", "Duplicate user", HttpStatus.CONFLICT),

  // interest 에러 코드
  SIMILAR_INTEREST_EXISTS("INTEREST_001", "작성하신 관심사가 이미 존재하는 관심사와 유사합니다.", HttpStatus.CONFLICT),
  INTEREST_NOT_FOUND("INTEREST_002", "해당 관심사가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  INVALID_KEYWORD_COUNT("INTEREST_003", "키워드는 최소 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
  INVALID_ORDER_BY("INTEREST_004", "정렬은 '이름' 또는 '구독자 수' 기준으로만 가능합니다.", HttpStatus.BAD_REQUEST),

  // Server 에러 코드
  INTERNAL_SERVER_ERROR("SERVER_001","서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

  // Request 에러 코드
  INVALID_REQUEST("REQUEST_001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

  // Comment 관련 에러 코드
  ALREADY_LIKED("COMMENT_001", "이미 좋아요된 댓글입니다.", HttpStatus.CONFLICT),
  COMMENT_NOT_FOUND("COMMENT_002", "댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  COMMENT_NOT_FOUND_BY_ARTICLE("COMMENT_003", "해당 게시글에 댓글이 없습니다.", HttpStatus.NOT_FOUND),
  LIKE_NOT_FOUND("COMMENT_004", "댓글 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;

  ErrorCode(String code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
