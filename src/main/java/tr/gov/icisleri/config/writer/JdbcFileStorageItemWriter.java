package tr.gov.icisleri.config.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import tr.gov.icisleri.entity.AccFileStorage;
import tr.gov.icisleri.mongo.entity.FileStorageData;
import tr.gov.icisleri.repository.AccFileStorageDataRepository;

import java.util.List;


public class JdbcFileStorageItemWriter implements ItemWriter<AccFileStorage> {

    private final AccFileStorageDataRepository fileStorageDataRepository;

    public JdbcFileStorageItemWriter(AccFileStorageDataRepository fileStorageDataRepository) {
        this.fileStorageDataRepository = fileStorageDataRepository;
    }


    @Override
    public void write(Chunk<? extends AccFileStorage> fileStorageDatas) throws Exception {

        System.out.println("Writer Thread " + Thread.currentThread().getName());

       fileStorageDataRepository.saveAll(fileStorageDatas.getItems());

    }




}
