package team03.monew.util.exception.interest;

import team03.monew.util.exception.ErrorCode;

public class EmptyKeywordListException extends InterestException {

  public EmptyKeywordListException() {
    super(ErrorCode.INVALID_KEYWORD_COUNT);
  }
}
