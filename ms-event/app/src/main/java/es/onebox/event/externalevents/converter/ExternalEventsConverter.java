package es.onebox.event.externalevents.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventRateDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventTypeDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRatesRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRecord;

public class ExternalEventsConverter {
    private ExternalEventsConverter(){}

    public static CpanelExternalEventRecord newCpanelExternalEventRecord(ExternalEventDTO externalEventDTO){
        CpanelExternalEventRecord record = new CpanelExternalEventRecord();
        record.setExternaleventid(externalEventDTO.getEventId());
        record.setEntityid(externalEventDTO.getEntityId());
        record.setName(externalEventDTO.getEventName());
        record.setEventtype(ExternalEventsConverter.convertExternalEventType(externalEventDTO.getEventType()));
        return record;
    }


    public static ExternalEventDTO newExternalEventDTO(CpanelExternalEventRecord cpanelExternalEventRecord) {
        ExternalEventDTO externalEventDTO = new ExternalEventDTO();
        externalEventDTO.setInternalId(cpanelExternalEventRecord.getInternalid().longValue());
        externalEventDTO.setEntityId(cpanelExternalEventRecord.getEntityid());
        externalEventDTO.setEventId(cpanelExternalEventRecord.getExternaleventid());
        externalEventDTO.setEventName(cpanelExternalEventRecord.getName());
        externalEventDTO.setEventType(getExternalEventType(cpanelExternalEventRecord.getEventtype()));
        return externalEventDTO;
    }

    private static Byte convertExternalEventType(ExternalEventTypeDTO externalEventTypeDTO) {
        byte response;
        switch (externalEventTypeDTO) {
            case SEASON_TICKET:
                response = (byte) 2;
                break;
            default:
                response = (byte) 1;
                break;
        }
        return response;
    }

    private static ExternalEventTypeDTO getExternalEventType(byte type) {
        if (type == 2) {
            return ExternalEventTypeDTO.SEASON_TICKET;
        } else {
            return ExternalEventTypeDTO.EVENT;
        }
    }

    public static IdNameDTO convertRateRecord(CpanelExternalEventRatesRecord record) {
        if(record == null) {
            return null;
        }
        return new IdNameDTO(record.getRateid().longValue(), record.getRatename());
    }

    public static CpanelExternalEventRatesRecord convertToRateRecord(ExternalEventRateDTO rateDTO, Integer externalEventInternalId) {
        CpanelExternalEventRatesRecord record = new CpanelExternalEventRatesRecord();
        record.setExternaleventinternalid(externalEventInternalId);
        record.setRatename(rateDTO.getRateName());
        return record;
    }
}
