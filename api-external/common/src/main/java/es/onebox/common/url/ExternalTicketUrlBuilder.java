package es.onebox.common.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ExternalTicketUrlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalTicketUrlBuilder.class);

    private static final String EXTENSION = ".jpg";
    private static final String S3_SEPARATOR = "/";
    private static final String PROTOCOL = "https://";
    private static final String SEPARATOR = ".";

    private String host;
    private String bucket;
    private ExternalTicketParams params;

    public ExternalTicketUrlBuilder(ExternalTicketParams params) {
        this.params = params;
    }

    public static ExternalTicketUrlBuilder of(ExternalTicketParams params) {
        return new ExternalTicketUrlBuilder(params);
    }

    public ExternalTicketUrlBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public ExternalTicketUrlBuilder withBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();

        if (Objects.nonNull(host) && Objects.nonNull(bucket)) {
            builder.append(PROTOCOL);
            builder.append(bucket);
            builder.append(SEPARATOR);
            builder.append(host);
            builder.append(S3_SEPARATOR);
        }

        LOGGER.info("[{}] External ticket image: {}", params.getOrderId(), builder);
        if (Objects.nonNull(params)) {
            builder.append(params.getEntityId());
            builder.append(S3_SEPARATOR);
            builder.append(params.getSessionId());
            builder.append(S3_SEPARATOR);
            builder.append(params.getOrderId());
            builder.append(S3_SEPARATOR);
            builder.append(params.getProductId());
            builder.append(EXTENSION);
        }
        LOGGER.info("[{}] External ticket image: {}", params.getOrderId(), builder);

        return builder.toString();
    }
}
