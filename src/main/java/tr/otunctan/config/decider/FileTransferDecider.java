package tr.otunctan.config.decider;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class FileTransferDecider implements JobExecutionDecider {


    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // Based on timezone you're in, you need to tweak conditions to get different results
        ExitStatus exitStatus = stepExecution.getExitStatus();
        return new FlowExecutionStatus(exitStatus.getExitCode().equals("COMPLETED") ?"ISLEM_TAMAMLANDI":"ISLEM_TAMAMLANAMADI");
    }
}