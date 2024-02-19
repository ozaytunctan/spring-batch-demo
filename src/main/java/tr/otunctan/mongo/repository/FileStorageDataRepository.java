package tr.otunctan.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tr.otunctan.mongo.entity.FileStorageData;

@Repository
public interface FileStorageDataRepository extends MongoRepository<FileStorageData,String> {



}
