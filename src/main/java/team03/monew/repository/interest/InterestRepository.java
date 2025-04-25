package team03.monew.repository.interest;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.interest.Interest;
import team03.monew.repository.interest.custom.CustomInterestRepository;

public interface InterestRepository extends JpaRepository<Interest, UUID>,
    CustomInterestRepository {

}
