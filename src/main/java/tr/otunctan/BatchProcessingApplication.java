package tr.otunctan;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableBatchProcessing
@EnableTransactionManagement
public class BatchProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingApplication.class, args);

//List<File> files = FileHelper.findFiles(new File("/ebelediye/home/eBelediye/files/"));
//
//files.forEach(item->{
//	System.out.println(FileUtils.sizeInMegaBytesString(item.length()));
//	System.out.println(FileUtils.sizeInKiloBytesString(item.length()));
//});
//
//		String[] paths = new String("/ebelediye/home/eBelediye/files/4/donemler/2023/yevmiyeler/54/ekler")
//				.split("/");

//		List<Integer> integers = Arrays.asList(
//				12,
//				1209,
//				1301,
//				1676,
//				1677,
//				180,
//				182,
//				198,
//				199,
//				206,
//				208,
//				212,
//				216,
//				218,
//				221,
//				239,
//				246,
//				252,
//				268,
//				283,
//				294,
//				295,
//				298,
//				302,
//				352,
//				372,
//				4,
//				422,
//				442,
//				533,
//				542,
//				549,
//				554,
//				591,
//				62,
//				643,
//				66,
//				71,
//				879,
//				107,
//				111,
//				1113,
//				1158,
//				1301,
//				1676,
//				1677,
//				180,
//				182,
//				198,
//				199,
//				206,
//				208,
//				212,
//				216,
//				218,
//				221
//		);
//

//		System.out.println(new HashSet<>(integers));
	}

}
