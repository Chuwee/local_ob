package es.onebox.event.attributes;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.dto.Attribute;
import es.onebox.event.datasources.ms.entity.dto.AttributeScope;
import es.onebox.event.datasources.ms.entity.dto.AttributeType;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventAttributeService extends AttributeService {

    @Autowired
    private EventAttributeDao eventAttributeDao;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private EntitiesRepository entitiesRepository;

    @MySQLRead
    public List<AttributeDTO> getEventAttributes(Long eventId) {
        getAndCheckEvent(eventId.intValue());
        return AttributeConverter.attributestoDTO(eventAttributeDao.getEventAttributes(eventId.intValue()));
    }

    private CpanelEventoRecord getAndCheckEvent(Integer eventId) {
        try {
            return eventDao.getById(eventId);
        } catch (EntityNotFoundException ex) {
            throw OneboxRestException
                    .builder(MsEventErrorCode.EVENT_NOT_FOUND)
                    .setMessage("Event attributes not found for id: " + eventId)
                    .build();
        }
    }

    @MySQLRead
    public Map<Integer, Map<Integer, List<Integer>>> getEventsAttributes(List<Long> eventIds) {
        Map<Integer, List<CpanelAtributosEventoRecord>> eventsAttributes = eventAttributeDao.getEventsAttributes(eventIds);
        Map<Integer, Map<Integer, List<Integer>>> result = new HashMap<>();

        for (Map.Entry<Integer, List<CpanelAtributosEventoRecord>> entry : eventsAttributes.entrySet()) {
            List<CpanelAtributosEventoRecord> eventAttributes = entry.getValue().stream()
                    .filter(cpanelAtributosEventoRecord ->
                        cpanelAtributosEventoRecord.getIdatributo() != null && cpanelAtributosEventoRecord.getIdvalor() != null)
                    .collect(Collectors.toList());

            Map<Integer, List<Integer>> attributes = eventAttributes.stream()
                    .collect(Collectors.groupingBy(CpanelAtributosEventoRecord::getIdatributo,
                            Collectors.mapping(CpanelAtributosEventoRecord::getIdvalor, Collectors.toList())));
            result.put(entry.getKey(), attributes);

        }
        return result;
    }

    @MySQLWrite
    public void putEventAttributes(Long eventId, List<AttributeRequestValueDTO> attributeRequestValueDTO) {
        CpanelEventoRecord eventoRecord = getAndCheckEvent(eventId.intValue());
        Map<Long, Attribute> attributes = entitiesRepository.getAttributes(eventoRecord.getIdentidad().longValue(), AttributeScope.EVENT)
                .stream()
                .collect(Collectors.toMap(Attribute::getId, Function.identity()));

        for (AttributeRequestValueDTO requestValueDTO : attributeRequestValueDTO) {
            Attribute attribute = attributes.get(requestValueDTO.getId());
            if (attribute == null) {
                throw OneboxRestException
                        .builder(MsEventErrorCode.ATTRIBUTE_INVALID_ID)
                        .setMessage("Attributes id: " + requestValueDTO.getId() + ", event id: " + eventId + ", is not valid")
                        .build();
            }
            switch (AttributeType.get(attribute.getType())) {
                case NUMERIC:
                    updateNumericValue(eventId, attribute, requestValueDTO);
                    break;
                case ALPHANUMERIC:
                    updateStringValue(eventId, attribute, requestValueDTO);
                    break;
                case DEFINED:
                    updateDefinedValue(eventId, attribute, requestValueDTO);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateNumericValue(Long eventId, Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getValue() != null) {
            checkNumericValue(attribute, requestValueDTO);
            upsertAttributeValue(eventId, requestValueDTO);
        }
    }

    private void updateStringValue(Long eventId, Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getValue() != null) {
            checkStringValue(attribute, requestValueDTO);
            upsertAttributeValue(eventId, requestValueDTO);
        }
    }

    private void updateDefinedValue(Long eventId, Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getSelected() != null) {
            List<CpanelAtributosEventoRecord> eventAttributes = eventAttributeDao.getEventAttribute(eventId.intValue(), requestValueDTO.getId().intValue());

            checkDefinedType(attribute, requestValueDTO);

            for (CpanelAtributosEventoRecord eventAttribute : eventAttributes) {
                if (!requestValueDTO.getSelected().contains(eventAttribute.getIdvalor().longValue())) {
                    eventAttributeDao.delete(eventAttribute);
                }
            }
            List<Integer> valueIds = eventAttributes.stream().map(CpanelAtributosEventoRecord::getIdvalor).collect(Collectors.toList());
            for (Long valueId : requestValueDTO.getSelected()) {
                if (!valueIds.contains(valueId.intValue())) {
                    CpanelAtributosEventoRecord eventAttribute = new CpanelAtributosEventoRecord();
                    eventAttribute.setIdevento(eventId.intValue());
                    eventAttribute.setIdatributo(requestValueDTO.getId().intValue());
                    eventAttribute.setIdvalor(valueId.intValue());
                    eventAttributeDao.insert(eventAttribute);
                }
            }
        }
    }

    private void upsertAttributeValue(Long eventId, AttributeRequestValueDTO requestValueDTO) {
        List<CpanelAtributosEventoRecord> eventAttributes = eventAttributeDao.getEventAttribute(eventId.intValue(), requestValueDTO.getId().intValue());
        if (CollectionUtils.isEmpty(eventAttributes)) {
            CpanelAtributosEventoRecord eventAttribute = new CpanelAtributosEventoRecord();
            eventAttribute.setIdevento(eventId.intValue());
            eventAttribute.setIdatributo(requestValueDTO.getId().intValue());
            eventAttribute.setValor(requestValueDTO.getValue());
            eventAttributeDao.insert(eventAttribute);
        } else {
            CpanelAtributosEventoRecord eventAttribute = eventAttributes.get(0);
            eventAttribute.setValor(requestValueDTO.getValue());
            eventAttributeDao.update(eventAttribute);
        }
    }

}
