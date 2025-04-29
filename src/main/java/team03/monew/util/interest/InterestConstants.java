package team03.monew.util.interest;

// 관심사 관리 상수
public class InterestConstants {

  // 낙관적 락 충돌 시 대기 시간
  public static final int LOCK_WAIT_TIME_MS = 100;
  
  // 낙관적 락 최대 시도 횟수
  public static final int MAX_RETRY_COUNT = 5;
}
