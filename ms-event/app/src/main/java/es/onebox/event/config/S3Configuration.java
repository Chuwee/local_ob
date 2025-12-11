package es.onebox.event.config;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    @Bean
    @Qualifier("s3OneboxRepository")
    public S3BinaryRepository s3OneboxRepository(S3RepositoryProperties s3RepositoryProperties) {
        return new S3BinaryRepository(s3RepositoryProperties.getBucketName(), true, s3RepositoryProperties);
    }

    @Bean
    @Qualifier("s3ExportRepository")
    public S3BinaryRepository s3ExportRepository(S3ExportProperties s3ExportProperties) {
        return new S3BinaryRepository(s3ExportProperties.getBucketName(), false, s3ExportProperties);
    }

}
