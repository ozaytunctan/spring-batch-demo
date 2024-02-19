package tr.otunctan.config.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import tr.otunctan.entity.AccFileStorage;
import tr.otunctan.repository.AccFileStorageDataRepository;


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
