package es.onebox.event.attributes;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.dto.Attribute;
import es.onebox.event.datasources.ms.entity.dto.AttributeScope;
import es.onebox.event.datasources.ms.entity.dto.AttributeType;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SessionAttributeService extends AttributeService {

    @Autowired
    private SessionAttributeDao sessionAttributeDao;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private SessionValidationHelper sessionValidationHelper;

    @MySQLRead
    public List<AttributeDTO> getSessionAttributes(Long eventId, Long sessionId) {
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        return AttributeConverter.sessionAttributestoDTO(sessionAttributeDao.getSessionAttributes(sessionId.intValue()));
    }

    @MySQLWrite
    public void putSessionAttributes(Long eventId, Long sessionId, List<AttributeRequestValueDTO> attributeRequestValueDTO) {
        sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId);
        CpanelEventoRecord eventoRecord = eventDao.getById(eventId.intValue());

        Map<Long, Attribute> attributes = entitiesRepository.getAttributes(eventoRecord.getIdentidad().longValue(), AttributeScope.SESSION)
                .stream()
                .collect(Collectors.toMap(Attribute::getId, Function.identity()));

        for (AttributeRequestValueDTO requestValueDTO : attributeRequestValueDTO) {
            Attribute attribute = attributes.get(requestValueDTO.getId());
            if (attribute == null) {
                throw OneboxRestException
                        .builder(MsEventErrorCode.ATTRIBUTE_INVALID_ID)
                        .setMessage("Attributes id: " + requestValueDTO.getId() + ", session id: " + sessionId + ", is not valid")
                        .build();
            }
            switch (AttributeType.get(attribute.getType())) {
                case NUMERIC:
                    updateNumericValue(sessionId, attribute, requestValueDTO);
                    break;
                case ALPHANUMERIC:
                    updateStringValue(sessionId, attribute, requestValueDTO);
                    break;
                case DEFINED:
                    updateDefinedValue(sessionId, attribute, requestValueDTO);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateNumericValue(Long sessionId, Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getValue() != null) {
            checkNumericValue(attribute, requestValueDTO);
            upsertAttributeValue(sessionId, requestValueDTO);
        }
    }

    private void updateStringValue(Long sessionId, Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getValue() != null) {
            checkStringValue(attribute, requestValueDTO);
            upsertAttributeValue(sessionId, requestValueDTO);
        }
    }

    private void updateDefinedValue(Long sessionId, Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getSelected() != null) {
            List<CpanelAtributosSesionRecord> sessionAttributes = sessionAttributeDao.getSessionAttribute(sessionId.intValue(), requestValueDTO.getId().intValue());

            checkDefinedType(attribute, requestValueDTO);

            for (CpanelAtributosSesionRecord sessionAttribute : sessionAttributes) {
                if (!requestValueDTO.getSelected().contains(sessionAttribute.getIdvalor().longValue())) {
                    sessionAttributeDao.delete(sessionAttribute);
                }
            }
            List<Integer> valueIds = sessionAttributes.stream().map(CpanelAtributosSesionRecord::getIdvalor).collect(Collectors.toList());
            for (Long valueId : requestValueDTO.getSelected()) {
                if (!valueIds.contains(valueId.intValue())) {
                    CpanelAtributosSesionRecord record = new CpanelAtributosSesionRecord();
                    record.setIdsesion(sessionId.intValue());
                    record.setIdatributo(requestValueDTO.getId().intValue());
                    record.setIdvalor(valueId.intValue());
                    sessionAttributeDao.insert(record);
                }
            }
        }
    }

    private void upsertAttributeValue(Long sessionId, AttributeRequestValueDTO requestValueDTO) {
        List<CpanelAtributosSesionRecord> sessionAttributes = sessionAttributeDao.getSessionAttribute(sessionId.intValue(), requestValueDTO.getId().intValue());
        if (CollectionUtils.isEmpty(sessionAttributes)) {
            CpanelAtributosSesionRecord sessionAttribute = new CpanelAtributosSesionRecord();
            sessionAttribute.setIdsesion(sessionId.intValue());
            sessionAttribute.setIdatributo(requestValueDTO.getId().intValue());
            sessionAttribute.setValor(requestValueDTO.getValue());
            sessionAttributeDao.insert(sessionAttribute);
        } else {
            CpanelAtributosSesionRecord sessionAttribute = sessionAttributes.get(0);
            sessionAttribute.setValor(requestValueDTO.getValue());
            sessionAttributeDao.update(sessionAttribute);
        }
    }

}
