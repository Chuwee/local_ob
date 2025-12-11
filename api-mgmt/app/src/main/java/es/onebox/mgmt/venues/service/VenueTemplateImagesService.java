package es.onebox.mgmt.venues.service;

import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateImageConverter;
import es.onebox.mgmt.venues.dto.UpsertVenueTemplateImageDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateImageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueTemplateImagesService {

    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;

    public VenueTemplateImagesService(VenuesRepository venuesRepository, ValidationService validationService) {
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
    }

    public List<VenueTemplateImageDTO> getVenueTemplateImages(Long venueTemplateId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        return VenueTemplateImageConverter.fromMs(venuesRepository.getVenueTemplateImages(venueTemplateId));
    }

    public VenueTemplateImageDTO upsertVenueTemplateImage(Long venueTemplateId, UpsertVenueTemplateImageDTO body) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        return VenueTemplateImageConverter.fromMs(
                venuesRepository.upsertVenueTemplateImage(venueTemplateId, VenueTemplateImageConverter.toMs(body)));
    }

    public void deleteVenueTemplateImage(Long venueTemplateId, Long imageId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venuesRepository.deleteVenueTemplateImage(venueTemplateId, imageId);
    }
}
