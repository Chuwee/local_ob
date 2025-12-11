package es.onebox.event.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.enums.PriceZoneRestrictionType;
import es.onebox.event.events.dto.EventTemplateRestrictionDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventTemplateRestrictionConverter {

    private EventTemplateRestrictionConverter() {
    }

    public static EventTemplateRestrictionDTO fromRecord(Long priceTypeId, PriceZoneRestriction priceZoneRestriction,
                                                         Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById) {
        if (priceZoneRestriction == null) {
            return null;
        }
        EventTemplateRestrictionDTO result = new EventTemplateRestrictionDTO();
        result.setLockedPriceType(new IdNameDTO(priceTypeId, priceTypesById.get(priceTypeId.intValue()).getDescripcion()));
        if (CollectionUtils.isNotEmpty(priceZoneRestriction.getRequiredPriceZones())) {
            result.setRequiredPriceTypes(priceZoneRestriction.getRequiredPriceZones().stream()
                    .map(id -> new IdNameDTO(id.longValue(), priceTypesById.get(id).getDescripcion()))
                    .collect(Collectors.toList()));
        }
        if(priceZoneRestriction.getMaxItemsMultiplier() == 1 && priceZoneRestriction.getRestrictionType() != null) {
            if(PriceZoneRestrictionType.LOCKED.equals(priceZoneRestriction.getRestrictionType())) {
                result.setLockedTickets(priceZoneRestriction.getMaxItemsMultiplier().intValue());
            } else {
                double tickets = 1 / priceZoneRestriction.getMaxItemsMultiplier();
                result.setRequiredTickets((int)Math.ceil(tickets));
            }
            return result;
        }
        if (priceZoneRestriction.getMaxItemsMultiplier() >= 1) {
            result.setLockedTickets(priceZoneRestriction.getMaxItemsMultiplier().intValue());
        } else {
            double tickets = 1 / priceZoneRestriction.getMaxItemsMultiplier();
            result.setRequiredTickets((int)Math.ceil(tickets));
        }
        return result;
    }

    public static List<IdNameDTO> fromRecord(PriceZonesRestrictions priceZonesRestrictions,
                                             Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById) {
        if (priceZonesRestrictions == null) {
            return null;
        }
        List<IdNameDTO> result = new ArrayList<>();
        for(Map.Entry<Integer, PriceZoneRestriction> priceTypeRestriction : priceZonesRestrictions.entrySet()) {
            if(priceTypesById.containsKey(priceTypeRestriction.getKey())) {
                IdNameDTO idNameDTO = new IdNameDTO();
                idNameDTO.setId(priceTypeRestriction.getKey().longValue());
                idNameDTO.setName(priceTypesById.get(priceTypeRestriction.getKey()).getDescripcion());
                result.add(idNameDTO);
            }
        }
        return result;
    }

    public static PriceZoneRestriction convert(List<Long> requiredPriceTypeIds,
                                               Integer requiredTicketNumber, Integer lockedTicketNumber) {
        PriceZoneRestriction result = new PriceZoneRestriction();
        result.setRequiredPriceZones(requiredPriceTypeIds.stream()
                .map(Long::intValue)
                .collect(Collectors.toList()));
        if(requiredTicketNumber != null) {
            result.setMaxItemsMultiplier(1/Double.valueOf(requiredTicketNumber));
            result.setRestrictionType(PriceZoneRestrictionType.REQUIRED);
        } else {
            result.setMaxItemsMultiplier(Double.valueOf(lockedTicketNumber));
            result.setRestrictionType(PriceZoneRestrictionType.LOCKED);
        }
        return result;
    }
}
