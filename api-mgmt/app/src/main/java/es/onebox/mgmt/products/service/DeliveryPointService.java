package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchDeliveryPointFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.DeliveryPointConverter;
import es.onebox.mgmt.products.dto.CreateDeliveryPointDTO;
import es.onebox.mgmt.products.dto.DeliveryPointDTO;
import es.onebox.mgmt.products.dto.DeliveryPointsDTO;
import es.onebox.mgmt.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.mgmt.products.dto.UpdateDeliveryPointDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DeliveryPointService {
    private final SecurityManager securityManager;
    private final ProductsRepository productsRepository;
    private final MasterdataRepository masterdataRepository;

    @Autowired
    public DeliveryPointService(SecurityManager securityManager, ProductsRepository productsRepository, MasterdataRepository masterdataRepository) {
        this.securityManager = securityManager;
        this.productsRepository = productsRepository;
        this.masterdataRepository = masterdataRepository;
    }

    public Long createDeliveryPoint(CreateDeliveryPointDTO createDeliveryPointDTO) {
        securityManager.checkEntityAccessible(createDeliveryPointDTO.getEntityId());
        MasterdataValue country = null;
        MasterdataValue countrySubdivision = null;
        if(createDeliveryPointDTO.getLocation() != null && createDeliveryPointDTO.getLocation().getCountry() != null) {
            List<MasterdataValue> countries = masterdataRepository.getCountries(createDeliveryPointDTO.getLocation().getCountry(), null);
            if(countries == null || countries.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.COUNTRY_NOT_FOUND);
            }
            country = countries.get(0);
        }
        if(createDeliveryPointDTO.getLocation() != null && createDeliveryPointDTO.getLocation().getCountrySubdivision() != null) {
            List<MasterdataValue> countrieSubdivisions = masterdataRepository.getCountrySubdivisions(createDeliveryPointDTO.getLocation().getCountrySubdivision());
            if(countrieSubdivisions == null || countrieSubdivisions.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.COUNTRY_SUBDIVISION_NOT_FOUND);
            }
            countrySubdivision = countrieSubdivisions.get(0);
        }
        CreateDeliveryPoint createDeliveryPoint = DeliveryPointConverter.convert(createDeliveryPointDTO, country, countrySubdivision);
        return productsRepository.createDeliveryPoint(createDeliveryPoint);
    }

    public DeliveryPointDTO getDeliveryPoint(Long deliveryPointId) {
        DeliveryPoint deliveryPoint = productsRepository.getDeliveryPoint(deliveryPointId);
        securityManager.checkEntityAccessible(deliveryPoint.getEntity().getId());
        return DeliveryPointConverter.toDto(deliveryPoint);
    }

    public DeliveryPointsDTO searchDeliveryPoint(SearchDeliveryPointFilterDTO searchDeliveryPointFilter) {
        securityManager.checkEntityAccessible(searchDeliveryPointFilter);
        List<Long> entityIds = new ArrayList<>();
        if(!SecurityUtils.isOperatorEntity()) {
            if (searchDeliveryPointFilter.getEntityId() != null) {
                securityManager.checkEntityAccessible(searchDeliveryPointFilter.getEntityId());
                entityIds.add(searchDeliveryPointFilter.getEntityId());
            } else {
                entityIds.add(SecurityUtils.getUserEntityId());
            }
        } else {
            entityIds.add(searchDeliveryPointFilter.getEntityId());
        }
        SearchDeliveryPointFilter filter = DeliveryPointConverter.convertFilter(searchDeliveryPointFilter, entityIds);
        DeliveryPoints deliveryPoints = productsRepository.searchDeliveryPoint(filter);
        return DeliveryPointConverter.toDtoList(deliveryPoints);
    }

    public void deleteDeliveryPoint(Long deliveryPointId) {
        DeliveryPoint deliveryPoint = productsRepository.getDeliveryPoint(deliveryPointId);
        if (Objects.nonNull(deliveryPoint)) {
            securityManager.checkEntityAccessible(deliveryPoint.getEntity().getId());
            productsRepository.deleteDeliveryPoint(deliveryPointId);
        }
    }

    public DeliveryPointDTO updateDeliveryPoint(Long deliveryPointId, UpdateDeliveryPointDTO updateDeliveryPointDTO) {
        DeliveryPoint deliveryPoint = productsRepository.getDeliveryPoint(deliveryPointId);
        if (deliveryPoint == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(deliveryPoint.getEntity().getId());
        MasterdataValue country = null;
        MasterdataValue countrySubdivision = null;
        if(updateDeliveryPointDTO.getLocation() != null && updateDeliveryPointDTO.getLocation().getCountry() != null) {
            List<MasterdataValue> countries = masterdataRepository.getCountries(updateDeliveryPointDTO.getLocation().getCountry(), null);
            if(countries == null || countries.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.COUNTRY_NOT_FOUND);
            }
            country = countries.get(0);
        }
        if(updateDeliveryPointDTO.getLocation() != null && updateDeliveryPointDTO.getLocation().getCountrySubdivision() != null) {
            List<MasterdataValue> countrieSubdivisions = masterdataRepository.getCountrySubdivisions(updateDeliveryPointDTO.getLocation().getCountrySubdivision());
            if(countrieSubdivisions == null || countrieSubdivisions.isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.COUNTRY_SUBDIVISION_NOT_FOUND);
            }
            countrySubdivision = countrieSubdivisions.get(0);
        }

        UpdateDeliveryPoint updateDeliveryPoint = DeliveryPointConverter.toEntity(updateDeliveryPointDTO, country, countrySubdivision);
        deliveryPoint = productsRepository.updateDeliveryPoint(deliveryPointId, updateDeliveryPoint);
        return DeliveryPointConverter.toDto(deliveryPoint);
    }

}
