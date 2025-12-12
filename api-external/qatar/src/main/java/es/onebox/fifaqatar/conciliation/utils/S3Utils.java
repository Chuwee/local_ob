package es.onebox.fifaqatar.conciliation.utils;

import es.onebox.core.file.exporter.generator.export.FileFormat;
import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class S3Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Utils.class);
    private static final String BUCKET_OBJECT_PATH_SEPARATOR = "/";
    private static final String BUCKET_OBJECT_NAME_SEPARATOR = "_";

    private final S3BinaryRepository s3ExportRepository;

    public S3Utils(S3BinaryRepository s3ExportRepository) {
        this.s3ExportRepository = s3ExportRepository;
    }



    public String toS3(final File csv, final String exportId, final FileFormat fileFormat, final String reportTypeName) {
        try (InputStream stream = FileUtils.openInputStream(csv)) {
            return uploadReport(stream, exportId, fileFormat, reportTypeName);
        } catch (IOException e) {
            LOGGER.error("Error uploading file to s3", e);
            throw OneboxRestException.builder(ApiExternalErrorCode.EXPORT_UPLOAD_EXCEPTION).build();
        }
    }

    private String uploadReport(final InputStream stream, final String txId, final FileFormat format, final String reportTypeName) {
        final ObjectPolicy policy = createReportPolicy();
        final String s3Path = buildPath(txId, format, reportTypeName);
        s3ExportRepository.upload(s3Path, stream, Boolean.TRUE, policy);
        if (!s3ExportRepository.existObject(s3Path)) {
            throw ExceptionBuilder.build(CoreErrorCode.GENERIC_ERROR, "File check not successful");
        }
        return s3ExportRepository.getPublicSignedUrl(s3Path, policy);
    }

    private static String buildPath(final String txId, final FileFormat format, final String reportTypeName) {
        final ZonedDateTime date = DateUtils.now();
        final String formattedDay = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        final String fileName = fileName(reportTypeName, formattedDay, txId, format);
        return resolveS3Path(reportTypeName, date, fileName);
    }

    private static String resolveS3Path(final String reportType, final ZonedDateTime date, final String fileName) {
        return reportType
                + BUCKET_OBJECT_PATH_SEPARATOR + date.getYear()
                + BUCKET_OBJECT_PATH_SEPARATOR + date.getMonthValue()
                + BUCKET_OBJECT_PATH_SEPARATOR + date.getDayOfMonth()
                + BUCKET_OBJECT_PATH_SEPARATOR + fileName;
    }

    private static String fileName(final String reportType, final String formattedDay, final String fileName, final FileFormat format) {
        return reportType + BUCKET_OBJECT_NAME_SEPARATOR + formattedDay + BUCKET_OBJECT_NAME_SEPARATOR + fileName + format.getExtension();
    }

    private static ObjectPolicy createReportPolicy() {
        final Date expirationTime = Date.from(DateUtils.now().plusDays(1L).toInstant());
        return ObjectPolicy.builder().expiration(expirationTime).contentEncoding(ObjectPolicy.ContentEncoding.GZIP)
                .build();
    }

}
