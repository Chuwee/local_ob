package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDate;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDeliveryPointDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessionDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessionDeliveryPointDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessionDeliveryPoints;
import es.onebox.mgmt.products.dto.ProductSessionDateDTO;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointDTO;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointDetailDTO;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsDTO;
import es.onebox.mgmt.products.dto.ProductSessionSmartBookingDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDeliveryPointDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDeliveryPointDetailDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionDeliveryPointsDTO;
import es.onebox.mgmt.sessions.enums.SessionSmartBookingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductSessionDeliveryPointConverter {

    private ProductSessionDeliveryPointConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static UpdateProductSessionDeliveryPoints convert(UpdateProductSessionDeliveryPointsDTO updateProductSessionDeliveryPointsDTO) {
        UpdateProductSessionDeliveryPoints updateProductSessionDeliveryPoints = new UpdateProductSessionDeliveryPoints();
        for (UpdateProductSessionDeliveryPointDTO updateProductSessionDeliveryPointDTO : updateProductSessionDeliveryPointsDTO) {
            UpdateProductSessionDeliveryPoint updateProductSessionDeliveryPoint = new UpdateProductSessionDeliveryPoint();
            updateProductSessionDeliveryPoint.setId(updateProductSessionDeliveryPointDTO.getId());
            updateProductSessionDeliveryPoint.setDeliveryPoints(convertDetails(updateProductSessionDeliveryPointDTO.getDeliveryPoints()));
            updateProductSessionDeliveryPoints.add(updateProductSessionDeliveryPoint);
        }
        return updateProductSessionDeliveryPoints;
    }

    public static List<UpdateProductSessionDeliveryPointDetail> convertDetails(List<UpdateProductSessionDeliveryPointDetailDTO> updateProductSessionDeliveryPointDetailDTOS) {
        List<UpdateProductSessionDeliveryPointDetail> result = new ArrayList<>();
        for (UpdateProductSessionDeliveryPointDetailDTO updateProductSessionDeliveryPointDetailDTO: updateProductSessionDeliveryPointDetailDTOS) {
            UpdateProductSessionDeliveryPointDetail updateProductSessionDeliveryPointDetail = new UpdateProductSessionDeliveryPointDetail();
            updateProductSessionDeliveryPointDetail.setDeliveryPointId(updateProductSessionDeliveryPointDetailDTO.getDeliveryPointId());
            updateProductSessionDeliveryPointDetail.setIsDefault(updateProductSessionDeliveryPointDetailDTO.getIsDefault());
            result.add(updateProductSessionDeliveryPointDetail);
        }
        return result;
    }

    public static ProductSessionDeliveryPointsDTO toDto(ProductSessionDeliveryPoints src) {
        ProductSessionDeliveryPointsDTO out = new ProductSessionDeliveryPointsDTO();
        if (src == null) {
            return out;
        }
        out.setMetadata(src.getMetadata());
        out.setData(convertToDtoList(src.getData()));
        return out;
    }

    private static List<ProductSessionDeliveryPointDTO> convertToDtoList(List<ProductSessionDeliveryPoint> data) {
        if (data == null || data.isEmpty()) {
            return List.of();
        }
        return data.stream()
                .map(ProductSessionDeliveryPointConverter::toDtoItem)
                .filter(Objects::nonNull)
                .toList();
    }

    private static ProductSessionDeliveryPointDTO toDtoItem(ProductSessionDeliveryPoint in) {
        if (in == null) return null;

        ProductSessionDeliveryPointDTO dto = new ProductSessionDeliveryPointDTO();
        dto.setId(in.getId());
        dto.setName(in.getName());
        dto.setSmartBooking(toSmartBookingDTO(in));
        dto.setDates(toDates(in.getDates()));
        if (in.getDeliveryPoints() != null) {
            dto.setDeliveryPoints(toDetailDto(in.getDeliveryPoints()));
        }
        return dto;
    }

    private static ProductSessionDateDTO toDates(ProductSessionDate productSessionDate) {
        ProductSessionDateDTO productSessionDateDTO = new ProductSessionDateDTO();
        productSessionDateDTO.setStart(productSessionDate.getStart());
        productSessionDateDTO.setEnd(productSessionDate.getEnd());
        return productSessionDateDTO;
    }
    public static List<ProductSessionDeliveryPointDetailDTO> toDetailDto(List<ProductSessionDeliveryPointDetail> productSessionDeliveryPointDetailList) {
        List<ProductSessionDeliveryPointDetailDTO> result = new ArrayList<>();
        for(ProductSessionDeliveryPointDetail productSessionDeliveryPointDetail : productSessionDeliveryPointDetailList) {
            ProductSessionDeliveryPointDetailDTO productSessionDeliveryPointDetailDTO = new ProductSessionDeliveryPointDetailDTO();
            productSessionDeliveryPointDetailDTO.setId(productSessionDeliveryPointDetail.getId());
            productSessionDeliveryPointDetailDTO.setName(productSessionDeliveryPointDetail.getName());
            productSessionDeliveryPointDetailDTO.setIsDefault(productSessionDeliveryPointDetail.getIsDefault());
            result.add(productSessionDeliveryPointDetailDTO);
        }
        return result;
    }

    private static ProductSessionSmartBookingDTO toSmartBookingDTO(ProductSessionDeliveryPoint in) {
        if (in.getSmartBooking() == null) {
            return null;
        }
        ProductSessionSmartBookingDTO smartBooking = new ProductSessionSmartBookingDTO();
        smartBooking.setType(in.getSmartBooking() ? SessionSmartBookingType.SMART_BOOKING : SessionSmartBookingType.SEAT_SELECTION);
        return smartBooking;
    }
}
