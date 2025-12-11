package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalVenuesService {

    private final EntitiesRepository entitiesRepository;
    private final VenuesRepository venuesRepository;

    @Autowired
    public ExternalVenuesService(EntitiesRepository entitiesRepository, VenuesRepository venuesRepository) {
        this.entitiesRepository = entitiesRepository;
        this.venuesRepository = venuesRepository;
    }


    public List<IdNameCodeDTO> getExternalVenues(Long entityId) {
        Entity entity = entitiesRepository.getEntity(entityId);
        if (entity == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        }
        if(entity.getInventoryProviders() == null || entity.getInventoryProviders().size() != 1) {
            return new ArrayList<>();
        }
        return venuesRepository.getProviderVenues(entity.getInventoryProviders().get(0).name());
    }

    public List<IdNameCodeDTO> getExternalVenueTemplates(Long entityId, Long externalVenueId) {
        Entity entity = entitiesRepository.getEntity(entityId);
        if (entity == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        }
        if(entity.getInventoryProviders() == null || entity.getInventoryProviders().size() != 1) {
            return new ArrayList<>();
        }
        return venuesRepository.getProviderVenueTemplates(entity.getInventoryProviders().get(0).name(), externalVenueId);
    }

}
