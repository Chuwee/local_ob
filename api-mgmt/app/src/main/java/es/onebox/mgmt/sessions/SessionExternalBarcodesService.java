package es.onebox.mgmt.sessions;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.mgmt.accesscontrol.dto.BarcodesDTO;
import es.onebox.mgmt.accesscontrol.dto.BarcodesFileDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.ExternalBarcodesRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.order.dto.ProductBarcodesResponseDTO;
import es.onebox.mgmt.datasources.ms.order.repository.SessionExternalBarcodeRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.ExternalBarcodeConverter;
import es.onebox.mgmt.sessions.converters.SessionUploadedExternalBarcodeConverter;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesResponseDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SessionExternalBarcodesService {

    private static final String HEADER_REPORT_TYPE = "reportType";
    private static final String REPORT_TYPE = "IMPORT_EXTERNAL_BARCODE_RESULT";

    private final DefaultProducer externalBarcodesProducer;
    private final ValidationService validationService;
    private final ExternalBarcodesRepository externalBarcodesRepository;
    private final SessionExternalBarcodeRepository sessionExternalBarcodeRepository;
    private final UsersRepository usersRepository;
    private final MasterdataRepository masterdataRepository;

    @Autowired
    public SessionExternalBarcodesService(final DefaultProducer externalBarcodesProducer,
                                          final ValidationService validationService,
                                          final ExternalBarcodesRepository externalBarcodesRepository,
                                          final SessionExternalBarcodeRepository sessionExternalBarcodeRepository,
                                          final UsersRepository usersRepository,
                                          final MasterdataRepository masterdataRepository) {
        this.externalBarcodesProducer = externalBarcodesProducer;
        this.validationService = validationService;
        this.externalBarcodesRepository = externalBarcodesRepository;
        this.sessionExternalBarcodeRepository = sessionExternalBarcodeRepository;
        this.usersRepository = usersRepository;
        this.masterdataRepository = masterdataRepository;
    }

    public ExternalBarcodesResponseDTO getExternalBarcodes(Long eventId, Long sessionId, String barcode, Long limit, Long offset) {
        validationService.getAndCheckSession(eventId, sessionId);

        ProductBarcodesResponseDTO productBarcodesResponseDTO = sessionExternalBarcodeRepository.getExternalBarcodes(eventId, sessionId, barcode, limit, offset);
        return SessionUploadedExternalBarcodeConverter.toDTO(productBarcodesResponseDTO);
    }

    public IdDTO uploadExternalBarcodes(Long eventId, Long sessionId, BarcodesDTO externalBarcodes) {
        validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckSession(eventId, sessionId);

        externalBarcodesRepository.verifyImportAvailable(sessionId);

        if (externalBarcodes.getEmail() == null) {
            externalBarcodes.setEmail(SecurityUtils.getUsername());
        }

        String language = null;
        User user = usersRepository.getById(SecurityUtils.getUserId());
        if(user != null && user.getLanguageId() != null) {
            MasterdataValue masterdataValue = masterdataRepository.getLanguage(user.getLanguageId().longValue());
            language = masterdataValue != null ? masterdataValue.getCode() : null;
        }

        Integer importProcessId = new Random().nextInt(Integer.MAX_VALUE);
        externalBarcodesRepository.startImport(sessionId, importProcessId);

        try {
            sendMessage(importProcessId, eventId, sessionId, externalBarcodes.getEmail(), language, externalBarcodes.getBarcodes());
        } catch (Exception e) {
            throw new OneboxRestException(ApiMgmtErrorCode.EXTERNAL_BARCODES_FILE_ERROR);
        }
        return new IdDTO(importProcessId.longValue());
    }

    public IdDTO getPendingUpload(Long eventId, Long sessionId){
        validationService.getAndCheckEvent(eventId);
        validationService.getAndCheckSession(eventId, sessionId);
        return externalBarcodesRepository.getPendingImport(sessionId);
    }

    private void sendMessage(Integer importProcessId, Long eventId, Long sessionId, String email,
                             String language, List<BarcodesFileDTO> barcodes) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(HEADER_REPORT_TYPE, REPORT_TYPE);
        externalBarcodesProducer.sendMessage(
                ExternalBarcodeConverter.toMessage(importProcessId, eventId, sessionId, email, language, barcodes), map);
    }

}
