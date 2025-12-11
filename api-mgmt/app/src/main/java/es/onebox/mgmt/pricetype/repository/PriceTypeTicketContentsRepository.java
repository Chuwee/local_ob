package es.onebox.mgmt.pricetype.repository;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.common.dto.PriceTypeCommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.venue.MsVenueDatasource;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketCommunicationElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class PriceTypeTicketContentsRepository {

    private final MsVenueDatasource msVenueDatasource;

    @Autowired
    public PriceTypeTicketContentsRepository(final MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    public List<PriceTypeTicketCommunicationElement> findPriceTypePdfCommunicationElements(final Long venueTemplateId, final Long priceTypeId, final PriceTypeCommunicationElementFilter filter) {
        return this.msVenueDatasource.getPriceTypePdfTicketCommunicationElements(venueTemplateId, priceTypeId, filter);
    }

    public List<PriceTypeTicketCommunicationElement> findPriceTypePrinterCommunicationElements(final Long venueTemplateId, final Long priceTypeId, final PriceTypeCommunicationElementFilter filter) {
        return this.msVenueDatasource.getPriceTypePrinterTicketCommunicationElements(venueTemplateId, priceTypeId, filter);
    }

    public void updatePriceTypePdfCommunicationElements(final Long venueTemplateId, final Long priceTypeId, final Set<PriceTypeTicketCommunicationElement> elements) {
        this.msVenueDatasource.updatePriceTypePdfTicketCommunicationElements(venueTemplateId, priceTypeId, elements);
    }

    public void updatePriceTypePrinterCommunicationElements(final Long venueTemplateId, final Long priceTypeId, final Set<PriceTypeTicketCommunicationElement> elements) {
        this.msVenueDatasource.updatePriceTypePrinterTicketCommunicationElements(venueTemplateId, priceTypeId, elements);
    }

    public void deletePriceTypePdfCommunicationElementImage(final Long venueTemplateId, final Long priceTypeId, String language, String type) {
        this.msVenueDatasource.deletePriceTypePdfTicketCommunicationElement(venueTemplateId, priceTypeId, language, type);
    }

    public void deletePriceTypePrinterCommunicationElementImage(final Long venueTemplateId, final Long priceTypeId, String language, String type) {
        this.msVenueDatasource.deletePriceTypePrinterTicketCommunicationElement(venueTemplateId, priceTypeId, language, type);
    }

    public VenueTemplate getVenueTemplate(final Long venueTemplateId) {
        return this.msVenueDatasource.getVenueTemplate(venueTemplateId);
    }

    public List<PriceType> getPriceTypes(Long venueTemplateId) {
        return this.msVenueDatasource.getPriceTypes(venueTemplateId);
    }

    public List<PriceTypeTicketCommunicationElement> findPriceTypePassbookCommunicationElements(final Long venueTemplateId, final Long priceTypeId, final PriceTypeCommunicationElementFilter filter) {
        return this.msVenueDatasource.getPriceTypePassbookTicketCommunicationElements(venueTemplateId, priceTypeId, filter);
    }

    public void updatePriceTypePassbookCommunicationElements(final Long venueTemplateId, final Long priceTypeId, final Set<PriceTypeTicketCommunicationElement> elements) {
        this.msVenueDatasource.updatePriceTypePassbookTicketCommunicationElements(venueTemplateId, priceTypeId, elements);
    }

    public void deletePriceTypePassbookCommunicationElementImage(final Long venueTemplateId, final Long priceTypeId, String language, String type) {
        this.msVenueDatasource.deletePriceTypePassbookTicketCommunicationElement(venueTemplateId, priceTypeId, language, type);
    }

    public List<IdNameDTO> findChangedPriceTypeTicketContents(final Long venueTemplateId) {
        return this.msVenueDatasource.findChangedPriceTypeTicketContents(venueTemplateId);
    }

}
