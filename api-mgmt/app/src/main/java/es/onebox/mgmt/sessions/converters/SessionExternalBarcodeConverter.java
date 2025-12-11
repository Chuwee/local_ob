package es.onebox.mgmt.sessions.converters;

import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalBarcodeSessionConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.sessions.dto.SessionExternalBarcodeConfigDTO;
import es.onebox.mgmt.sessions.enums.SessionPassType;

import java.util.HashMap;
import java.util.Map;

public class SessionExternalBarcodeConverter {

    private static final String PERSON_TYPE = "personType";
    private static final String VARIABLE_CODE = "variableCode";
    private static final String PASS_TYPE = "passType";
    private static final String DAYS = "numDays";
    private static final String USES = "numUses";

    private SessionExternalBarcodeConverter() {}

    public static SessionExternalBarcodeConfigDTO toDTO(ExternalBarcodeSessionConfig source) {
        SessionExternalBarcodeConfigDTO target = new SessionExternalBarcodeConfigDTO();

        if (source != null) {
            Map<String, String> dataConfig = source.getDataConfig();

            if (dataConfig.containsKey(PERSON_TYPE)) {
                target.setPersonType(dataConfig.get(PERSON_TYPE));
            }
            if (dataConfig.containsKey(VARIABLE_CODE)) {
                target.setVariableCode(dataConfig.get(VARIABLE_CODE));
            }
            if (dataConfig.containsKey(PASS_TYPE)) {
                target.setPassType(SessionPassType.findByValue(dataConfig.get(PASS_TYPE)));
                if (dataConfig.containsKey(DAYS) && SessionPassType.DAYS.equals(target.getPassType())) {
                    target.setDays(Integer.parseInt(dataConfig.get(DAYS)));
                }
                if (dataConfig.containsKey(USES) && SessionPassType.USES.equals(target.getPassType())) {
                    target.setUses(Integer.parseInt(dataConfig.get(USES)));
                }
            }
        }

        return target;
    }

    public static ExternalBarcodeSessionConfig toMs(Long eventId, Long sessionId, SessionExternalBarcodeConfigDTO source, Session session, boolean hasSales) {
        ExternalBarcodeSessionConfig target = new ExternalBarcodeSessionConfig();
        target.setSessionId(sessionId.intValue());
        target.setEventId(eventId.intValue());
        target.setDataConfig(new HashMap<>());

        if (!hasSales) {
            target.getDataConfig().put(PERSON_TYPE, source.getPersonType());
            target.getDataConfig().put(VARIABLE_CODE, source.getVariableCode());
        }

        target.getDataConfig().put(PASS_TYPE, source.getPassType().getValue());
        if (SessionPassType.DAYS.equals(source.getPassType())) {
            target.getDataConfig().put(DAYS, source.getDays().toString());
        }
        if (SessionPassType.USES.equals(source.getPassType())) {
            target.getDataConfig().put(USES, source.getUses().toString());
        }

        return target;
    }
}
