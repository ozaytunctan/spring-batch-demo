package tr.gov.icisleri.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class InsertionJobConfiguration {

//    @Bean
//    public MongoItemReader<Person> mongoItemReader(MongoTemplate mongoTemplate) {
//        Map<String, Sort.Direction> sortOptions = new HashMap<>();
//        sortOptions.put("name", Sort.Direction.DESC);
//        return new MongoItemReaderBuilder<Person>().name("personItemReader")
//                .collection("person_in")
//                .targetType(Person.class)
//                .template(mongoTemplate)
//                .jsonQuery("{}")
//                .sorts(sortOptions)
//                .build();
//    }
//
//    @Bean
//    public MongoItemWriter<Person> mongoItemWriter(MongoTemplate mongoTemplate) {
//        return new MongoItemWriterBuilder<Person>().template(mongoTemplate).collection("person_out").build();
//    }
//
//    @Bean
//    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
//                     MongoItemReader<Person> mongoItemReader, MongoItemWriter<Person> mongoItemWriter) {
//        return new StepBuilder("step", jobRepository).<Person, Person>chunk(2, transactionManager)
//                .reader(mongoItemReader)
//                .writer(mongoItemWriter)
//                .build();
//    }
//
//    @Bean
//    public Job insertionJob(JobRepository jobRepository, Step step) {
//        return new JobBuilder("insertionJob", jobRepository).start(step).build();
//    }

}