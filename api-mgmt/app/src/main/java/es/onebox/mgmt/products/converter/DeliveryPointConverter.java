package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateDeliveryPointAddress;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPointAddress;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchDeliveryPointFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateDeliveryPointAddress;
import es.onebox.mgmt.products.dto.CreateDeliveryPointAddressDTO;
import es.onebox.mgmt.products.dto.CreateDeliveryPointDTO;
import es.onebox.mgmt.products.dto.DeliveryPointAddressDTO;
import es.onebox.mgmt.products.dto.DeliveryPointDTO;
import es.onebox.mgmt.products.dto.DeliveryPointsDTO;
import es.onebox.mgmt.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.mgmt.products.dto.UpdateDeliveryPointAddressDTO;
import es.onebox.mgmt.products.dto.UpdateDeliveryPointDTO;
import es.onebox.mgmt.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPointConverter {

    private DeliveryPointConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static CreateDeliveryPoint convert(CreateDeliveryPointDTO dto, MasterdataValue country, MasterdataValue countrySubdivision) {
        CreateDeliveryPoint createDeliveryPoint = new CreateDeliveryPoint();
        createDeliveryPoint.setEntityId(dto.getEntityId());
        createDeliveryPoint.setName(dto.getName());
        createDeliveryPoint.setLocation(convertAddress(dto.getLocation(), country, countrySubdivision));
        return createDeliveryPoint;
    }

    public static CreateDeliveryPointAddress convertAddress(CreateDeliveryPointAddressDTO source, MasterdataValue country, MasterdataValue countrySubdivision) {
        CreateDeliveryPointAddress target = new CreateDeliveryPointAddress();
        target.setCountryId(country != null ? country.getId() : null);
        target.setCountrySubdivisionId(countrySubdivision != null ? countrySubdivision.getId() : null);
        target.setCity(source.getCity());
        target.setAddress(source.getAddress());
        target.setZipCode(source.getZipCode());
        return target;
    }

    public static DeliveryPointDTO toDto(DeliveryPoint deliveryPoint) {
        DeliveryPointDTO productDeliveryPointDTO = new DeliveryPointDTO();
        productDeliveryPointDTO.setId(deliveryPoint.getId());
        productDeliveryPointDTO.setEntity(deliveryPoint.getEntity());
        productDeliveryPointDTO.setName(deliveryPoint.getName());
        productDeliveryPointDTO.setLocation(toAddressDto(deliveryPoint.getLocation()));
        productDeliveryPointDTO.setStatus(deliveryPoint.getStatus());
        return productDeliveryPointDTO;
    }

    public static DeliveryPointsDTO toDtoList(DeliveryPoints deliveryPoints) {
        DeliveryPointsDTO deliveryPointsDTO = new DeliveryPointsDTO();
        List<DeliveryPointDTO> deliveryPointDTOs = new ArrayList<>();
        if (deliveryPoints.getData() != null) {
            for(DeliveryPoint deliveryPoint : deliveryPoints.getData()) {
                deliveryPointDTOs.add(toDto(deliveryPoint));
            }
        }
        deliveryPointsDTO.setData(deliveryPointDTOs);
        deliveryPointsDTO.setMetadata(deliveryPoints.getMetadata());
        return deliveryPointsDTO;
    }

    public static UpdateDeliveryPoint toEntity(UpdateDeliveryPointDTO updateDeliveryPointDTO, MasterdataValue country, MasterdataValue countrySubdivision) {
        UpdateDeliveryPoint updateDeliveryPoint = new UpdateDeliveryPoint();
        updateDeliveryPoint.setName(updateDeliveryPointDTO.getName());
        updateDeliveryPoint.setStatus(updateDeliveryPointDTO.getStatus());
        if (updateDeliveryPointDTO.getLocation() != null) {
            updateDeliveryPoint.setLocation(toAddressEntity(updateDeliveryPointDTO.getLocation(), country, countrySubdivision));
        }
        return updateDeliveryPoint;
    }

    public static DeliveryPointAddressDTO toAddressDto(DeliveryPointAddress source) {
        DeliveryPointAddressDTO target = new DeliveryPointAddressDTO();
        target.setCountry(source.getCountry());
        target.setCountrySubdivision(source.getCountrySubdivision());
        target.setCity(source.getCity());
        target.setAddress(source.getAddress());
        target.setZipCode(source.getZipCode());
        target.setNotes(source.getNotes());
        return target;
    }

    public static UpdateDeliveryPointAddress toAddressEntity(UpdateDeliveryPointAddressDTO source, MasterdataValue country, MasterdataValue countrySubdivision) {
        UpdateDeliveryPointAddress target = new UpdateDeliveryPointAddress();
        target.setCountryId(country != null ? country.getId() : null);
        target.setCountrySubdivisionId(countrySubdivision != null ? countrySubdivision.getId() : null);
        target.setCity(source.getCity());
        target.setAddress(source.getAddress());
        target.setZipCode(source.getZipCode());
        target.setNotes(source.getNotes());
        return target;
    }

    public static SearchDeliveryPointFilter convertFilter(SearchDeliveryPointFilterDTO searchDeliveryPointFilterDTO, List<Long> entityIds) {
        SearchDeliveryPointFilter searchDeliveryPointFilter = new SearchDeliveryPointFilter();
        searchDeliveryPointFilter.setOperatorId(SecurityUtils.getUserOperatorId());
        searchDeliveryPointFilter.setEntityIds(entityIds);
        searchDeliveryPointFilter.setName(searchDeliveryPointFilterDTO.getQ());
        searchDeliveryPointFilter.setCountrySubdivision(searchDeliveryPointFilterDTO.getCountrySubdivision());
        searchDeliveryPointFilter.setCountry(searchDeliveryPointFilterDTO.getCountry());
        searchDeliveryPointFilter.setStatus(searchDeliveryPointFilterDTO.getStatus());
        searchDeliveryPointFilter.setOffset(searchDeliveryPointFilterDTO.getOffset());
        searchDeliveryPointFilter.setLimit(searchDeliveryPointFilterDTO.getLimit());
        searchDeliveryPointFilter.setSort(searchDeliveryPointFilterDTO.getSort());
        return searchDeliveryPointFilter;
    }
}
