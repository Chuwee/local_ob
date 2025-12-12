package es.onebox.common.config;

import com.oneboxtds.datasource.s3.S3Properties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("onebox.s3.bucket-automatic-sales")
public class S3AutomaticSalesProperties extends S3Properties {
}
