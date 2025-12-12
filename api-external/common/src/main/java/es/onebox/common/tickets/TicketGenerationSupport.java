package es.onebox.common.tickets;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import com.oneboxtds.datasource.s3.builder.PresignedUrlBuilder;
import com.oneboxtds.datasource.s3.enums.ContentDisposition;
import com.oneboxtds.datasource.s3.enums.ContentType;
import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.annotation.SkippedCachedArg;
import es.onebox.common.config.HazelcastConfiguration;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.url.S3URLBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.manager.TypeFile;
import es.onebox.hazelcast.core.service.HazelcastMapService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Service
public class TicketGenerationSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketGenerationSupport.class);

    public static final int EXPIRATION_MINUTES = 60;

    private static final String DIR_TICKETS_AD_HOC = "ticketsAdHoc";

    private final HazelcastMapService hazelcastMapService;
    private final S3BinaryRepository s3TicketsRepository;
    private final EntitiesRepository entitiesRepository;
    private final S3BinaryRepository s3OneboxRepository;

    @Value("${onebox.repository.S3Separator}")
    private String s3Separator;
    @Value("${i.am.snapbox:false}")
    private Boolean snapbox;

    @Autowired
    public TicketGenerationSupport(HazelcastMapService hazelcastMapService,
                                   @Qualifier("s3TicketsRepository") S3BinaryRepository s3TicketsRepository,
                                   EntitiesRepository entitiesRepository,
                                   @Qualifier("s3OneboxRepository") S3BinaryRepository s3OneboxRepository) {
        this.hazelcastMapService = hazelcastMapService;
        this.s3TicketsRepository = s3TicketsRepository;
        this.entitiesRepository = entitiesRepository;
        this.s3OneboxRepository = s3OneboxRepository;
    }

    public void createExternalPrintGenerationMap(String orderCode) {
        hazelcastMapService.putIntoMap(HazelcastConfiguration.API_EXTERNAL_PRINT_GENERATION_MAP, orderCode, true);
    }

    public boolean processReady(String orderCode) {
        Object value = hazelcastMapService.getObjectFromMap(HazelcastConfiguration.API_EXTERNAL_PRINT_GENERATION_MAP, orderCode);
        return (nonNull(value) && (Boolean) value);
    }

    public void removeSemaphore(String orderCode) {
        hazelcastMapService.putIntoMap(HazelcastConfiguration.API_EXTERNAL_PRINT_GENERATION_MAP, orderCode, false);
        hazelcastMapService.removeFromMap(HazelcastConfiguration.API_EXTERNAL_PRINT_GENERATION_MAP, orderCode);
    }

    public void isNotSnapBox(String path) {
        if (!snapbox) {
            Map<Grantee, Permission> permissions = new HashMap<>();
            permissions.put(GroupGrantee.AllUsers, Permission.Read);
            s3TicketsRepository.addPermissions(path, permissions);
        }
    }

    public void joinTicketsPDF(OrderDTO order, List<OrderProductDTO> itemsList, List<JasperPrint> itemsPdf) {
        String mergePath = getPathForMergePDF(order);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (!CollectionUtils.isEmpty(itemsPdf)) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setExporterInput(SimpleExporterInput.getInstance(itemsPdf));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                exporter.exportReport();
            } else {
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, outputStream);
                document.open();
                for (OrderProductDTO item : itemsList) {
                    String path = getPathForTicketPDF(order.getCode(), item);
                    byte[] ticketPDF = s3TicketsRepository.download(path);
                    copy.addDocument(new PdfReader(ticketPDF));
                }
                document.close();
                copy.close();
            }

            s3TicketsRepository.upload(mergePath, outputStream.toByteArray());
            isNotSnapBox(mergePath);
        } catch (JRException | IOException | DocumentException e) {
            removeSemaphore(order.getCode());
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "An error has occurred during the ticket merged process", null);
        }
    }

    public void uploadTicketToS3(String orderCode, JasperPrint pdfData, OrderProductDTO item) {
        String path = getPathForTicketPDF(orderCode, item);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(pdfData));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();

            s3TicketsRepository.upload(path, outputStream.toByteArray());
            isNotSnapBox(path);
        } catch (JRException | IOException e) {
            removeSemaphore(orderCode);
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "An error has occurred during the ticket upload process", null);
        }
    }

    public String getPathForTicketPDF(String orderCode, OrderProductDTO item) {
        Long entityId = item.getEventEntityId().longValue();
        Long operatorId = getOperatorId(entityId);
        Long sessionId = item.getSessionId().longValue();
        String fileName = item.getTicketData().getBarcode();

        return S3URLBuilder.builder()
                .pathParts(operatorId, entityId, sessionId, orderCode, DIR_TICKETS_AD_HOC, fileName)
                .fileType(TypeFile.REPORT_PDF.getRelativePath())
                .separator(s3Separator)
                .build();
    }

    public String getPathForMergePDF(OrderDTO order) {
        Long entityId = order.getOrderData().getChannelEntityId().longValue();
        Long operatorId = getOperatorId(entityId);
        String orderCode = order.getCode();

        return S3URLBuilder.builder()
                .pathParts(operatorId, entityId, "mergedTickets", DIR_TICKETS_AD_HOC, orderCode)
                .fileType(TypeFile.REPORT_PDF.getRelativePath())
                .separator(s3Separator)
                .build();
    }

    public String getMergedTickets(OrderDTO orderDTO, ContentDisposition contentDisposition) {
        String path = getPath(orderDTO);
        if (existFile(path)) {
            return generatePresignedUrl(path, contentDisposition);
        }
        return null;
    }

    public String getSingleTickets(String orderCode, OrderProductDTO item, ContentDisposition contentDisposition) {
        String path = getPathForTicketPDF(orderCode, item);
        if (existFile(path)) {
            return generatePresignedUrl(path, contentDisposition);
        }
        return null;
    }


    public ZonedDateTime getFileCreationDate(OrderDTO orderDTO) {
        String path = getPath(orderDTO);
        if (existFile(path)) {
            Date lastModified = s3TicketsRepository.getObjectMetadata(path).getLastModified();
            return ZonedDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }

    private String getPath(OrderDTO orderDTO) {
        Long entityId = orderDTO.getOrderData().getChannelEntityId().longValue();
        Long operatorId = getOperatorId(entityId);
        return buildMergedTicketUrl(orderDTO.getCode(), operatorId, entityId);
    }

    public String buildMergedTicketUrl(String orderCode, Long operatorId, Long entityId) {
        return S3URLBuilder.builder()
                .pathParts(operatorId, entityId, "mergedTickets", DIR_TICKETS_AD_HOC, orderCode)
                .fileType(TypeFile.REPORT_PDF.getRelativePath())
                .separator(s3Separator)
                .build();
    }

    public boolean existFile(String path) {
        try {
            return s3TicketsRepository.existObject(path);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 403) {
                return false;
            }
            throw e;
        }
    }

    public String generatePresignedUrl(String path, ContentDisposition contentDisposition) {
        try {
            Date expiration = DateUtils.addMinutes(new Date(), EXPIRATION_MINUTES);
            PresignedUrlBuilder b = new PresignedUrlBuilder(path);
            b.withExpirationDate(expiration);
            b.withContentType(ContentType.APPLICATION_PDF);
            if (contentDisposition != null) {
                b.withContentDisposition(contentDisposition);
            }
            return s3TicketsRepository.getPublicSignedUrl(b);
        } catch (SdkClientException e) {
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "An error has occurred while generating Presigned Url", null);
        }
    }

    public Long getOperatorId(Long entityId) {
        EntityDTO entity = entitiesRepository.getByIdCached(entityId);
        if (isNull(entity)) {
            return null;
        }
        if (isNull(entity.getOperator())) {
            return null;
        }
        return entity.getOperator().getId();
    }

    @Cached(key = "load_s3_report_file", expires = 3 * 60)
    public JasperReport loadReportFile(@SkippedCachedArg String orderCode, @CachedArg String fileName) {
        JasperReport report = null;

        String path = S3URLBuilder.builder()
                .pathParts("informes", "modelosJasper", fileName)
                .fileType("jasper")
                .separator(s3Separator)
                .build();
        try {
            report = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(s3OneboxRepository.download(path)));
        } catch (Exception e) {
            LOGGER.info("[{}] Could not load report definition from s3 path: {}", orderCode, path);
        }

        try {
            if (isNull(report)) {
                report = (JasperReport) JRLoader.loadObject(this.getClass().getClassLoader().getResourceAsStream(fileName + ".jasper"));
            }
        } catch (JRException e) {
            LOGGER.info("[{}] Could not load report definition from classpath file: {}", orderCode, fileName);
        }

        if (isNull(report)) {
            LOGGER.error("[{}] Could not load any report definition", orderCode);
            removeSemaphore(orderCode);
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "An error occurred while retrieving Jasper file", null);
        }

        return report;
    }

}
