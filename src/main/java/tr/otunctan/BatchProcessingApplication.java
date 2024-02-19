package tr.otunctan;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class BatchProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);

//		List<File> files = FileHelper.findFiles(new File("/ebelediye/home/eBelediye/files/"));
//
//		String[] paths = new String("/ebelediye/home/eBelediye/files/4/donemler/2023/yevmiyeler/54/ekler")
//				.split("/");
	}

}