package es.onebox.event.sessions.service;

import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.SessionExternalBarcodeConfigDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.ExternalBarcodeSessionConfigDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SessionExternalBarcodeConfigService {

    private static final String PERSON_TYPE = "personType";
    private static final String VARIABLE_CODE = "variableCode";
    private static final String PASS_TYPE = "passType";
    private static final String DAYS = "numDays";
    private static final String USES = "numUses";

    private final SessionExternalBarcodeConfigDao sessionExternalBarcodeConfigDao;
    private final SessionDao sessionDao;
    private final OrdersRepository ordersRepository;

    @Autowired
    public SessionExternalBarcodeConfigService(SessionExternalBarcodeConfigDao sessionExternalBarcodeConfigDao,
                                               SessionDao sessionDao, OrdersRepository ordersRepository) {
        this.sessionExternalBarcodeConfigDao = sessionExternalBarcodeConfigDao;
        this.sessionDao = sessionDao;
        this.ordersRepository = ordersRepository;
    }

    public ExternalBarcodeSessionConfigDTO getExternalBarcodeSessionConfig(Long sessionId) {
        return sessionExternalBarcodeConfigDao.get(sessionId.toString());
    }

    public void upsertExternalBarcodeSessionConfig(Long sessionId, ExternalBarcodeSessionConfigDTO externalBarcodeSessionConfigDTO) {
        SessionRecord session = sessionDao.findSession(sessionId);
        if (BooleanUtils.isNotTrue(session.getIsexternal())) {
            session.setIsexternal(true);
            session.update();
        }

        ExternalBarcodeSessionConfigDTO config = getExternalBarcodeSessionConfig(sessionId);
        if (config != null) {
            Long sessionSales = ordersRepository.countBySession(sessionId);
            updateMapConfig(externalBarcodeSessionConfigDTO.getDataConfig(), config.getDataConfig(), sessionSales);
        } else {
            config = externalBarcodeSessionConfigDTO;
        }

        sessionExternalBarcodeConfigDao.upsert(sessionId.toString(), config);
    }

    private static void updateMapConfig(Map<String, String> source, Map<String, String> target, Long sessionSales) {
        if (sessionSales.equals(0L)) {
            target.put(PERSON_TYPE, source.get(PERSON_TYPE));
            target.put(VARIABLE_CODE, source.get(VARIABLE_CODE));
        }
        target.put(PASS_TYPE, source.get(PASS_TYPE));
        if (source.containsKey(DAYS)) {
            target.put(DAYS, source.get(DAYS));
            target.remove(USES);
        }
        if (source.containsKey(USES)) {
            target.put(USES, source.get(USES));
            target.remove(DAYS);
        }
    }
}
