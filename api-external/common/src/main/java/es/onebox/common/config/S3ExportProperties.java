package es.onebox.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("onebox.s3.bucket-export")
public class S3ExportProperties extends com.oneboxtds.datasource.s3.S3Properties {
}
