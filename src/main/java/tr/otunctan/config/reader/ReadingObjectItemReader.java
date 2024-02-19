package tr.otunctan.config.reader;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import tr.otunctan.mongo.entity.FileStorageData;

import java.util.List;

public class ReadingObjectItemReader implements ItemReader<FileStorageData> {
    int i = 0;
    private List<FileStorageData> items;
    @Override
    public FileStorageData read() {
        if (i >= items.size()) {
            return null;
        } else {
            return items.get(i++);
        }
    }
    // Get the details from cache
    @SuppressWarnings("unchecked")
    @BeforeStep
    public void retrieveSharedData(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        items = (List<FileStorageData>) jobContext.get("items");
    }
}