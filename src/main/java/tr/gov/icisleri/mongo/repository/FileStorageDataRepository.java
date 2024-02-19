package tr.gov.icisleri.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tr.gov.icisleri.mongo.entity.FileStorageData;

@Repository
public interface FileStorageDataRepository extends MongoRepository<FileStorageData,String> {



}
