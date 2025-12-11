package es.onebox.event.sessions.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.enums.PriceZoneRestrictionType;
import es.onebox.event.sessions.domain.sessionconfig.PriceTypeLimit;
import es.onebox.event.sessions.dto.PriceTypeRestrictionDTO;
import es.onebox.event.sessions.dto.SessionSaleRestrictionDTO;
import es.onebox.event.sessions.dto.UpdatePriceTypeLimitDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

public class PriceTypeRestrictionConverter {

    private PriceTypeRestrictionConverter() {
    }

    public static PriceTypeRestrictionDTO convert(Long priceTypeId, PriceZoneRestriction priceZoneRestriction,
                                                  Map<Integer, CpanelZonaPreciosConfigRecord> priceTypesById) {

        PriceTypeRestrictionDTO result = new PriceTypeRestrictionDTO();
        result.setLockedPriceType(new IdNameDTO(priceTypeId, priceTypesById.get(priceTypeId.intValue()).getDescripcion()));
        if (priceZoneRestriction.getRequiredPriceZones() != null) {
            result.setRequiredPriceTypes(
                    priceZoneRestriction.getRequiredPriceZones().stream()
                            .map(id -> new IdNameDTO(id.longValue(), priceTypesById.get(id).getDescripcion()))
                            .collect(Collectors.toList())
            );
        }
        if (priceZoneRestriction.getMaxItemsMultiplier() != null) {
            if (priceZoneRestriction.getMaxItemsMultiplier() >= 1) {
                result.setRequiredTickets(null);
                result.setLockedTickets(priceZoneRestriction.getMaxItemsMultiplier().intValue());
            } else {
                double tickets = 1 / priceZoneRestriction.getMaxItemsMultiplier();
                result.setRequiredTickets((int) tickets);
                result.setLockedTickets(null);
            }
        }
        return result;
    }

    public static PriceZoneRestriction convert(List<Long> requiredPriceTypesIds, Integer requiredTicketNumber,
                                               Integer lockedTicketNumber) {
        PriceZoneRestriction result = new PriceZoneRestriction();
        result.setRequiredPriceZones(requiredPriceTypesIds.stream()
                .map(Long::intValue)
                .collect(Collectors.toList()));
        if(requiredTicketNumber != null) {
            result.setMaxItemsMultiplier(1/Double.valueOf(requiredTicketNumber));
            result.setRestrictionType(PriceZoneRestrictionType.REQUIRED);
        }else {
            result.setMaxItemsMultiplier(Double.valueOf(lockedTicketNumber));
            result.setRestrictionType(PriceZoneRestrictionType.LOCKED);
        }
        return result;
    }

    public static SessionSaleRestrictionDTO convert(List<PriceTypeLimit> priceTypeLimits, Integer idSession) {
        if (priceTypeLimits == null) {
            return null;
        }
        SessionSaleRestrictionDTO target = new SessionSaleRestrictionDTO();
        target.setSessionId(idSession);
        target.setPriceTypeRestrictions(priceTypeLimits.stream()
                .map(PriceTypeRestrictionConverter::convertPriceTypeLimit)
                .collect(Collectors.toList()));
        return target;
    }

    public static List<IdNameDTO> convert(PriceZonesRestrictions priceZonesRestrictions, Map<Integer, CpanelZonaPreciosConfigRecord> priceTypes) {
        List<IdNameDTO> result = new ArrayList<>();
        for(Map.Entry<Integer, PriceZoneRestriction> priceZoneRestriction : priceZonesRestrictions.entrySet()) {
            if(priceTypes.containsKey(priceZoneRestriction.getKey())) {
                IdNameDTO idNameDTO = new IdNameDTO();
                idNameDTO.setId(priceZoneRestriction.getKey().longValue());
                idNameDTO.setName(priceTypes.get(priceZoneRestriction.getKey()).getDescripcion());
                result.add(idNameDTO);
            }
        }
        return result;
    }

    public static PriceTypeRestrictionDTO convertPriceTypeLimit(PriceTypeLimit source) {
        if (source == null) {
            return null;
        }
        PriceTypeRestrictionDTO target = new PriceTypeRestrictionDTO();
        target.setRequiredPriceTypes(new ArrayList<>());
        target.setLockedPriceType(new IdNameDTO());
        return target;
    }

    public static PriceTypeLimit convert(UpdatePriceTypeLimitDTO source) {
        if (source == null) {
            return null;
        }
        PriceTypeLimit target = new PriceTypeLimit();
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        target.setId(source.getId());
        return target;
    }

    public static List<PriceTypeLimit> convert(List<UpdatePriceTypeLimitDTO> source) {
        if (source == null) {
            return new ArrayList<>();
        }
        return source.stream()
                .map(PriceTypeRestrictionConverter::convert)
                .collect(Collectors.toList());
    }
}
