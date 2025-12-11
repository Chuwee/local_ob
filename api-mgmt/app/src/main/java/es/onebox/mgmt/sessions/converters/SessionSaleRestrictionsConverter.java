package es.onebox.mgmt.sessions.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypeLimit;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.session.UpdateSaleRestriction;
import es.onebox.mgmt.sessions.dto.PriceTypeDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeLimitDTO;
import es.onebox.mgmt.sessions.dto.SessionSaleRestrictionDTO;
import es.onebox.mgmt.sessions.dto.SessionSaleRestrictionsDTO;
import es.onebox.mgmt.sessions.dto.UpdatePriceTypeLimitDTO;
import es.onebox.mgmt.sessions.dto.UpdateSaleRestrictionDTO;

import java.util.ArrayList;
import java.util.List;

public class SessionSaleRestrictionsConverter {

    private SessionSaleRestrictionsConverter() {
    }


    public static UpdateSaleRestriction convert(UpdateSaleRestrictionDTO source) {
        if (source == null) {
            return null;
        }
        UpdateSaleRestriction target = new UpdateSaleRestriction();
        target.setLockedTicketsNumber(source.getLockedTicketsNumber());
        target.setRequiredTicketsNumber(source.getRequiredTicketsNumber());
        target.setRequiredPriceTypeIds(source.getRequiredPriceTypeIds());
        return target;
    }

    public static PriceTypeLimit convert(UpdatePriceTypeLimitDTO source) {
        if (source == null) {
            return null;
        }
        PriceTypeLimit target = new PriceTypeLimit();
        target.setId(source.getPriceTypeId());
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    public static SessionSaleRestrictionDTO convert(SessionSaleRestriction source) {
        if (source == null) {
            return null;
        }
        SessionSaleRestrictionDTO target = new SessionSaleRestrictionDTO();
        target.setLockedPriceType(mapPriceType(source.getLockedPriceType()));
        target.setRequiredPriceTypes(new ArrayList<>());
        if (source.getRequiredPriceTypes() != null) {
            for (PriceTypeDTO dto : source.getRequiredPriceTypes()) {
                IdNameDTO priceTypeDTO = mapPriceType(dto);
                target.getRequiredPriceTypes().add(priceTypeDTO);
            }
        }
        target.setLockedTickets(source.getLockedTickets());
        target.setRequiredTickets(source.getRequiredTickets());
        return target;
    }

    private static IdNameDTO mapPriceType(PriceTypeDTO priceTypeDTO) {
        if (priceTypeDTO != null) {
            IdNameDTO result = new IdNameDTO();
            result.setId(priceTypeDTO.getId());
            result.setName(priceTypeDTO.getName());
            return result;
        } else {
            return null;
        }
    }

    private static PriceTypeLimitDTO convert(PriceTypeLimit source, String priceTypeName) {
        if (source == null) {
            return null;
        }
        PriceTypeLimitDTO target = new PriceTypeLimitDTO();
        target.setPriceTypeId(source.getId());
        target.setPriceTypeName(priceTypeName);
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        return target;
    }

    public static SessionSaleRestrictionsDTO convert(List<IdNameDTO> source) {
        SessionSaleRestrictionsDTO result = new SessionSaleRestrictionsDTO();
        Metadata metadata = new Metadata();
        if(source != null) {
            result.setData(source);
            metadata.setTotal((long) source.size());
        }
        metadata.setOffset(0L);
        metadata.setLimit(1000L);
        result.setMetadata(metadata);
        return result;
    }

}
