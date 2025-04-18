package team03.monew.service.interest;

import team03.monew.dto.interest.InterestRegisterRequest;
import team03.monew.entity.interest.Interest;

public interface InterestService {
  Interest create(InterestRegisterRequest request);
}