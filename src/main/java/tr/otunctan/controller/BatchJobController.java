package tr.otunctan.controller;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tr.otunctan.mongo.entity.FileStorageData;
import tr.otunctan.mongo.repository.FileStorageDataRepository;

@RestController
@RequestMapping("/api/v1/batch")
public class BatchJobController {


    private final JobLauncher jobLauncher;

    private final FileStorageDataRepository fileStorageDataRepository;

    private final Job transferFileMongoJob;


    private final String TEMP_STORAGE = "/home/files/%d/donemler/";

    public BatchJobController(JobLauncher jobLauncher, FileStorageDataRepository fileStorageDataRepository, Job transferFileMongoJob) {
        this.jobLauncher = jobLauncher;
        this.fileStorageDataRepository = fileStorageDataRepository;
        this.transferFileMongoJob = transferFileMongoJob;
    }

    @GetMapping(path = "/start-batch")
    public String startBatch(@RequestParam("municipalityId") Long municipalityId) {


        // file  -> path we don't know
        //copy the file to some storage in your VM : get the file path
        //copy the file to DB : get the file path

        try {
//            String originalFileName = multipartFile.getOriginalFilename();
//            File fileToImport = new File(TEMP_STORAGE + originalFileName);
//            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                   .addString("fullPathFileName", String.format(TEMP_STORAGE, municipalityId))
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();


            JobExecution execution = jobLauncher.run(transferFileMongoJob, jobParameters);

//            if(execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)){
//                //delete the file from the TEMP_STORAGE
//                Files.deleteIfExists(Paths.get(TEMP_STORAGE + originalFileName));
//            }

        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {

            e.printStackTrace();
        }
        return "BU IŞ BURADA BITER.!!!!!!!!!!!!!!!!!!!!!!!!!!";
    }


    @GetMapping(path = "/start-batch/{municipalityId}/{period}")
    public String startBatchFinancialPeriod(@PathVariable("municipalityId") Long municipalityId,@PathVariable("period") Long period) {


        // file  -> path we don't know
        //copy the file to some storage in your VM : get the file path
        //copy the file to DB : get the file path

        try {
//            String originalFileName = multipartFile.getOriginalFilename();
//            File fileToImport = new File(TEMP_STORAGE + originalFileName);
//            multipartFile.transferTo(fileToImport);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", String.format("/ebelediye/home/eBelediye/files/%d/donemler/%d", municipalityId,period))
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();


            JobExecution execution = jobLauncher.run(transferFileMongoJob, jobParameters);

//            if(execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED)){
//                //delete the file from the TEMP_STORAGE
//                Files.deleteIfExists(Paths.get(TEMP_STORAGE + originalFileName));
//            }

        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {

            e.printStackTrace();
        }
        return "BU IŞ BURADA BITER.!!!!!!!!!!!!!!!!!!!!!!!!!!";
    }




    /*@GetMapping(path = "/start-batch")
    public String startBatch() {


        // file  -> path we don't know
        //copy the file to some storage in your VM : get the file path
        //copy the file to DB : get the file path

        try {

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", "/ebelediye/home/eBelediye/files/")
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(transferFileMongoJob, jobParameters);

        } catch (JobExecutionAlreadyRunningException
                 | JobRestartException
                 | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {

            e.printStackTrace();
        }
        return "BU IŞ BURADA BITER.!!!!!!!!!!!!!!!!!!!!!!!!!!";
    }*/

    @GetMapping("/files/{id}")
    public FileStorageData getAllFilesById(@PathVariable String id) {
        return fileStorageDataRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
