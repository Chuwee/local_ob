package es.onebox.common.config;

import com.oneboxtds.datasource.s3.S3Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("onebox.s3.bucket-tickets")
public class S3TicketsProperties extends S3Properties {
}
