package es.onebox.event.datasources.ms.venue.repository;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.datasources.ms.venue.MsVenueDatasource;
import es.onebox.event.datasources.ms.venue.dto.CapacityMapDTO;
import es.onebox.event.datasources.ms.venue.dto.Gate;
import es.onebox.event.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.event.datasources.ms.venue.dto.PriceTypeRequest;
import es.onebox.event.datasources.ms.venue.dto.QuotaDTO;
import es.onebox.event.datasources.ms.venue.dto.SectorDTO;
import es.onebox.event.datasources.ms.venue.dto.UpdateVenueTemplateRequest;
import es.onebox.event.datasources.ms.venue.dto.Venue;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplateStatus;
import es.onebox.event.datasources.ms.venue.dto.VenueTemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class VenuesRepository {

    private final MsVenueDatasource msVenueDatasource;

    @Autowired
    public VenuesRepository(MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    public void upsertPriceTypeCommElements(Long venueTemplateId, Long priceTypeId,
                                            List<PriceTypeCommunicationElement> commElements) {
        msVenueDatasource.upsertPriceTypeCommElements(venueTemplateId, priceTypeId, commElements);
    }

    public void deletePriceTypeCommElements(Long venueTemplateId, Long priceTypeId) {
        msVenueDatasource.deletePriceTypeCommElements(venueTemplateId, priceTypeId);
    }

    public Venue getVenue(Long venueId) {
        return msVenueDatasource.getVenue(venueId);
    }

    public VenueTemplate getVenueTemplate(Long venueTemplateId) {
        return msVenueDatasource.getVenueTemplate(venueTemplateId);
    }

    public List<Gate> getGates(Long venueTemplateId) {
        return msVenueDatasource.getGates(venueTemplateId);
    }

    public CapacityMapDTO getCapacityMap(Long sessionId) {
        return msVenueDatasource.getVenueCapacityMap(sessionId);
    }

    public List<QuotaDTO> getQuotas(Long venueTemplateId) {
        return msVenueDatasource.getQuotas(venueTemplateId);
    }

    public Long createVenueTemplate(String name, Long eventId, Long venueId, Long spaceId, Long entityId,
                                    VenueTemplateType type, Boolean smartBooking) {
        return msVenueDatasource.createVenueTemplate(name, eventId, venueId, spaceId, entityId, type, smartBooking);
    }

    public void deleteVenueTemplate(Long venueTemplateId) {
        UpdateVenueTemplateRequest request = new UpdateVenueTemplateRequest();
        request.setStatus(VenueTemplateStatus.DELETED);
        msVenueDatasource.updateVenueTemplate(venueTemplateId, request);
    }

    public List<IdNameCodeDTO> getPriceTypes(Long venueTemplateId) {
        return msVenueDatasource.getPriceTypes(venueTemplateId);
    }

    public Long createPriceType(Long venueTemplateId, PriceTypeRequest request) {
        return msVenueDatasource.createPriceType(venueTemplateId, request);
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequest request) {
        msVenueDatasource.updatePriceType(venueTemplateId, priceTypeId, request);
    }

    public List<SectorDTO> getSectorsByTemplateId(Integer venueTemplateId) {
        return msVenueDatasource.getSectorsByTemplateId(venueTemplateId);
    }
}
