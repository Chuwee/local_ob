package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.entity.dto.CountryDTO;
import es.onebox.event.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.DeliveryPointRecordConverter;
import es.onebox.event.products.dao.DeliveryPointDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.dto.CreateDeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointsDTO;
import es.onebox.event.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.event.products.dto.UpdateDeliveryPointDTO;
import es.onebox.event.products.enums.DeliveryPointStatus;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelDeliveryPointRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DeliveryPointService {

    private final EntitiesRepository entitiesRepository;
    private final DeliveryPointDao deliveryPointDao;
    private final RefreshDataService refreshDataService;
    private final ProductSessionDao productSessionDao;

    @Autowired
    public DeliveryPointService(DeliveryPointDao deliveryPointDao,
                                EntitiesRepository entitiesRepository,
                                RefreshDataService refreshDataService,
                                ProductSessionDao productSessionDao) {
        this.deliveryPointDao = deliveryPointDao;
        this.entitiesRepository = entitiesRepository;
        this.refreshDataService = refreshDataService;
        this.productSessionDao = productSessionDao;
    }

    @MySQLWrite
    public Long createDeliveryPoint(CreateDeliveryPointDTO createDeliveryPointDTO) {
        validateCreation(createDeliveryPointDTO);

        CpanelDeliveryPointRecord cpanelDeliveryPointRecord = DeliveryPointRecordConverter.toRecord(createDeliveryPointDTO);
        cpanelDeliveryPointRecord.setDeliverypointstatus(DeliveryPointStatus.ACTIVE.getId());
        CpanelDeliveryPointRecord result = deliveryPointDao.insert(cpanelDeliveryPointRecord);
        return result.getDeliverypointid().longValue();
    }

    @MySQLRead
    public DeliveryPointDTO getDeliveryPoint(Long deliveryPointId) {
        List<DeliveryPointRecord> productDeliveryPoints = deliveryPointDao.getProductDeliveryPoints(null, deliveryPointId);
        if (productDeliveryPoints == null || productDeliveryPoints.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND);
        }
        return DeliveryPointRecordConverter.toDto(productDeliveryPoints.get(0));
    }

    @MySQLRead
    public DeliveryPointsDTO searchDeliveryPoint(SearchDeliveryPointFilterDTO searchDeliveryPointFilterDTO) {
        DeliveryPointsDTO productDeliveryPointsDTO = new DeliveryPointsDTO();
        List<DeliveryPointRecord> productDeliveryPoints = deliveryPointDao.getProductDeliveryPoints(searchDeliveryPointFilterDTO, null);
        if (productDeliveryPoints != null) {
            productDeliveryPointsDTO.setData(DeliveryPointRecordConverter.toDto(productDeliveryPoints));
            Long total = deliveryPointDao.getTotalProductDeliveryPoints(searchDeliveryPointFilterDTO, null);
            productDeliveryPointsDTO.setMetadata(MetadataBuilder.build(searchDeliveryPointFilterDTO, total));
            return productDeliveryPointsDTO;
        }
        return null;
    }

    @MySQLWrite
    public void deleteDeliveryPoint(Long deliveryPointId) {
        CpanelDeliveryPointRecord cpanelDeliveryPointRecord = deliveryPointDao.findById(deliveryPointId.intValue());
        if (cpanelDeliveryPointRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND);
        }
        if (!cpanelDeliveryPointRecord.getDeliverypointstatus().equals(DeliveryPointStatus.DELETED.getId())) {
            cpanelDeliveryPointRecord.setDeliverypointstatus(DeliveryPointStatus.DELETED.getId());
            deliveryPointDao.update(cpanelDeliveryPointRecord);
        }
    }

    @MySQLWrite
    public DeliveryPointDTO updateDeliveryPoint(Long deliveryPointId, UpdateDeliveryPointDTO updateDeliveryPointDTO) {
        CpanelDeliveryPointRecord cpanelDeliveryPointRecord = deliveryPointDao.findById(deliveryPointId.intValue());
        if (cpanelDeliveryPointRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND);
        }
        if (updateDeliveryPointDTO.getName() != null) {
            cpanelDeliveryPointRecord.setName(updateDeliveryPointDTO.getName());
        }
        if (updateDeliveryPointDTO.getStatus() != null) {
            cpanelDeliveryPointRecord.setDeliverypointstatus(updateDeliveryPointDTO.getStatus().getId());
        }
        if (updateDeliveryPointDTO.getLocation() != null) {
            if (updateDeliveryPointDTO.getLocation().getAddress() != null) {
                cpanelDeliveryPointRecord.setAddress(updateDeliveryPointDTO.getLocation().getAddress());
            }
            if (updateDeliveryPointDTO.getLocation().getCity() != null) {
                cpanelDeliveryPointRecord.setCity(updateDeliveryPointDTO.getLocation().getCity());
            }
            if (updateDeliveryPointDTO.getLocation().getCountryId() != null) {
                cpanelDeliveryPointRecord.setCountryid(updateDeliveryPointDTO.getLocation().getCountryId().intValue());
            }
            if (updateDeliveryPointDTO.getLocation().getCountrySubdivisionId() != null) {
                cpanelDeliveryPointRecord.setCountrysubdivisionid(updateDeliveryPointDTO.getLocation().getCountrySubdivisionId().intValue());
            }
            if (updateDeliveryPointDTO.getLocation().getZipCode() != null) {
                cpanelDeliveryPointRecord.setZipcode(updateDeliveryPointDTO.getLocation().getZipCode());
            }
            if (updateDeliveryPointDTO.getLocation().getNotes() != null) {
                cpanelDeliveryPointRecord.setNotes(updateDeliveryPointDTO.getLocation().getNotes());
            }
        }
        deliveryPointDao.update(cpanelDeliveryPointRecord);
        List<DeliveryPointRecord> productDeliveryPoints = deliveryPointDao.getProductDeliveryPoints(null, deliveryPointId);
        postUpdateDeliveryPoint(deliveryPointId);
        return DeliveryPointRecordConverter.toDto(productDeliveryPoints.get(0));
    }

    private void validateCreation(CreateDeliveryPointDTO createProductDeliveryPointDTO) {
        EntityDTO entity = entitiesRepository.getEntity(createProductDeliveryPointDTO.getEntityId().intValue());
        if (entity == null) {
            throw new OneboxRestException(MsEventErrorCode.ENTITY_NOT_FOUND);
        }

        CountryDTO countryDTO = entitiesRepository.getCountry(createProductDeliveryPointDTO.getLocation().getCountryId().intValue());
        if (countryDTO == null) {
            throw new OneboxRestException(MsEventErrorCode.COUNTRY_NOT_FOUND);
        }

        CountrySubdivisionDTO countrySubdivisionDTO = entitiesRepository.getCountrySubdivision(createProductDeliveryPointDTO.getLocation().getCountrySubdivisionId().intValue());
        if (countrySubdivisionDTO == null) {
            throw new OneboxRestException(MsEventErrorCode.COUNTRY_SUBDIVISION_NOT_FOUND);
        }

        if (createProductDeliveryPointDTO.getLocation().getZipCode() != null && !createProductDeliveryPointDTO.getLocation().getZipCode().startsWith(countrySubdivisionDTO.getZipCode())) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_POSTAL_CODE_COUNTRY_SUBDIVISION);
        }

        List<CpanelDeliveryPointRecord> currentDeliveryPoints = deliveryPointDao.findDeliveryPoints(createProductDeliveryPointDTO.getEntityId(), createProductDeliveryPointDTO.getName());
        if (currentDeliveryPoints != null && !currentDeliveryPoints.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.DELIVERY_POINT_NAME_ALREADY_IN_USE);
        }
    }

    private void postUpdateDeliveryPoint(Long deliveryPointId) {
        // Update roduct catalogs delivery points
        Set<Integer> productIds = productSessionDao.findPublishedDeliveryProducts(deliveryPointId);
        if (productIds != null) {
            for (Integer productId : productIds) {
                refreshDataService.refreshProduct(productId.longValue());
            }
        }
    }
}
