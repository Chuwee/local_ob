package es.onebox.internal.automaticsales.report;

import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.internal.automaticsales.mailing.MailTemplateCouchDao;
import es.onebox.internal.automaticsales.mailing.MailingService;
import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.generator.export.FileExporter;
import es.onebox.core.file.exporter.generator.export.FileFormat;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import es.onebox.core.file.exporter.generator.request.ExportFilter;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailFilter;
import es.onebox.core.file.exporter.generator.service.ExportService;
import es.onebox.core.file.exporter.status.service.ExportStatusService;
import es.onebox.core.mail.template.manager.TemplateResolver;
import es.onebox.core.mail.template.manager.model.TemplateContentDTO;
import es.onebox.core.mail.template.manager.model.TemplateScope;
import es.onebox.core.utils.common.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Lazy
@Service
public class ReportService extends ExportService {

    private static final String BUCKET_OBJECT_PATH_SEPARATOR = "/";
    private static final String BUCKET_OBJECT_NAME_SEPARATOR = "_";
    private static final String EMAIL_TEMPLATE = "report";
    private static final String FAILED_EMAIL_TEMPLATE = "failed_report";

    private final S3BinaryRepository s3ExportRepository;
    private final ExportStatusService exportStatusService;
    private final MailTemplateCouchDao mailTemplateCouchDao;
    private final TemplateResolver templateResolver;
    private final MailingService mailingService;

    @Autowired
    public ReportService(List<FileExporter> exporters, List<ExportProvider<?, ?>> providers,
                         @Qualifier("s3ExportRepository")
                         S3BinaryRepository s3ExportRepository,
                         ExportStatusService exportProcessStatusService,
                         MailTemplateCouchDao mailTemplateCouchDao,
                         TemplateResolver templateResolver,
                         MailingService mailingService) {
        super(exporters, providers);
        this.s3ExportRepository = s3ExportRepository;
        this.exportStatusService = exportProcessStatusService;
        this.mailTemplateCouchDao = mailTemplateCouchDao;
        this.templateResolver = templateResolver;
        this.mailingService = mailingService;
    }

    @Override
    protected <T extends ExportFilter<?, ?, ?>> void onError(Exception e, T filter) {
        log.error("Report generation failed", e);
        ApiExternalExportType type = (ApiExternalExportType) filter.getType();
        exportStatusService.fail(filter.getUserId(), filter.getExportId(), filter.getType().getName(), e.getMessage());
        sendErrorMail(filter, type);
    }

    @Override
    protected <T extends ExportFilter<?, ?, ?>> void generationHandler(T filter, File file) {
        ApiExternalExportType type = (ApiExternalExportType) filter.getType();
        String reportTypeName = filter.getType().getName();
        String s3Url = toS3(file, filter.getExportId(), filter.getFormat(), reportTypeName);
        exportStatusService.success(filter.getUserId(), filter.getExportId(), reportTypeName, s3Url);
        if (filter instanceof ExportWithEmailAndTimeZoneFilter<?, ?> mailFilter) {
            sendMail(mailFilter, type, s3Url);
        }
    }

    private <T extends ExportFilter<?, ?, ?>> void sendMail(T filter, ApiExternalExportType type, String s3Url) {
        ExportWithEmailFilter<?, ?> mailFilter = (ExportWithEmailFilter<?, ?>) filter;
        TemplateContentDTO template = mailTemplateCouchDao.getTemplate(type.getMailTemplateKey(), mailFilter.getLanguage());
        String body = this.templateResolver
                .context()
                .of(TemplateScope.CPANEL, EMAIL_TEMPLATE)
                .withDocument(template)
                .withParams(Map.of("download_link", s3Url))
                .build();
        mailingService.sendReport(mailFilter.getEmail(), body, template.getSubject());
    }

    private <T extends ExportFilter<?, ?, ?>> void sendErrorMail(T filter, ApiExternalExportType type) {
        ExportWithEmailFilter<?, ?> mailFilter = (ExportWithEmailFilter<?, ?>) filter;
        TemplateContentDTO template = mailTemplateCouchDao.getFailedTemplate(type.getMailTemplateKey(), mailFilter.getLanguage());
        String body = this.templateResolver
                .context()
                .of(TemplateScope.CPANEL, FAILED_EMAIL_TEMPLATE)
                .withDocument(template)
                .withParams(Map.of("download_link", "none"))
                .build();
        mailingService.sendReport(mailFilter.getEmail(), body, template.getSubject());
    }

    private String toS3(final File csv, final String exportId, final FileFormat fileFormat, final String reportTypeName) {
        try (InputStream stream = FileUtils.openInputStream(csv)) {
            return uploadReport(stream, exportId, fileFormat, reportTypeName);
        } catch (IOException e) {
            log.error("Error uploading file to s3", e);
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
        final String type = reportType(reportTypeName);
        final String fileName = fileName(type, formattedDay, txId, format);
        return resolveS3Path(type, date, fileName);
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

    private static String reportType(final String reportTypeName) {
        if("AUTOMATIC_SALES".equals(reportTypeName)) {
            return "automatic-sales";
        } else {
            return StringUtils.EMPTY;
        }
    }
}
