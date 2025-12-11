package es.onebox.event.communicationelements.utils;

import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.TableField;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

public class CommunicationElementsUtils {

    public static final String IMG_FILENAME_PATTERN = "{0}_image_{1,number,#}{2}.{3}";
    public static final String IMG_200DPI_SUFFIX = "_200";

    private static final SecureRandom RANDOM = new SecureRandom();

    private CommunicationElementsUtils() {
        throw new UnsupportedOperationException();
    }

    public static TableField<CpanelEventoRecord, Integer> checkJoinField(TicketCommunicationElementCategory type) {
        return switch (type) {
            case PDF -> Tables.CPANEL_EVENTO.ELEMENTOCOMTICKET;
            case TICKET_OFFICE -> Tables.CPANEL_EVENTO.ELEMENTOCOMTICKETTAQUILLA;
            case INVITATION_PDF -> Tables.CPANEL_EVENTO.ELEMENTOCOMTICKETINVITACION;
            case INVITATION_TICKET_OFFICE -> Tables.CPANEL_EVENTO.ELEMENTOCOMTICKETTAQUILLAINVITACION;
            case PASSBOOK -> Tables.CPANEL_EVENTO.ELEMENTOSCOMPASSBOOK;
            default -> null;
        };
    }

    public static TableField<CpanelSesionRecord, Integer> checkSessionJoinField(TicketCommunicationElementCategory type) {
        return switch (type) {
            case PDF, INVITATION_PDF -> Tables.CPANEL_SESION.ELEMENTOCOMTICKET;
            case TICKET_OFFICE, INVITATION_TICKET_OFFICE -> Tables.CPANEL_SESION.ELEMENTOCOMTICKETTAQUILLA;
            default -> null;
        };
    }

