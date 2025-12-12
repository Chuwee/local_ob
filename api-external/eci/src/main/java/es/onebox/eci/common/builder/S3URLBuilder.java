package es.onebox.eci.common.builder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class S3URLBuilder {
    private Object[] pathParts;
    private String fileType;
    private String separator;

    public static S3URLBuilder builder() {
        return new S3URLBuilder();
    }

    public String build() {
        return Arrays.stream(pathParts).map(Object::toString).collect(Collectors.joining(separator))
                + "." + fileType;
    }

    public S3URLBuilder pathParts(Object... pathParts) {
        this.pathParts = pathParts;
        return this;
    }

    public S3URLBuilder fileType(String typeFile) {
        this.fileType = typeFile;
        return this;
    }

    public S3URLBuilder separator(String separator) {
        this.separator = separator;
        return this;
    }
}
