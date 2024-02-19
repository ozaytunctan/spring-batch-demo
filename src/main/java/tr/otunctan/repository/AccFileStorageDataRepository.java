package tr.otunctan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.otunctan.entity.AccFileStorage;


@Repository
public interface AccFileStorageDataRepository extends JpaRepository<AccFileStorage, Long> {

}
