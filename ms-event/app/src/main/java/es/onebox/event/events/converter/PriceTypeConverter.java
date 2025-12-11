package es.onebox.event.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.dao.record.PriceTypeConfigCustomRecord;
import es.onebox.event.events.dto.PriceTypeDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PriceTypeConverter {

    private PriceTypeConverter() {
        throw new UnsupportedOperationException("Cannot instantiate convert class");
    }

    public static List<PriceTypeDTO> convertToPriceTypeDto(List<PriceTypeConfigCustomRecord> result) {
        if (CollectionUtils.isNotEmpty(result)) {
            return result.stream().filter(Objects::nonNull).map( r-> PriceTypeConverter.convertToPriceType(r, null)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    public static List<PriceTypeDTO> convertToPriceTypeDto(List<PriceTypeConfigCustomRecord> result, Set<Long> upsellingPriceZones) {
        if (CollectionUtils.isNotEmpty(result)) {
            return result.stream().filter(Objects::nonNull).map(r-> PriceTypeConverter.convertToPriceType(r, upsellingPriceZones)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private static PriceTypeDTO convertToPriceType(PriceTypeConfigCustomRecord priceTypeConfigCustomRecord, Set<Long> upsellingPriceZones) {
        PriceTypeDTO out = new PriceTypeDTO();
        long id = priceTypeConfigCustomRecord.getIdzona().longValue();
        out.setId(id);
        out.setName(priceTypeConfigCustomRecord.getDescripcion());
        fillVenueConfig(out, priceTypeConfigCustomRecord.getVenueConfig());
        if (CollectionUtils.isNotEmpty(upsellingPriceZones)) {
            out.setUpsell(upsellingPriceZones.contains(id));
        }
        return out;
    }

    private static void fillVenueConfig(PriceTypeDTO priceTypeDTO, CpanelConfigRecintoRecord cpanelConfigRecintoRecord) {
        if (Objects.nonNull(cpanelConfigRecintoRecord)) {
            IdNameDTO venueConfig = new IdNameDTO();
            venueConfig.setId(cpanelConfigRecintoRecord.getIdconfiguracion().longValue());
            venueConfig.setName(cpanelConfigRecintoRecord.getNombreconfiguracion());
            priceTypeDTO.setVenueConfig(venueConfig);
        }
    }


}
