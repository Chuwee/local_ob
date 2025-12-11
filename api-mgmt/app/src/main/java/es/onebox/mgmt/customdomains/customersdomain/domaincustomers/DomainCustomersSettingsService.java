package es.onebox.mgmt.customdomains.customersdomain.domaincustomers;

import es.onebox.mgmt.customdomains.common.converter.DomainSettingsConverter;
import es.onebox.mgmt.customdomains.common.dto.DomainSettingsDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainCustomersSettingsService {

    private final EntitiesRepository entitiesRepository;

    @Autowired
    public DomainCustomersSettingsService(EntitiesRepository entitiesRepository) {
        this.entitiesRepository = entitiesRepository;
    }

    public DomainSettingsDTO get(Long entityId) {
        DomainSettings domainSettings = entitiesRepository.getCustomersDomainSettings(entityId);
        return DomainSettingsConverter.toDTO(domainSettings);
    }

    public void upsert( Long entityId, DomainSettingsDTO body) {
        DomainSettings domainSettings = DomainSettingsConverter.fromDTO(body);
        entitiesRepository.upsertCustomersDomainSettings(entityId, domainSettings);
    }

    public void disable( Long entityId) {
        entitiesRepository.disableCustomersDomainSettings(entityId);
    }
}
