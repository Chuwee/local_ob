package es.onebox.mgmt.datasources.ms.venue.repository;

import es.onebox.mgmt.datasources.ms.venue.MsVenueDatasource;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoStatusUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionUpdateTemplateInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBaseResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoCreateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoImagesDeleteFilterDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.UpdateTemplateInfoDefault;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoSearchDTO;
import es.onebox.mgmt.venues.enums.ElementType;
import org.springframework.stereotype.Repository;

@Repository
public class VenueTemplateElementsInfoRepository {

    private final MsVenueDatasource msVenueDatasource;

    public VenueTemplateElementsInfoRepository(MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    public TemplateInfoListResponse searchVenueTemplateElementsInfo(Long venueTemplateId, VenueTemplateElementInfoSearchDTO baseRequestFilter) {
        return msVenueDatasource.searchVenueTemplateElementsInfo(venueTemplateId, baseRequestFilter);
    }

    public TemplateInfoBaseResponse getVenueTemplateElementsInfo(Long venueTemplateId, ElementType elementType, Long elementId) {
        return msVenueDatasource.getVenueTemplateElementsInfo(venueTemplateId, elementType, elementId);
    }

    public void createTemplateElementInfo(Long venueTemplateId, TemplateInfoCreateRequest request) {
        msVenueDatasource.createTemplateElementInfo(venueTemplateId, request);
    }

    public void updateTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, UpdateTemplateInfoDefault updateTemplateInfoDefault) {
        msVenueDatasource.updateTemplateElementInfo(venueTemplateId, elementType, elementId, updateTemplateInfoDefault);
    }

    public void deleteTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId) {
        msVenueDatasource.deleteTemplateElementInfo(venueTemplateId, elementType, elementId);
    }

    public void deleteTemplateElementInfoImages(Long venueTemplateId, ElementType elementType, Long elementId,
                                                ElementInfoImageType imageType, String language,
                                                TemplateInfoImagesDeleteFilterDTO filter) {
        msVenueDatasource.deleteTemplateElementInfoImages(venueTemplateId, elementType, elementId, imageType, language, filter);
    }

    public void bulkUpdateTemplateElementInfo(Long venueTemplateId, TemplateInfoBulkUpdateRequest request,
                                              VenueTemplateElementInfoSearchDTO requestSearch) {
        msVenueDatasource.bulkUpdateTemplateElementInfo(venueTemplateId, request, requestSearch);
    }

    public void bulkDeleteVenueTemplateElementInfo(Long venueTemplateId, VenueTemplateElementInfoBulkRequestDTO filters) {
        msVenueDatasource.bulkDeleteVenueTemplateElementInfo(venueTemplateId, filters);
    }

    public SessionTemplateInfoListResponse searchSessionsVenueTemplateElementsInfo(Long venueConfigId, Long sessionId,
                                                                                   VenueTemplateSessionElementInfoSearchDTO searchRequest) {
        return msVenueDatasource.searchSessionVenueTemplateElementsInfo(venueConfigId, sessionId, searchRequest);
    }

    public void createSessionTemplateElementInfo(Long venueTemplateId, Long sessionId, SessionTemplateInfoRequest request) {
         msVenueDatasource.createSessionTemplateElementInfo(venueTemplateId, sessionId, request);
    }

    public SessionTemplateInfoResponse getSessionVenueTemplateElementsInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId) {
        return msVenueDatasource.getSessionVenueTemplateElementInfo(venueTemplateId, elementType, elementId, sessionId);
    }

    public void updateSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId, SessionUpdateTemplateInfo request) {
        msVenueDatasource.updateSessionVenueTemplateElementInfo(venueTemplateId, elementType, elementId, sessionId, request);
    }

    public void deleteSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId) {
        msVenueDatasource.deleteSessionVenueTemplateElementInfo(venueTemplateId, elementType, elementId, sessionId);
    }

    public void bulkUpdateSessionTemplateElementInfo(Long venueTemplateId, Long sessionId,
                                                     SessionTemplateInfoBulkUpdateRequest request,
                                                     VenueTemplateElementInfoSearchDTO requestSearch) {
        msVenueDatasource.bulkUpdateSessionTemplateElementInfo(venueTemplateId, sessionId, request, requestSearch);
    }

    public void updateStatusSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId,
                                                            Long sessionId, SessionTemplateInfoStatusUpdateRequest request) {
        msVenueDatasource.updateStatusSessionVenueTemplateElementInfo(venueTemplateId, elementType, elementId, sessionId, request);
    }

    public void deleteSessionTemplateElementInfoImages(Long venueConfigId, ElementType elementType, Long elementId,
                                                       Long sessionId, ElementInfoImageType imageType, String language,
                                                       TemplateInfoImagesDeleteFilterDTO filter) {
        msVenueDatasource.deleteSessionTemplateElementInfoImages(
                venueConfigId, elementType, elementId, sessionId, imageType, language, filter);
    }
}
