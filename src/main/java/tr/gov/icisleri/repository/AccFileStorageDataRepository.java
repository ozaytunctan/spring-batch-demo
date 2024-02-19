package tr.gov.icisleri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.gov.icisleri.entity.AccFileStorage;


@Repository
public interface AccFileStorageDataRepository extends JpaRepository<AccFileStorage, Long> {

}
