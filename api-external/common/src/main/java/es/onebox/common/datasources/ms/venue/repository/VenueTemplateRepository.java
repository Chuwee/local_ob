package es.onebox.common.datasources.ms.venue.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.venue.MsVenueDatasource;
import es.onebox.common.datasources.ms.venue.dto.BasePriceType;
import es.onebox.common.datasources.ms.venue.dto.BlockingReasonDTO;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeWebCommunicationElementDTO;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeCommunicationElementFilter;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeRequestDTO;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeTicketCommunicationElement;
import es.onebox.common.datasources.ms.venue.dto.SectorDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplates;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplatesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VenueTemplateRepository {

    @Qualifier("msVenueDataSource")
    private final MsVenueDatasource msVenueDatasource;

    @Autowired
    public VenueTemplateRepository(MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    @Cached(key = "getVenueTemplate", expires = 10 * 60)
    public VenueTemplate getVenueTemplate(@CachedArg Long venueTemplateId) {
        return msVenueDatasource.getVenueTemplate(venueTemplateId);
    }

    public VenueTemplates getVenueTemplates(Long operatorId, VenueTemplatesFilter filter) {
        return msVenueDatasource.getVenueTemplates(operatorId, filter);
    }

    @Cached(key = "getPriceTypesByVenueTemplateId", expires = 10 * 60)
    public List<BasePriceType> getPriceTypes(@CachedArg Long venueTemplateId) {
        return msVenueDatasource.getPriceTypes(venueTemplateId);
    }

    @Cached(key = "getSectorsByVenueTemplateId", expires = 10 * 60)
    public List<SectorDTO> getSectors(@CachedArg Long venueTemplateId) {
        return msVenueDatasource.getSectors(venueTemplateId);
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequestDTO requestDTO) {
        msVenueDatasource.updatePriceType(venueTemplateId, priceTypeId, requestDTO);
    }

    public List<PriceTypeTicketCommunicationElement> getPriceTypeTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return msVenueDatasource.getPriceTypeTicketCommunicationElements(venueTemplateId, priceTypeId, filter);
    }

    @Cached(key = "getVenueById", expires = 60 * 60)
    public VenueDTO getVenue(@CachedArg Long venueId) {
        return msVenueDatasource.getVenue(venueId);
    }

    @Cached(key = "getBlockingReasons", expires = 10 * 60)
    public List<BlockingReasonDTO> getBlockingReasons(@CachedArg Long venueTemplateId) {
        return msVenueDatasource.getBlockingReasons(venueTemplateId);
    }

    @Cached(key = "VenueTemplateRepository_getPriceType", expires = 2 * 60)
    public MsPriceTypeDTO getPriceType(@CachedArg Long venueTemplateId, @CachedArg Long priceTypeId) {
        return msVenueDatasource.getPriceType(venueTemplateId, priceTypeId);
    }

    public List<MsPriceTypeWebCommunicationElementDTO> getPriceTypeWebCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return msVenueDatasource.getPriceTypeWebCommunicationElements(venueTemplateId, priceTypeId, filter);
    }
}
