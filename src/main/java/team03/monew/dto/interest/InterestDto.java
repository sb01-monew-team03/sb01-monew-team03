package team03.monew.dto.interest;

import java.util.List;

public record InterestDto(
  String id,
  String name,
  List<String> keywords,
  long subscriberCount,
  boolean subscribedByMe
) {

}
