package es.onebox.mgmt.events.converter;

import com.google.common.collect.ImmutableMap;
import es.onebox.mgmt.datasources.ms.event.dto.event.ExternalBarcodeEventConfig;
import es.onebox.mgmt.events.dto.EventExternalBarcodesConfigDTO;
import org.apache.commons.lang3.BooleanUtils;

public class EventExternalBarcodeConverter {

    private static final String FAIR_CODE = "fairCode";
    private static final String FAIR_EDITION = "fairEdition";

    private EventExternalBarcodeConverter() {}

    public static EventExternalBarcodesConfigDTO toDTO(ExternalBarcodeEventConfig source) {
        EventExternalBarcodesConfigDTO target = new EventExternalBarcodesConfigDTO();
        if (source != null) {
            target.setAllowed(source.getAllow());
            if (source.getDataConfig() != null) {
                if (source.getDataConfig().containsKey(FAIR_CODE)) {
                    target.setFairCode(source.getDataConfig().get(FAIR_CODE));
                }
                if (source.getDataConfig().containsKey(FAIR_EDITION)) {
                    target.setFairEdition(source.getDataConfig().get(FAIR_EDITION));
                }
            }
        }
        return target;
    }

    public static ExternalBarcodeEventConfig toMs(EventExternalBarcodesConfigDTO source, Long eventId, Long entityId) {
        ExternalBarcodeEventConfig target = new ExternalBarcodeEventConfig();
        target.setAllow(source.getAllowed());
        if (BooleanUtils.isTrue(source.getAllowed())) {
            target.setDataConfig(ImmutableMap.of(
                    FAIR_CODE, source.getFairCode(),
                    FAIR_EDITION, source.getFairEdition())
            );
            target.setEventId(eventId.intValue());
            target.setEntityId(entityId.intValue());
        }
        return target;
    }
}
