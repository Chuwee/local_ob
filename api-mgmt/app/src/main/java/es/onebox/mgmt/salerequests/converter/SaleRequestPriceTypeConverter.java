package es.onebox.mgmt.salerequests.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.PriceType;
import es.onebox.mgmt.salerequests.dto.BaseVenueSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.PriceTypeDTO;
import es.onebox.mgmt.salerequests.dto.PriceTypesDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaleRequestPriceTypeConverter {

    private SaleRequestPriceTypeConverter() {throw new UnsupportedOperationException("Cannot instantiate convert class");}

    public static void fillPriceTypeData(PriceTypesDTO priceTypesDto, List<PriceType> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            priceTypesDto.setData(new ArrayList<>());
            data.stream().map(SaleRequestPriceTypeConverter::convertToPriceTypeDto)
                    .filter(Objects::nonNull).forEach(item -> priceTypesDto.getData().add(item));
        }
    }

    private static PriceTypeDTO convertToPriceTypeDto(PriceType priceType) {
        if (Objects.nonNull(priceType)) {
            PriceTypeDTO priceTypeDto = new PriceTypeDTO();
            priceTypeDto.setId(priceType.getId());
            priceTypeDto.setName(priceType.getName());
            fillVenueConfigData(priceTypeDto, priceType.getVenueConfig());
            return priceTypeDto;
        }
        return null;
    }

    private static void fillVenueConfigData(PriceTypeDTO priceTypeDto, IdNameDTO venueConfig) {
        if (Objects.nonNull(priceTypeDto) && Objects.nonNull(venueConfig)) {
            BaseVenueSaleRequestDTO venueConfigDto = new BaseVenueSaleRequestDTO();
            venueConfigDto.setId(venueConfig.getId());
            venueConfigDto.setName(venueConfig.getName());
            priceTypeDto.setVenueConfig(venueConfigDto);
        }
    }
}
