package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.dto.CreateDeliveryPointAddressDTO;
import es.onebox.event.products.dto.CreateDeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointAddressDTO;
import es.onebox.event.products.dto.DeliveryPointDTO;
import es.onebox.event.products.enums.DeliveryPointStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelDeliveryPointRecord;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPointRecordConverter {

    public static CpanelDeliveryPointRecord toRecord(CreateDeliveryPointDTO createDeliveryPointDto) {
        CpanelDeliveryPointRecord productPointRecord = new CpanelDeliveryPointRecord();

        productPointRecord.setEntityid(createDeliveryPointDto.getEntityId().intValue());
        productPointRecord.setName(createDeliveryPointDto.getName());
        toAddressRecord(productPointRecord, createDeliveryPointDto.getLocation());
        productPointRecord.setDeliverypointstatus(DeliveryPointStatus.INACTIVE.getId());
        return productPointRecord;
    }

    public static CpanelDeliveryPointRecord toAddressRecord(CpanelDeliveryPointRecord cpanelDeliveryPointRecord, CreateDeliveryPointAddressDTO createDeliveryPointAddressDto) {

        cpanelDeliveryPointRecord.setCountryid(createDeliveryPointAddressDto.getCountryId().intValue());
        cpanelDeliveryPointRecord.setCity(createDeliveryPointAddressDto.getCity());
        cpanelDeliveryPointRecord.setCountrysubdivisionid(createDeliveryPointAddressDto.getCountrySubdivisionId().intValue());
        cpanelDeliveryPointRecord.setZipcode(createDeliveryPointAddressDto.getZipCode());
        cpanelDeliveryPointRecord.setAddress(createDeliveryPointAddressDto.getAddress());
        return cpanelDeliveryPointRecord;
    }

    public static List<DeliveryPointDTO> toDto(List<DeliveryPointRecord> productDeliveryPointRecords) {
        List<DeliveryPointDTO> productDeliveryPointDTOs = new ArrayList<>();
        productDeliveryPointRecords.forEach(r -> productDeliveryPointDTOs.add(toDto(r)));
        return productDeliveryPointDTOs;
    }

    public static DeliveryPointDTO toDto(DeliveryPointRecord deliveryPointRecord) {
        DeliveryPointDTO productDeliveryPointDTO = new DeliveryPointDTO();
        productDeliveryPointDTO.setId(deliveryPointRecord.getDeliverypointid().longValue());
        productDeliveryPointDTO.setName(deliveryPointRecord.getName());
        productDeliveryPointDTO.setStatus(DeliveryPointStatus.get(deliveryPointRecord.getDeliverypointstatus()));
        productDeliveryPointDTO.setEntity(new IdNameDTO(deliveryPointRecord.getEntityid().longValue(), deliveryPointRecord.getEntityName()));
        productDeliveryPointDTO.setLocation(new DeliveryPointAddressDTO());
        productDeliveryPointDTO.getLocation().setAddress(deliveryPointRecord.getAddress());
        if (deliveryPointRecord.getCountryCode() != null) {
            productDeliveryPointDTO.getLocation().setCountry(new CodeNameDTO(deliveryPointRecord.getCountryCode(), deliveryPointRecord.getCountryName()));
        }
        if (deliveryPointRecord.getCountrySubdivisionCode() != null) {
            productDeliveryPointDTO.getLocation().setCountrySubdivision(new CodeNameDTO(deliveryPointRecord.getCountrySubdivisionCode(), deliveryPointRecord.getCountrySubdivisionName()));
        }
        productDeliveryPointDTO.getLocation().setCity(deliveryPointRecord.getCity());
        productDeliveryPointDTO.getLocation().setZipCode(deliveryPointRecord.getZipcode());
        productDeliveryPointDTO.getLocation().setNotes(deliveryPointRecord.getNotes());
        return productDeliveryPointDTO;
    }
}
