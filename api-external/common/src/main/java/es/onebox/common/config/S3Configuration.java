package es.onebox.common.config;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    @Value("${onebox.s3.bucket-repository.bucketName}")
    private String repositoryBucketName;

    @Value("${onebox.s3.bucket-tickets.bucketName}")
    private String ticketBucketName;

    @Value("${onebox.s3.bucket-tickets-external.bucketName}")
    private String ticketExternalBucketName;

    @Value("${onebox.s3.bucket-automatic-sales.bucketName}")
    private String automaticSalesBucketName;

    @Value("${onebox.s3.bucket-export.bucketName}")
    private String exportBucketName;

    @Bean(name = "s3TicketsRepository")
    public S3BinaryRepository s3TicketsRepository(S3TicketsProperties s3TicketsProperties) {
        return new S3BinaryRepository(ticketBucketName, true, s3TicketsProperties);
    }

    @Bean(name = "s3OneboxRepository")
    public S3BinaryRepository s3OneboxRepository(S3RepositoryProperties s3RepositoryProperties) {
        return new S3BinaryRepository(repositoryBucketName, true, s3RepositoryProperties);
    }

    @Bean(name = "s3TicketsExternalRepository")
    public S3BinaryRepository s3TicketsExternalRepository(S3TicketsExternalProperties s3TicketsExternalProperties) {
        return new S3BinaryRepository(ticketExternalBucketName, false, s3TicketsExternalProperties);
    }

    @Bean(name = "s3AutomaticSalesRepository")
    public S3BinaryRepository s3AutomaticSalesRepository(S3AutomaticSalesProperties s3AutomaticSalesProperties) {
        return new S3BinaryRepository(automaticSalesBucketName, false, s3AutomaticSalesProperties);
    }

    @Bean(name="s3ExportRepository")
    public S3BinaryRepository s3ExportRepository(S3ExportProperties s3ExportProperties) {
        return new S3BinaryRepository(exportBucketName, false, s3ExportProperties);
    }
}
