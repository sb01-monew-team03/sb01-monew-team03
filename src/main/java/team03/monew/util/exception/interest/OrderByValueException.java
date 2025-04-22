package team03.monew.util.exception.interest;

import team03.monew.util.exception.ErrorCode;

public class OrderByValueException extends InterestException {

  public OrderByValueException() {
    super(ErrorCode.INVALID_ORDER_BY);
  }

  public static OrderByValueException withOrderBy(String orderBy) {
    OrderByValueException exception = new OrderByValueException();
    exception.addDetail("orderBy", orderBy);
    return exception;
  }
}
