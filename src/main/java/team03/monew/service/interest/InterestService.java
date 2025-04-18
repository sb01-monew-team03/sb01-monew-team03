package team03.monew.service.interest;

import team03.monew.dto.interest.InterestDto;
import team03.monew.dto.interest.InterestRegisterRequest;

public interface InterestService {

  InterestDto create(InterestRegisterRequest request);
}