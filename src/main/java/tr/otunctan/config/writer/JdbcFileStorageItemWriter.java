package tr.otunctan.config.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import tr.otunctan.entity.AccFileStorage;
import tr.otunctan.repository.AccFileStorageDataRepository;


public class JdbcFileStorageItemWriter implements ItemWriter<AccFileStorage> {


    private final Logger logger= LoggerFactory.getLogger(JdbcFileStorageItemWriter.class);
    private final AccFileStorageDataRepository fileStorageDataRepository;

    public JdbcFileStorageItemWriter(AccFileStorageDataRepository fileStorageDataRepository) {
        this.fileStorageDataRepository = fileStorageDataRepository;
    }


    @Override
    public void write(Chunk<? extends AccFileStorage> fileStorageDatas) throws Exception {

        logger.info("Db ye yazılıyor.. ->"+fileStorageDatas.size());
        try{
            fileStorageDataRepository.saveAllAndFlush(fileStorageDatas.getItems());
        }
        catch (Exception e){
            logger.info("Hata ",e);
        }

        logger.info("Db ye yazma bitti..");

    }




}
