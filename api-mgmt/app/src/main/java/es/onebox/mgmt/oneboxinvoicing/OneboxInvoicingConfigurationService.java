package es.onebox.mgmt.oneboxinvoicing;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntities;
import es.onebox.mgmt.datasources.ms.entity.repository.OneboxInvoicingRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.events.dto.EventSearchFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.converters.OneboxInvoiceEntitiesConverter;
import es.onebox.mgmt.oneboxinvoicing.dto.CreateOneboxInvoiceEntityRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.EntityInvoiceConfigurationSearchFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntitiesDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEventsResponse;
import es.onebox.mgmt.oneboxinvoicing.dto.UpdateOneboxInvoiceEntityRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OneboxInvoicingConfigurationService {

    private final OneboxInvoicingRepository oneboxInvoicingRepository;

    @Autowired
    public OneboxInvoicingConfigurationService(OneboxInvoicingRepository oneboxInvoicingRepository) {
        this.oneboxInvoicingRepository = oneboxInvoicingRepository;
    }

    public OneboxInvoiceEntitiesDTO getEntitiesInvoiceConfiguration(EntityInvoiceConfigurationSearchFilterDTO filterDTO) {
        OneboxInvoiceEntities entities =
                oneboxInvoicingRepository.getEntitiesInvoiceConfiguration(OneboxInvoiceEntitiesConverter.toMs(filterDTO));
        return OneboxInvoiceEntitiesConverter.toDTO(entities);
    }

    public void createEntityInvoiceConfiguration(Long entityId, CreateOneboxInvoiceEntityRequestDTO request) {
        oneboxInvoicingRepository.createEntityInvoiceConfiguration(entityId, OneboxInvoiceEntitiesConverter.toMs(request));
    }

    public void updateEntityInvoiceConfiguration(Long entityId, OneboxInvoiceType type, UpdateOneboxInvoiceEntityRequestDTO request) {
        oneboxInvoicingRepository.updateEntityInvoiceConfiguration(entityId,
                OneboxInvoiceEntitiesConverter.toMs(type),
                OneboxInvoiceEntitiesConverter.toMs(request));
    }

    public OneboxInvoiceEventsResponse getEvents(Long entityId, EventSearchFilterDTO filter) {
        EventSearchFilter eventSearchFilter = new EventSearchFilter();
        OneboxInvoiceEventsResponse response = new OneboxInvoiceEventsResponse();

        eventSearchFilter.setEntityId(entityId);
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            eventSearchFilter.setStatus(filter.getStatus().stream().map(Enum::toString).toList());
        }
        eventSearchFilter.setFreeSearch(filter.getFreeSearch());
        eventSearchFilter.setOffset(filter.getOffset());
        eventSearchFilter.setLimit(filter.getLimit());
        var events = oneboxInvoicingRepository.getEvents(eventSearchFilter);
        response.setData(events.getData().stream().map(e -> {
            IdNameDTO dto = new IdNameDTO();
            dto.setId(e.getId());
            dto.setName(e.getName());
            return dto;
        }).toList());

        response.setMetadata(events.getMetadata());

        return response;
    }

}
