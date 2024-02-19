package tr.gov.icisleri.config;

import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.PlatformTransactionManager;
import tr.gov.icisleri.config.decider.FileTransferDecider;
import tr.gov.icisleri.config.listener.JobCompletionNotificationListener;
import tr.gov.icisleri.config.reader.ReadingObjectItemReader;
import tr.gov.icisleri.config.writer.JdbcFileStorageItemWriter;
import tr.gov.icisleri.entity.AccFileStorage;
import tr.gov.icisleri.entity.FileStorageType;
import tr.gov.icisleri.entity.FinancialPeriod;
import tr.gov.icisleri.mongo.entity.FileStorageData;
import tr.gov.icisleri.mongo.repository.FileStorageDataRepository;
import tr.gov.icisleri.repository.AccFileStorageDataRepository;
import tr.gov.icisleri.utils.TextNormalizer;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class FileTransferBatchConfig {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${batch.execution.user-id}")
    private Long userId;

    @Value("${batch.execution.mong.collection-name}")
    private String collectionName;

    private final AccFileStorageDataRepository accFileStorageDataRepository;
    private final FileStorageDataRepository fileStorageDataRepository;

    private final JobCompletionNotificationListener jobCompletionNotificationListener;

    public FileTransferBatchConfig(AccFileStorageDataRepository accFileStorageDataRepository, FileStorageDataRepository fileStorageDataRepository,
                                   JobCompletionNotificationListener jobCompletionNotificationListener) {
        this.accFileStorageDataRepository = accFileStorageDataRepository;
        this.fileStorageDataRepository = fileStorageDataRepository;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
    }


    @Bean
    @StepScope
    public ItemReader<Resource> fileStorageDataItemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        Resource[] resources = new Resource[1];
        try {
            resources = Files.walk(Paths.get(pathToFile))
                    .filter(Files::isRegularFile)
                    .map(FileSystemResource::new)
                    .toArray(Resource[]::new);
        } catch (IOException e) {
            logger.error("fileStorageDataItemReader error()", e);
        }

        ResourcesItemReader resourcesItemReader = new ResourcesItemReader();
        resourcesItemReader.setResources(resources);
        return resourcesItemReader;
    }


    @Bean
    public ItemProcessor<Resource, FileStorageData> processor() {
        return (resource) -> {
            File file = resource.getFile();
            String realPath = TextNormalizer.replace(file.getParentFile().getPath(), File.separator, "/");
            String[] paths = realPath.split("/");

            FileStorageData fileStorageData = new FileStorageData();
            fileStorageData.setBelediyeId(Long.parseLong(paths[5]));
            fileStorageData.setModul("Muhasebe");
            fileStorageData.setIslemYapanTc("32377152358");
            fileStorageData.setIslemZamani(LocalDateTime.now());
            fileStorageData.setRealPath(realPath);
            fileStorageData.setDosyaAdi(file.getName());
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            fileStorageData.setMimeType(mimeType);
            fileStorageData.setIcerik(new Binary(resource.getContentAsByteArray()));
            return fileStorageData;
        };

    }

    @Bean
    public MongoItemWriter<FileStorageData> mongoItemWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<FileStorageData>()//
                .template(mongoTemplate)//
                .collection(collectionName)//
                .build();
    }

    @Bean
    public Step tranferMongoStep(JobRepository jobRepository,
                                 ItemReader<Resource> fileStorageDataItemReader,
                                 MongoItemWriter<FileStorageData> mongoItemWriter,
                                 PlatformTransactionManager transactionManager


    ) {

        return new StepBuilder("step1", jobRepository)
                .<Resource, FileStorageData>chunk(50, transactionManager)
                .reader(fileStorageDataItemReader)
                .processor(processor())
                .writer(mongoItemWriter)
                .faultTolerant()
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public Job transferFileMongoJob(
            JobRepository jobRepository,
            Step tranferMongoStep,
            Step transferFileMongoToPostgresDb
    ) {
        return new JobBuilder("transferFileMongoJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tranferMongoStep)
                .next(new FileTransferDecider()).on("ISLEM_TAMAMLANDI").to(transferFileMongoToPostgresDb).end()
                .listener(jobCompletionNotificationListener)
                .build();
    }




    @Bean
    public Step transferFileMongoToPostgresDb(JobRepository jobRepository,
                                              ItemReader<FileStorageData> mongoFileDataReader,
                                              JdbcFileStorageItemWriter fileStorageItemWriter,
                                              PlatformTransactionManager transactionManager


    ) {
        return new StepBuilder("step2", jobRepository)
                .<FileStorageData, AccFileStorage>chunk(50, transactionManager)
                .reader(mongoFileDataReader)
                .processor(storageDataAccFileStorageItemProcessor())
                .writer(fileStorageItemWriter)
                .faultTolerant()
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public ReadingObjectItemReader readingObjectItemReader() {
        return new ReadingObjectItemReader();
    }


    @Bean
    @StepScope
    public ItemReader<FileStorageData> mongoFileDataReader( @Value("#{jobParameters[fullPathFileName]}") String pathToFile,
                                                            MongoTemplate mongoTemplate
    ) {

        Query query = new Query();
        query.addCriteria(Criteria.where("realPath").regex("^"+pathToFile));

        Map<String, Sort.Direction> sorts = new HashMap<>(1);
        sorts.put("islemZamani", Sort.Direction.ASC);

        return new MongoPagingItemReaderBuilder<FileStorageData>()
                .name("mongoFileDataReader")
                .collection(collectionName)
                .query(query)
                .pageSize(50)
                .sorts(sorts)
                .targetType(FileStorageData.class)
                .template(mongoTemplate)
                .build();
    }


    @Bean
    public ItemProcessor<FileStorageData, AccFileStorage> storageDataAccFileStorageItemProcessor() {
        return (fileStorageData) -> {

            String realPath = fileStorageData.getRealPath();
            String[] paths = realPath.split("/");
            FinancialPeriod financialPeriod = FinancialPeriod.of(Short.parseShort(paths[7]), Long.parseLong(paths[5]));

            AccFileStorage storage = new AccFileStorage();
            storage.setUserId(userId);
            storage.setFileName(fileStorageData.getDosyaAdi());
            storage.setContentDocId(fileStorageData.getId());
            storage.setType(FileStorageType.SLIP_DETAIL_ADDENDA);
            storage.setContentType(fileStorageData.getMimeType());
            storage.setFullPath(realPath);
            storage.setFinancialPeriod(financialPeriod);
            return storage;
        };

    }

    @Bean
    public JdbcFileStorageItemWriter fileStorageItemWriter() {
        return new JdbcFileStorageItemWriter(accFileStorageDataRepository);
    }


    @Bean
    public SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }

    /*
    @Bean
    public SkipListener skipListener() {
        return new StepSkipListener();
    }
    */


    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }


}
