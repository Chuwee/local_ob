package es.onebox.event.events.utils;

import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;

public class EventCommunicationElementUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventCommunicationElementUtils.class);

    private static final SecureRandom RANDOM = new SecureRandom();

    private EventCommunicationElementUtils() {
    }

    public static CpanelElementosComEventoRecord checkAndGetElement(EventCommunicationElementDTO element, Integer
            languageId, List<CpanelElementosComEventoRecord> records) {
        return checkAndGetElementsStream(element, languageId, records).findFirst().orElse(null);
    }

    public static List<CpanelElementosComEventoRecord> checkAndGetElements(EventCommunicationElementDTO element, Integer
            languageId, List<CpanelElementosComEventoRecord> records) {
        return checkAndGetElementsStream(element, languageId, records).collect(Collectors.toList());
    }

    private static Stream<CpanelElementosComEventoRecord> checkAndGetElementsStream(EventCommunicationElementDTO element, Integer
            languageId, List<CpanelElementosComEventoRecord> records) {
        if (element.getTagId() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "tag id is mandatory", null);
        }
        if (element.getLanguage() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "language code id is mandatory", null);
        }
        if (languageId == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER_FORMAT, "language code invalid", null);
        }

        return records.stream().
                filter(e -> e.getIdtag().equals(element.getTagId()) &&
                        e.getIdioma().equals(languageId) &&
                        (element.getPosition() == null || e.getPosition().equals(element.getPosition())));
    }

    public static String uploadImage(S3BinaryRepository s3BinaryRepository, CpanelElementosComEventoRecord comElementRecord,
                                     EventCommunicationElementDTO element, S3URLResolver.S3ImageType type,
                                     Integer operatorId, Integer entityId, Long eventId, Long sessionId, Boolean snapbox) {
        if (element.getImageBinary() != null) {
            // Remove image only when we are modifying and image or removing it
            if (comElementRecord.getValor() != null) {
                String path = getComElementPath(type, comElementRecord.getValor(), operatorId, entityId, eventId, sessionId);
                s3BinaryRepository.delete(path);
            }
            // Add image only when client send an image
            if (element.getImageBinary().isPresent()) {
                String extension = detectImageExtension(element.getImageBinary().get());
                String filename = comElementRecord.getIdelemento() + "_" + RANDOM.nextLong(1000000) + "_" + System.currentTimeMillis() + extension;
                String path = getComElementPath(type, filename, operatorId, entityId, eventId, sessionId);

                s3BinaryRepository.upload(path, Base64.getDecoder().decode(element.getImageBinary().get()));

                //Skip permissions update for snapbox. Already not implemented by minio.
                if (!snapbox) {
                    Map<Grantee, Permission> permissions = new HashMap<>();
                    permissions.put(GroupGrantee.AllUsers, Permission.Read);
                    s3BinaryRepository.addPermissions(path, permissions);
                }
                return filename;
            }
        } else {
            return comElementRecord.getValor();
        }
        return null;
    }

    public static String getComElementPath(S3URLResolver.S3ImageType type, String fileName,
                                           Number operatorId, Number entityId, Number mainId, Number secondaryId) {
        S3URLResolver builder = S3URLResolver.builder()
                .withType(type)
                .withOperatorId(operatorId)
                .withEntityId(entityId)
                .withTourId(mainId)
                .withEventId(mainId)
                .withSessionId(secondaryId)
                .build();

        String path = null;
        switch (type) {
            case EVENT_IMAGE:
                path = builder.buildRelativeEventPath(fileName);
                break;
            case SESSION_IMAGE:
                path = builder.buildRelativeSessionPath(fileName);
                break;
            case TOUR_IMAGE:
                path = builder.buildRelativeTourPath(fileName);
                break;
            default:
        }
        return path;
    }

    private static String detectImageExtension(String base64Image) {

        try {
            int prefixLength = Math.min(20, base64Image.length());
            byte[] headerBytes = Base64.getDecoder().decode(base64Image.substring(0, prefixLength));

            // Check for PNG signature: 89 50 4E 47 0D 0A 1A 0A
            if (headerBytes.length >= 8 &&
                headerBytes[0] == (byte) 0x89 &&
                headerBytes[1] == (byte) 0x50 &&
                headerBytes[2] == (byte) 0x4E &&
                headerBytes[3] == (byte) 0x47 &&
                headerBytes[4] == (byte) 0x0D &&
                headerBytes[5] == (byte) 0x0A &&
                headerBytes[6] == (byte) 0x1A &&
                headerBytes[7] == (byte) 0x0A) {
                return ".png";
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Failed to decode base64 image. Falling back to .jpg. Reason: {}", e.getMessage());
            return ".jpg";
        }
        return ".jpg";
    }

}
