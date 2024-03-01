package tr.otunctan.config;

import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
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
import tr.otunctan.entity.AccFileStorage;
import tr.otunctan.entity.FileStorageType;
import tr.otunctan.config.decider.FileTransferDecider;
import tr.otunctan.config.listener.JobCompletionNotificationListener;
import tr.otunctan.config.reader.ReadingObjectItemReader;
import tr.otunctan.entity.FinancialPeriod;
import tr.otunctan.mongo.entity.FileStorageData;
import tr.otunctan.mongo.repository.FileStorageDataRepository;
import tr.otunctan.repository.AccFileStorageDataRepository;
import tr.otunctan.repository.FinancialPeriodRepository;
import tr.otunctan.utils.FileUtils;
import tr.otunctan.utils.TextNormalizer;

import javax.sql.DataSource;
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

    private final FinancialPeriodRepository financialPeriodRepository;

    private final JobCompletionNotificationListener jobCompletionNotificationListener;

    private final EntityManagerFactory entityManagerFactory;

    private final DataSource dataSource;

    public FileTransferBatchConfig(AccFileStorageDataRepository accFileStorageDataRepository,
                                   FileStorageDataRepository fileStorageDataRepository,
                                   FinancialPeriodRepository financialPeriodRepository,
                                   JobCompletionNotificationListener jobCompletionNotificationListener,
                                   EntityManagerFactory entityManagerFactory,
                                   DataSource dataSource) {

        this.accFileStorageDataRepository = accFileStorageDataRepository;
        this.fileStorageDataRepository = fileStorageDataRepository;
        this.financialPeriodRepository = financialPeriodRepository;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
        this.entityManagerFactory = entityManagerFactory;
        this.dataSource = dataSource;
    }


    @Bean
    @StepScope
    public ItemReader<Resource> fileStorageDataItemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        Resource[] resources = new Resource[1];
        try {
            resources = Files.walk(Paths.get(pathToFile))
                    .filter(Files::isRegularFile)
                    .filter(f-> FileUtils.sizeInMegaBytes(f.toFile().length())<=16.0)
                    .map(FileSystemResource::new)
                    .filter(f->f.getFilename()!=null && !(f.getFilename().endsWith(".modeshape.json")))
                    .toArray(Resource[]::new);
        } catch (IOException e) {
            logger.error("fileStorageDataItemReader error()", e);
        }

        logger.info("Dosya sayısı:"+resources.length);
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
            fileStorageData.setIslemYapanTc(32377152358L);
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
                .<Resource, FileStorageData>chunk(2000, transactionManager)
                .reader(fileStorageDataItemReader)
                .processor(processor())
                .writer(mongoItemWriter)
                .faultTolerant()
                .skipLimit(2000000)
                .skip(Exception.class)
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
                                              JpaItemWriter<AccFileStorage> fileStorageItemWriter,
                                              PlatformTransactionManager transactionManager


    ) {
        return new StepBuilder("step2", jobRepository)
                .<FileStorageData, AccFileStorage>chunk(2000, transactionManager)
                .reader(mongoFileDataReader)
                .processor(storageDataAccFileStorageItemProcessor())
                .writer(fileStorageItemWriter)
                .faultTolerant()
                .skipLimit(2000000)
                .skip(Exception.class)
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
        query.addCriteria(
                Criteria.where("realPath")
                        .regex("^"+pathToFile)
        );
        query.addCriteria(Criteria.where("islemYapanTc").is(32377152358L));
        query.addCriteria(Criteria.where("islemZamani").gte(LocalDateTime.now().minusHours(4)));

        Map<String, Sort.Direction> sorts = new HashMap<>(1);
        sorts.put("islemZamani", Sort.Direction.ASC);

        return new MongoPagingItemReaderBuilder<FileStorageData>()
                .name("mongoFileDataReader")
                .collection(collectionName)
                .query(query)
                .pageSize(2000)
                .sorts(sorts)
                .targetType(FileStorageData.class)
                .template(mongoTemplate)
                .build();
    }


    @Bean
    public ItemProcessor<FileStorageData, AccFileStorage> storageDataAccFileStorageItemProcessor() {
                return (fileStorageData)->{

                String realPath = fileStorageData.getRealPath();
                String[] paths = realPath.split("/");
                FinancialPeriod financialPeriod = FinancialPeriod.of(Short.parseShort(paths[7]), Long.parseLong(paths[5]));

                logger.info("Dönem ->"+financialPeriod);
                try{
                    financialPeriod=financialPeriodRepository.getReferenceById(financialPeriod.getNewId());
                }
                catch (Exception e){
                    financialPeriod=financialPeriodRepository.findById(financialPeriod.getNewId())
                            .orElse(financialPeriod);
                }

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
    public JpaItemWriter<AccFileStorage> fileStorageItemWriter() {

//        JdbcBatchItemWriter<AccFileStorage> writer = new JdbcBatchItemWriter<>();
//        writer.setDataSource(this.dataSource);
//        writer.setSql("INSERT INTO e_belediye.acc_file_storage(id,content_doc_id,type,user_id,financial_period_id ,full_path,file_name,content_type)" +
//                " VALUES (nextval('acc_file_storage_seq'),:contentDocId,:name,:userId,:periodId,:fullPath,:fileName,:contentType )");
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        writer.afterPropertiesSet();
//        return new JdbcFileStorageItemWriter(accFileStorageDataRepository);

        JpaItemWriter<AccFileStorage> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
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
        taskExecutor.setConcurrencyLimit(Math.max(Runtime.getRuntime().availableProcessors(),10));
        return taskExecutor;
    }


}
