package tr.otunctan.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import tr.otunctan.entity.AccFileStorage;

public class StepSkipListener implements SkipListener<AccFileStorage, Number> {


    Logger logger = LoggerFactory.getLogger(StepSkipListener.class);

    @Override // item reader
    public void onSkipInRead(Throwable throwable) {
        logger.info("A failure on read {} ", throwable.getMessage());
    }

    @Override // item writter
    public void onSkipInWrite(Number item, Throwable throwable) {
        logger.info("A failure on write {} , {}", throwable.getMessage(), item);
    }

    @Override // item processor
    public void onSkipInProcess(AccFileStorage accFileStorage, Throwable throwable) {
//        logger.info("Item {}  was skipped due to the exception  {}", new ObjectMapper().writeValueAsString(accFileStorage),
//                throwable.getMessage());
    }
}
