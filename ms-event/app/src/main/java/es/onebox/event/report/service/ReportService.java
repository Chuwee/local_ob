package es.onebox.event.report.service;

import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
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
import es.onebox.event.mailing.MailTemplateCouchDao;
import es.onebox.event.mailing.MailingService;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.exception.MsEventErrorCode;
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

    private static final String FILE_SEPARATOR = "/";
    private static final String FILE_NAME_SEPARATOR = "_";
    private static final String BASE_PATH = "export";
    private static final String EMAIL_TEMPLATE = "report";
    private static final String FAILED_EMAIL_TEMPLATE = "failed_report";


    private final S3BinaryRepository s3ExportRepository;
    private final ExportStatusService exportStatusService;
    private final MailingService mailingService;
    private final MailTemplateCouchDao mailTemplateCouchDao;
    private final TemplateResolver templateResolver;

    @Autowired
    public ReportService(@Qualifier("s3ExportRepository") S3BinaryRepository s3ExportRepository, List<FileExporter> exporters, List<ExportProvider<?, ?>> providers,
                         ExportStatusService exportProcessStatusService, MailingService mailingService,
                         MailTemplateCouchDao mailTemplateCouchDao, TemplateResolver templateResolver) {
        super(exporters, providers);
        this.s3ExportRepository = s3ExportRepository;
        this.mailingService = mailingService;
        this.exportStatusService = exportProcessStatusService;
        this.mailTemplateCouchDao = mailTemplateCouchDao;
        this.templateResolver = templateResolver;
    }

    @Override
    protected <T extends ExportFilter<?, ?, ?>> void onError(Exception e, T filter) {
        log.error("Report generation failed", e);
        exportStatusService.fail(filter.getUserId(), filter.getExportId(), filter.getType().getName(), e.getMessage());
        sendErrorMail(filter, (MsEventReportType) filter.getType());
    }

    @Override
    protected <T extends ExportFilter<?, ?, ?>> void generationHandler(T filter, File file) {
        MsEventReportType type = (MsEventReportType) filter.getType();
        String reportTypeName = filter.getType().getName();
        String s3Url = toS3(file, filter.getExportId(), filter.getFormat(), reportTypeName);
        exportStatusService.success(filter.getUserId(), filter.getExportId(), reportTypeName, s3Url);
        if (filter instanceof ExportWithEmailAndTimeZoneFilter) {
            sendMail(filter, type, s3Url);
        }
    }

    private <T extends ExportFilter<?, ?, ?>> void sendMail(T filter, MsEventReportType type, String s3Url) {
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

    private <T extends ExportFilter<?, ?, ?>> void sendErrorMail(T filter,  MsEventReportType type) {
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

    public String uploadReport(final InputStream stream, final String txId, final FileFormat format, final String reportTypeName) {
        final ObjectPolicy policy = createReportPolicy();
        final String s3Path = buildPath(txId, format, reportTypeName);
        s3ExportRepository.upload(s3Path, stream, Boolean.TRUE, policy);
        if (!s3ExportRepository.existObject(s3Path)) {
            throw ExceptionBuilder.build(MsEventErrorCode.S3_FILE_NOT_UPLOADED, "File check not successful");
        }
        return s3ExportRepository.getPublicSignedUrl(s3Path, policy);
    }

    private String toS3(final File csv, final String exportId, final FileFormat fileFormat, final String reportTypeName) {
        try (InputStream stream = FileUtils.openInputStream(csv)) {
            return this.uploadReport(stream, exportId, fileFormat, reportTypeName);
        } catch (IOException e) {
            log.error("Error uploading file to s3", e);
            throw OneboxRestException.builder(MsEventErrorCode.EXPORT_UPLOAD_EXCEPTION).build();
        }
    }

    private static String buildPath(final String txId, final FileFormat format, final String reportTypeName) {
        final ZonedDateTime date = DateUtils.now();
        final String formattedDay = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        final String type = reportType(reportTypeName);
        final String fileName = fileName(type, formattedDay, txId, format);
        return resolveS3Path(type, date, fileName);
    }

    private static String resolveS3Path(final String reportType, final ZonedDateTime date, final String fileName) {
        return reportType + FILE_SEPARATOR
                + date.getYear() + FILE_SEPARATOR
                + date.getMonthValue() + FILE_SEPARATOR
                + date.getDayOfMonth() + FILE_SEPARATOR
                + fileName;
    }

    private static String fileName(final String reportType, final String formattedDay, final String fileName, final FileFormat format) {
        return BASE_PATH + FILE_NAME_SEPARATOR + reportType.toLowerCase() + FILE_NAME_SEPARATOR + formattedDay + FILE_NAME_SEPARATOR
                + fileName + format.getExtension();
    }

    private static ObjectPolicy createReportPolicy() {
        final Date expirationTime = Date.from(DateUtils.now().plusDays(1L).toInstant());
        return ObjectPolicy.builder().expiration(expirationTime).contentEncoding(ObjectPolicy.ContentEncoding.GZIP)
                .build();
    }

    private static String reportType(final String reportTypeName) {
        return switch (reportTypeName) {
            case "SEASON_TICKETS_RENEWALS" -> "season-tickets-renewals";
            case "PRICE_SIMULATION" -> "price-simulation";
            default -> StringUtils.EMPTY;
        };
    }

}
