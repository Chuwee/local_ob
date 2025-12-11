package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.ProviderSector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Sector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandler;
import es.onebox.mgmt.externalaccesscontrolhandler.ExternalAccessControlHandlerStrategyProvider;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateSectorConverter;
import es.onebox.mgmt.venues.dto.CloneVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateSectorRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateSectorDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueTemplateSectorsService {

    private final VenuesRepository venuesRepository;
    private final EntitiesRepository entitiesRepository;
    private final ValidationService validationService;
    private final AccessControlSystemsRepository accessControlSystemsRepository;
    private final ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider;

    @Autowired
    public VenueTemplateSectorsService(VenuesRepository venuesRepository, EntitiesRepository entitiesRepository, ValidationService validationService, AccessControlSystemsRepository accessControlSystemsRepository, ExternalAccessControlHandlerStrategyProvider externalAccessControlHandlerStrategyProvider) {
        this.venuesRepository = venuesRepository;
        this.entitiesRepository = entitiesRepository;
        this.validationService = validationService;
        this.accessControlSystemsRepository = accessControlSystemsRepository;
        this.externalAccessControlHandlerStrategyProvider = externalAccessControlHandlerStrategyProvider;
    }

    public List<VenueTemplateSectorDTO> getSectors(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        List<Sector> sectors = venuesRepository.getSectors(venueTemplateId);
        return sectors.stream().map(VenueTemplateSectorConverter::fromMsVenue).collect(Collectors.toList());
    }


    public VenueTemplateSectorDTO getSector(Long venueTemplateId, Long sectorId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        Sector sector = venuesRepository.getSector(venueTemplateId, sectorId);

        return VenueTemplateSectorConverter.fromMsVenue(sector);
    }

    public Long createSector(Long venueTemplateId, CreateVenueTemplateSectorRequestDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        ExternalConfig externalConfig = entitiesRepository.getExternalConfig(venueTemplate.getEntityId());
        if (venueTemplate.getInventoryProvider() != null && BooleanUtils.isTrue(externalConfig.getSectorsValidation()) &&
            InventoryProviderEnum.getByCode(venueTemplate.getInventoryProvider()) != null) {
            if (requestDTO.getCode() == null || requestDTO.getCode().isEmpty()) {
                throw new OneboxRestException(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SECTOR_CODE_REQUIRED);
            } else {
                ProviderSector providerSector = venuesRepository.getProviderSector(InventoryProviderEnum.getByCode(venueTemplate.getInventoryProvider()).name(), requestDTO.getCode());
                if (providerSector == null) {
                    throw new OneboxRestException(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SECTOR_CODE_NOT_ALLOWED);
                }
            }
        }
        return venuesRepository.createSector(venueTemplateId, VenueTemplateSectorConverter.toMs(requestDTO));
    }

    public Long cloneSector(Long venueTemplateId, Long sectorId, CloneVenueTemplateSectorRequestDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        Long newSectorId = venuesRepository.cloneSector(venueTemplateId, sectorId, VenueTemplateSectorConverter.toMs(requestDTO));

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null
                && venueTemplate.getVenue() != null && venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }

        return newSectorId;
    }

    public void updateSector(Long venueTemplateId, Long sectorId, UpdateVenueTemplateSectorRequestDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        ExternalConfig externalConfig = entitiesRepository.getExternalConfig(venueTemplate.getEntityId());
        if (venueTemplate.getInventoryProvider() != null && BooleanUtils.isTrue(externalConfig.getSectorsValidation()) &&
                InventoryProviderEnum.getByCode(venueTemplate.getInventoryProvider()) != null) {
            if (requestDTO.getCode() == null || requestDTO.getCode().isEmpty()) {
                throw new OneboxRestException(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SECTOR_CODE_REQUIRED);
            } else {
                ProviderSector providerSector = venuesRepository.getProviderSector(InventoryProviderEnum.getByCode(venueTemplate.getInventoryProvider()).name(), requestDTO.getCode());
                if (providerSector == null) {
                    throw new OneboxRestException(ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SECTOR_CODE_NOT_ALLOWED);
                }
            }
        }

        venuesRepository.updateSector(venueTemplateId, sectorId, VenueTemplateSectorConverter.toMs(requestDTO));

        if (venueTemplate.getEventId() != null && venueTemplate.getEntityId() != null
                && venueTemplate.getVenue() != null && venueTemplate.getVenue().getId() != null) {
            checkAndProcessExternalVenueTemplates(venueTemplate.getEntityId(), venueTemplate.getVenue().getId(), venueTemplateId);
        }
    }

    public void deleteSector(Long venueTemplateId, Long sectorId) {
        validationService.getAndCheckWriteVenueTemplate(venueTemplateId);

        venuesRepository.deleteSector(venueTemplateId, sectorId);
    }


    private void checkAndProcessExternalVenueTemplates(Long entityId ,Long venueId, Long venueTemplateId) {
        if (venueTemplateId == null) {
            return;
        }

        List<AccessControlSystem> venueAccessControlSystems = accessControlSystemsRepository.findByVenueIdCached(venueId);

        if (CollectionUtils.isNotEmpty(venueAccessControlSystems)) {
            venueAccessControlSystems.stream().distinct().forEach(accessControlSystem -> {
                ExternalAccessControlHandler externalAccessControlHandler;
                externalAccessControlHandler = externalAccessControlHandlerStrategyProvider.provide(accessControlSystem.name());

                if (externalAccessControlHandler == null) {
                    return;
                }

                externalAccessControlHandler.addOrUpdateVenueElements(entityId, venueTemplateId);
            });
        }
    }
}
