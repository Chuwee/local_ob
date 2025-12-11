package es.onebox.mgmt.venues.service;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateViewLinkConverter;
import es.onebox.mgmt.venues.dto.CreateViewLinkDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VenueTemplateViewLinksService {

    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;

    @Autowired
    public VenueTemplateViewLinksService(VenuesRepository venuesRepository, ValidationService validationService) {
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
    }

    public IdDTO createVenueTemplateViewLink(Long venueTemplateId, Integer viewId, CreateViewLinkDTO body) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        return new IdDTO(venuesRepository.createVenueTemplateViewLink(venueTemplateId, viewId, VenueTemplateViewLinkConverter.toMs(body)));
    }

    public void deleteVenueTemplateViewLink(Long venueTemplateId, Long viewId, Long linkId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.deleteVenueTemplateViewLink(venueTemplateId, viewId, linkId);
    }
}
