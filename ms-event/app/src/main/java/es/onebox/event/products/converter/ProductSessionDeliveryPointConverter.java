package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.event.products.domain.ProductSessionDeliveryPointRecord;
import es.onebox.event.products.dto.ProductSessionDateDTO;
import es.onebox.event.products.dto.ProductSessionDeliveryPointDTO;
import es.onebox.event.products.dto.ProductSessionDeliveryPointDetailDTO;
import es.onebox.event.products.dto.ProductSessionDeliveryPointsDTO;
import es.onebox.event.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.event.products.dto.ProductSessionSearchFilter;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductSessionDeliveryPointConverter {

    public static ProductSessionDeliveryPointsDTO toEntity(
            List<ProductSessionDeliveryPointRecord> records,
            SessionSearchFilter filter,
            Long total
    ) {
        ProductSessionDeliveryPointsDTO dto = toEntity(records);
        if (dto != null && filter != null) {
            dto.setMetadata(MetadataBuilder.build(filter, total));
        }
        return dto;
    }

    public static ProductSessionDeliveryPointsDTO toEntity(List<ProductSessionDeliveryPointRecord> productSessionDeliveryPointRecords) {
        if (productSessionDeliveryPointRecords == null) {
            return null;
        }
        ProductSessionDeliveryPointsDTO result = new ProductSessionDeliveryPointsDTO();
        result.setData(new ArrayList<>());
        for (ProductSessionDeliveryPointRecord productSessionDeliveryPointRecord : productSessionDeliveryPointRecords) {
            if (result.getData().stream().noneMatch(re -> re.getId().equals(productSessionDeliveryPointRecord.getSessionid().longValue()))) {
                ProductSessionDeliveryPointDTO productSessionDeliveryPointDTO = new ProductSessionDeliveryPointDTO();
                productSessionDeliveryPointDTO.setId(productSessionDeliveryPointRecord.getSessionid().longValue());
                productSessionDeliveryPointDTO.setName(productSessionDeliveryPointRecord.getSessionName());
                productSessionDeliveryPointDTO.setDates(new ProductSessionDateDTO());
                productSessionDeliveryPointDTO.getDates().setStart(CommonUtils.timestampToZonedDateTime(productSessionDeliveryPointRecord.getSessionStart()));
                productSessionDeliveryPointDTO.getDates().setEnd(CommonUtils.timestampToZonedDateTime(productSessionDeliveryPointRecord.getSessionEnd()));
                productSessionDeliveryPointDTO.setSmartBooking(productSessionDeliveryPointRecord.getSessionType() != null
                        && VenueTemplateType.ACTIVITY.getId().equals(productSessionDeliveryPointRecord.getTemplateType()));

                ProductSessionDeliveryPointDetailDTO productSessionDeliveryPointDetailDTO = new ProductSessionDeliveryPointDetailDTO();
                if (productSessionDeliveryPointRecord.getDeliverypointid() != null) {
                    productSessionDeliveryPointDTO.setDeliveryPoints(new ArrayList<>());
                    productSessionDeliveryPointDetailDTO.setId(productSessionDeliveryPointRecord.getDeliverypointid().longValue());
                    productSessionDeliveryPointDetailDTO.setName(productSessionDeliveryPointRecord.getProductDeliveryPointName());
                    productSessionDeliveryPointDetailDTO.setIsDefault(ConverterUtils.isByteAsATrue(productSessionDeliveryPointRecord.getDefaultdeliverypoint()));
                    productSessionDeliveryPointDTO.getDeliveryPoints().add(productSessionDeliveryPointDetailDTO);
                }
                result.getData().add(productSessionDeliveryPointDTO);
            } else {
                Optional<ProductSessionDeliveryPointDTO> productSessionDeliveryPointDTOOpt = result.getData().stream().filter(r -> r.getId().equals(productSessionDeliveryPointRecord.getSessionid().longValue())).findFirst();
                ProductSessionDeliveryPointDetailDTO productSessionDeliveryPointDetailDTO = new ProductSessionDeliveryPointDetailDTO();
                productSessionDeliveryPointDetailDTO.setId(productSessionDeliveryPointRecord.getDeliverypointid().longValue());
                productSessionDeliveryPointDetailDTO.setName(productSessionDeliveryPointRecord.getProductDeliveryPointName());
                productSessionDeliveryPointDetailDTO.setIsDefault(ConverterUtils.isByteAsATrue(productSessionDeliveryPointRecord.getDefaultdeliverypoint()));
                productSessionDeliveryPointDTOOpt.get().getDeliveryPoints().add(productSessionDeliveryPointDetailDTO);
            }
        }
        return result;
    }

    public static SessionSearchFilter convertToSessionFilter(ProductSessionDeliveryPointsFilterDTO filter) {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setLimit(filter.getLimit());
        sessionFilter.setOffset(filter.getOffset());
        sessionFilter.setStartDate(filter.getStartDate());
        sessionFilter.setEndDate(filter.getEndDate());
        sessionFilter.setIds(filter.getIds());
        sessionFilter.setStatus(filter.getStatus());
        sessionFilter.setDaysOfWeek(filter.getDaysOfWeek());
        sessionFilter.setFreeSearch(filter.getFreeSearch());
        sessionFilter.setOlsonId(filter.getOlsonId());
        return sessionFilter;
    }

}
