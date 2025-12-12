package es.onebox.common.service;

import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplates;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplatesFilter;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VenueTemplateValidationService {

    private VenueTemplateRepository venueTemplateRepository;

    @Autowired
    public VenueTemplateValidationService(final VenueTemplateRepository venueTemplateRepository){
        this.venueTemplateRepository = venueTemplateRepository;
    }

    public void validate(Long entityId, Long eventId, Long venueTemplateId){
        VenueTemplatesFilter filter = new VenueTemplatesFilter();
        filter.setEntityId(entityId);
        filter.setEventId(eventId);

        VenueTemplates venueTemplates = venueTemplateRepository.getVenueTemplates(AuthenticationService.getOperatorId(),filter);
        validateTotal(venueTemplates);
        validateVenueTemplate(venueTemplates.getData(), venueTemplateId);
    }

    private void validateVenueTemplate(List<VenueTemplate> venueTemplates, Long venueTemplateId) {
        venueTemplates.stream()
                .filter(vt -> vt.getId().longValue() == venueTemplateId.longValue())
                .findAny()
                .orElseThrow(() -> ExceptionBuilder.build(ApiExternalErrorCode.VENUE_TEMPLATE_NOT_FOUND));
    }

    private void validateTotal(BaseResponseCollection response){
        if(Objects.isNull(response) || response.getMetadata().getTotal().longValue() == 0){
            throw ExceptionBuilder.build(ApiExternalErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        }
    }
}
