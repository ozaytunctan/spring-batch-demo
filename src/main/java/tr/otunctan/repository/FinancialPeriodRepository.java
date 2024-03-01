package tr.otunctan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.otunctan.entity.FinancialPeriod;

public interface FinancialPeriodRepository extends JpaRepository<FinancialPeriod, Long> {


}
