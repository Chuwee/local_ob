package es.onebox.mgmt.venues.service;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.RowDetail;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTemplateRowConverter;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateRowDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateRowsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateRowDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateRowsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateRowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueTemplateRowsService {

    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;

    @Autowired
    public VenueTemplateRowsService(VenuesRepository venuesRepository, ValidationService validationService) {
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
    }

    public VenueTemplateRowDTO getVenueTemplateRow(Long venueTemplateId, Integer rowId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        RowDetail row = venuesRepository.getVenueTemplateRow(venueTemplateId, rowId);

        return VenueTemplateRowConverter.fromMsVenue(row);
    }

    public Long createVenueTemplateRow(Long venueTemplateId, CreateVenueTemplateRowDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        return venuesRepository.createVenueTemplateRow(venueTemplateId, VenueTemplateRowConverter.toMs(requestDTO));
    }

    public List<IdDTO> createVenueTemplateRows(Long venueTemplateId, CreateVenueTemplateRowsDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        return venuesRepository.createVenueTemplateRows(venueTemplateId, VenueTemplateRowConverter.toMs(requestDTO));
    }

    public void updateVenueTemplateRow(Long venueTemplateId, Long rowId, UpdateVenueTemplateRowDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        venuesRepository.updateVenueTemplateRow(venueTemplateId, rowId, VenueTemplateRowConverter.toMs(requestDTO));
    }

    public void updateVenueTemplateRows(Long venueTemplateId, UpdateVenueTemplateRowsDTO requestDTO) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        venuesRepository.updateVenueTemplateRows(venueTemplateId, VenueTemplateRowConverter.toMs(requestDTO));
    }

    public void deleteVenueTemplateRow(Long venueTemplateId, Long rowId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);

        venuesRepository.deleteVenueTemplateRow(venueTemplateId, rowId);
    }
}
