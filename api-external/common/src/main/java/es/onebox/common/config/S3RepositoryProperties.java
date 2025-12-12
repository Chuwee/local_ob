package es.onebox.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("onebox.s3.bucket-repository")
public class S3RepositoryProperties extends com.oneboxtds.datasource.s3.S3Properties {
}