    public static <E extends Enum<E>> CpanelDescPorIdiomaRecord checkAndGetElement(es.onebox.event.communicationelements.dto.CommunicationElementDTO<E> element, Map<E, List<CpanelDescPorIdiomaRecord>> records, Integer languageId) {
        if (records.containsKey(element.getTag())) {
            return records.get(element.getTag()).stream()
                    .filter(desc -> desc.getIdidioma().equals(languageId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public static boolean isValueNotInformed(es.onebox.event.communicationelements.dto.CommunicationElementDTO<?> el) {
        return StringUtils.isBlank(el.getImageBinary()) && el.getValue() == null;
    }

    public static String uploadItemImage(S3BinaryRepository s3BinaryRepository, String previousFile, Integer languageId, Integer idItem,
                                         String fileValue, Integer operatorId, ImageFormat format) {
        return uploadItemImage(s3BinaryRepository, previousFile, languageId, idItem, fileValue, operatorId,
                getImgFilename(languageId, System.currentTimeMillis(), false, format));
    }

    public static String uploadItemImage(S3BinaryRepository s3BinaryRepository, String previousFile, Integer languageId, Integer idItem,
                                         String fileValue, Integer operatorId, String filename) {
        // Remove image only when we are modifying and image or removing it
        if (previousFile != null) {
            String path = getEmailComElementPath(languageId, idItem, operatorId, previousFile);
            s3BinaryRepository.delete(path);
        }
        // Add image only when client send an image
        String path = getEmailComElementPath(languageId, idItem, operatorId, filename);
        s3BinaryRepository.upload(path, Base64.getDecoder().decode(fileValue));
        updatePermissions(s3BinaryRepository, path);
        return filename;
    }

    public static String uploadProductImage(S3BinaryRepository s3BinaryRepository, String previousFile, Integer languageId, Integer productId,
                                            String fileValue, Integer operatorId, Integer entityId, String fileName) {
        // Remove image only when we are modifying and image or removing it
        if (previousFile != null && !previousFile.isEmpty()) {
            String path = getProductComElementPath(languageId, productId, operatorId, entityId, previousFile);
            s3BinaryRepository.delete(path);
        }
        // Add image only when client send an image
        String path = getProductComElementPath(languageId, productId, operatorId, entityId, fileName);
        s3BinaryRepository.upload(path, Base64.getDecoder().decode(fileValue));
        updatePermissions(s3BinaryRepository, path);
        return path;
    }

    public static String uploadPassbookImage(Integer operatorId, Integer entityId, Integer eventId, Integer sessionId, String langCode,
                                             String fileType, String fileValue, S3BinaryRepository s3BinaryRepository) {
        String filename = String.format("%s_image_%s_%s.jpg", langCode, fileType, System.currentTimeMillis());
        // Add image only when client send an image
        String path = getPassbookComElementPath(operatorId, entityId, eventId, sessionId, filename);
        s3BinaryRepository.upload(path, Base64.getDecoder().decode(fileValue));
        updatePermissions(s3BinaryRepository, path);
        return path;
    }

    public static String uploadProductTicketImage(Integer operatorId, Integer entityId, Long productId, String langCode,
                                                  String fileType, String fileValue, S3BinaryRepository s3BinaryRepository
    ) {
        String filename = String.format("%s_image_%s_%s.jpg", langCode, fileType, System.currentTimeMillis());
        String path = getProductTicketImagePath(operatorId, entityId, productId, filename);

        s3BinaryRepository.upload(path, Base64.getDecoder().decode(fileValue));

        Map<Grantee, Permission> permissions = new HashMap<>();
        permissions.put(GroupGrantee.AllUsers, Permission.Read);
        s3BinaryRepository.addPermissions(path, permissions);

        return path;
    }

    public static void deleteProductImage(S3BinaryRepository s3BinaryRepository, Long languageId, Long productId, Long operatorId, Long entityId, String fileName) {
        String filePath = getProductComElementPath(languageId.intValue(), productId.intValue(), operatorId, entityId, fileName);
        s3BinaryRepository.delete(filePath);
    }

    public static void deletePassbookImage(S3BinaryRepository s3BinaryRepository, String filePath) {
        s3BinaryRepository.delete(filePath);
    }

    public static void deleteItemImage(S3BinaryRepository s3BinaryRepository, Integer languageId, Integer idItem, String filename, Integer operatorId) {
        String path = getEmailComElementPath(languageId, idItem, operatorId, filename);
        s3BinaryRepository.delete(path);
    }

    public static void deleteProductTicketImage(S3BinaryRepository s3BinaryRepository, String path) {
        s3BinaryRepository.delete(path);
    }

    public static String uploadImage(S3BinaryRepository s3BinaryRepository, CpanelDescPorIdiomaRecord comElementRecord,
                                     Optional<String> imageBinary, String elementValue, Integer defaultLanguageId,
                                     S3URLResolver.S3ImageType type, Integer operatorId, Integer entityId,
                                     String oldFilename, String newFilename) {
        if (imageBinary != null) {
            // Remove image only when we are modifying and image or removing it
            if (StringUtils.isNotBlank(oldFilename)) {
                String path = getTicketComElementPath(type, comElementRecord, defaultLanguageId, operatorId, entityId, oldFilename);
                s3BinaryRepository.delete(path);
            }
            // Add image only when client send an image
            if (imageBinary.isPresent()) {
                String path = getTicketComElementPath(type, comElementRecord, defaultLanguageId, operatorId, entityId, newFilename);
                s3BinaryRepository.upload(path, Base64.getDecoder().decode(imageBinary.get()));
                updatePermissions(s3BinaryRepository, path);
                return newFilename;
            }
        } else {
            // Clone image when source path is in Communication Element
            if (oldFilename.isEmpty() && !elementValue.isEmpty()) {
                try {
                    String sourcePath = getSourceImageS3Path(elementValue, type, defaultLanguageId, operatorId, entityId);
                    byte[] sourceImage = s3BinaryRepository.download(sourcePath);
                    String path = getTicketComElementPath(type, comElementRecord, defaultLanguageId, operatorId, entityId, newFilename);
                    s3BinaryRepository.upload(path, sourceImage);
                    updatePermissions(s3BinaryRepository, path);
                    return newFilename;
                } catch (Exception e) {
                    throw new OneboxRestException(MsEventErrorCode.NO_OBJECT_UNDER_S3_PATH);
                }
            }
            return oldFilename;
        }
        return null;
    }

    public static String get200dpiImgPath(String path) {
        return FilenameUtils.getBaseName(path) + CommunicationElementsUtils.IMG_200DPI_SUFFIX + "." + FilenameUtils.getExtension(path);
    }

    @NotNull
    public static String getImgFilename(Integer languageId, long timestamp, boolean is200dpi, ImageFormat format) {
        return MessageFormat.format(IMG_FILENAME_PATTERN, languageId, timestamp, is200dpi ? IMG_200DPI_SUFFIX : "", format.getName());
    }

    private static void updatePermissions(S3BinaryRepository s3BinaryRepository, String path) {
        Map<Grantee, Permission> permissions = new HashMap<>();
        permissions.put(GroupGrantee.AllUsers, Permission.Read);
        s3BinaryRepository.addPermissions(path, permissions);
    }

    private static String getTicketComElementPath(S3URLResolver.S3ImageType type, CpanelDescPorIdiomaRecord descRecord,
                                                  Integer defaultLanguageId,
                                                  Number operatorId, Number entityId, String filename) {
        return S3URLResolver.builder().withType(type).withOperatorId(operatorId).withEntityId(entityId)
                .withLanguageId(defaultLanguageId).withItemId(descRecord.getIditem()).build()
                .buildRelativePath(filename);
    }

    private static String getEmailComElementPath(Integer languageId, Integer idItem, Number operatorId, String filename) {
        return S3URLResolver.builder().withType(S3URLResolver.S3ImageType.EVENT_TICKET_IMAGE)
                .withOperatorId(operatorId)
                .withLanguageId(languageId)
                .withItemId(idItem)
                .build()
                .buildRelativePath(filename);
    }

    private static String getProductComElementPath(Integer languageId, Integer productId, Number operatorId, Number entityId, String filename) {
        return S3URLResolver.builder().withType(S3URLResolver.S3ImageType.PRODUCT_IMAGE)
                .withOperatorId(operatorId)
                .withEntityId(entityId)
                .withLanguageId(languageId)
                .withProductId(productId)
                .build()
                .buildRelativePath(filename);
    }

    private static String getPassbookComElementPath(Number operatorId, Number entityId, Number eventId, Number sessionId, String filename) {
        S3URLResolver.S3ImageType type = isNull(sessionId) ? S3URLResolver.S3ImageType.EVENT_PASSBOOK_IMAGE
                : S3URLResolver.S3ImageType.SESSION_PASSBOOK_IMAGE;
        return S3URLResolver.builder().withType(type)
                .withOperatorId(operatorId)
                .withEntityId(entityId)
                .withEventId(eventId)
                .withSessionId(sessionId)
                .build()
                .buildRelativePath(filename);
    }

    private static String getProductTicketImagePath(Number operatorId, Number entityId, Number productId, String filename) {
        S3URLResolver.S3ImageType type = S3URLResolver.S3ImageType.PRODUCT_TICKET_IMAGE;
        return S3URLResolver.builder().withType(type)
                .withOperatorId(operatorId)
                .withEntityId(entityId)
                .withProductId(productId)
                .build()
                .buildRelativePath(filename);
    }

    private static String getSourceImageS3Path(String path, S3URLResolver.S3ImageType type, Integer defaultLanguageId,
                                               Number operatorId, Number entityId) {
        String[] splitPath = path.split("/");
        return S3URLResolver.builder().withType(type).withOperatorId(operatorId).withEntityId(entityId)
                .withLanguageId(defaultLanguageId).withItemId(Integer.parseInt(splitPath[splitPath.length - 2])).build()
                .buildRelativePath(splitPath[splitPath.length - 1]);
    }

    public static String uploadImage(S3BinaryRepository s3OneboxRepository, CpanelElementosComEventoCanalRecord record,
                                     EventCommunicationElementDTO element, Long operatorId, Boolean snapbox) {
        if (element.getImageBinary() != null) {
            // Remove image only when we are modifying an image or removing it
            if (record.getValor() != null) {
                String path = getChannelEventImagePath(record.getIdelemento(), record.getValor(), operatorId);
                s3OneboxRepository.delete(path);
            }
            // Add image only when client send an image
            if (element.getImageBinary().isPresent()) {
                String filename = record.getIdelemento() + "_" + RANDOM.nextLong(1000000) + "_" + System.currentTimeMillis() + ".jpg";
                String path = getChannelEventImagePath(record.getIdelemento(), filename, operatorId);
                s3OneboxRepository.upload(path, Base64.getDecoder().decode(element.getImageBinary().get()));

                //Skip permissions update for snapbox. Already not implemented by minio.
                if (!snapbox) {
                    Map<Grantee, Permission> permissions = new HashMap<>();
                    permissions.put(GroupGrantee.AllUsers, Permission.Read);
                    s3OneboxRepository.addPermissions(path, permissions);
                }
                return filename;
            }
        } else {
            return record.getValor();
        }
        return null;
    }

    private static String getChannelEventImagePath(Integer idItem, String filename, Number operatorId) {
        return S3URLResolver.builder()
                .withType(S3URLResolver.S3ImageType.ITEM_IMAGE)
                .withOperatorId(operatorId)
                .withItemId(idItem)
                .build()
                .buildRelativePath(filename);
    }
}
