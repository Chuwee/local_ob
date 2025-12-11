package es.onebox.event.events.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.request.PriceTypeFilter;
import es.onebox.event.events.converter.PriceTypeConverter;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.PriceTypeConfigCustomRecord;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.PriceTypesDTO;
import es.onebox.event.events.dto.UpdatePriceTypesDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.annotation.MySQLRead;

@Service
public class EventPriceTypeService {

    private final EventDao eventDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final PriceTypeConfigDao priceTypeConfigDao;
    
    public EventPriceTypeService(EventDao eventDao, PriceTypeConfigDao priceTypeConfigDao, EventConfigCouchDao eventConfigCouchDao) {
        this.eventDao = eventDao;
        this.priceTypeConfigDao = priceTypeConfigDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
    }
    
    @MySQLRead
    public PriceTypesDTO getEventPriceTypes(Long eventId, PriceTypeFilter filter) {

        if (BooleanUtils.isFalse(eventDao.existEventById(eventId))) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).
                    setMessage("Event: " + eventId + " not found").build();
        }

        List<PriceTypeConfigCustomRecord> priceTypes = priceTypeConfigDao.getPriceTypeWithVenueConfigByEventId(eventId, filter);
        PriceTypesDTO priceTypesDTO = new PriceTypesDTO();

        if (priceTypes != null) {
            EventConfig eventConfig = this.eventConfigCouchDao.get(String.valueOf(eventId));
            if (eventConfig != null && eventConfig.getInventoryProvider() != null &&  CollectionUtils.isNotEmpty(eventConfig.getUpsellingPriceZones())) {
                priceTypesDTO.setData(PriceTypeConverter.convertToPriceTypeDto(priceTypes, eventConfig.getUpsellingPriceZones()));
            } else {
                priceTypesDTO.setData(PriceTypeConverter.convertToPriceTypeDto(priceTypes));
            }
            Long total = priceTypeConfigDao.getTotalPriceTypeWithVenueConfigByEventId(eventId, filter);
            priceTypesDTO.setMetadata(MetadataBuilder.build(filter, total));
            return priceTypesDTO;
        }
        return null;
    }

    public void upsert(Long eventId, UpdatePriceTypesDTO body) {
        EventConfig config = eventConfigCouchDao.getOrInitEventConfig(eventId);
        Set<Long> upselling = config.getUpsellingPriceZones();
        if (BooleanUtils.isTrue(body.getUpsell())) {
            addUpsellingPriceZones(body, config, upselling);
        } else {
            removeUpsellingPriceZones(body,  upselling);
        }
        eventConfigCouchDao.upsert(String.valueOf(eventId),config);
    }

    private static void removeUpsellingPriceZones(UpdatePriceTypesDTO body, Set<Long> upselling) {
        if (upselling == null) {
            return;
        }
        upselling.removeAll(body.getIds());        
    }

    protected static void addUpsellingPriceZones(UpdatePriceTypesDTO body, EventConfig config, Set<Long> upselling) {
        if (upselling == null) {
            config.setUpsellingPriceZones(new HashSet<>(body.getIds()));
        } else {
            upselling.addAll(body.getIds());
        }
    }
}
