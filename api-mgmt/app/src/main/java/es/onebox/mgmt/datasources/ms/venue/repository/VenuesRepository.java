package es.onebox.mgmt.datasources.ms.venue.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.IdNameListWithMetadata;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.ms.venue.MsVenueDatasource;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.mgmt.datasources.ms.venue.dto.ProviderSector;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemPostRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemPutRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.Venues;
import es.onebox.mgmt.datasources.ms.venue.dto.VenuesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReason;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReasonRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CloneVenueTemplateSector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateRow;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateViewLink;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTag;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTagGroup;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Gate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.InteractiveVenue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.dto.template.RowDetail;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Sector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.TagWithGroup;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateInteractiveVenue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateNotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateRow;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateRowBulk;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateViewBulk;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateVipView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpsertVenueTemplateImage;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateBaseSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateImage;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViews;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViewsFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplates;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFiltersRequest;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.venues.dto.CreateVenueTagConfigRequestDTO;
import es.onebox.mgmt.venues.dto.GateRequestDTO;
import es.onebox.mgmt.venues.dto.PriceTypeChannelContentFilterDTO;
import es.onebox.mgmt.venues.dto.PriceTypeRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagConfigRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatesSeatExportFileField;
import es.onebox.mgmt.venues.dto.VenueTemplatesSectorExportFileField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Repository
public class VenuesRepository {

    private static final String VENUE_TEMPLATE_ID_CACHED = "venue.venue_template_id";


    private final MsVenueDatasource msVenueDatasource;

    @Autowired
    public VenuesRepository(MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    public Venue getVenue(Long venueId) {
        return msVenueDatasource.getVenue(venueId);
    }

    public Venues getVenues(Long userOperatorId, VenuesFilter filter, SortOperator<String> sort, List<String> fields) {
        return msVenueDatasource.getVenues(userOperatorId, filter, sort, fields);
    }

    public VenueTemplate getVenueTemplate(Long venueTemplateId) {
        return msVenueDatasource.getVenueTemplate(venueTemplateId);
    }

    @Cached(key = VENUE_TEMPLATE_ID_CACHED)
    public VenueTemplate getCachedVenueTemplate(@CachedArg Long venueTemplateId) {
        return msVenueDatasource.getVenueTemplate(venueTemplateId);
    }

    public VenueTemplates getVenueTemplates(Long operatorId, VenueTemplatesFilter filter, SortOperator<String> sort, List<String> fields) {
        return msVenueDatasource.getVenueTemplates(operatorId, filter, sort, fields);
    }

    public Long createVenueTemplate(CreateVenueTemplateRequest venueTemplateRequest) {
        return msVenueDatasource.createVenueTemplate(venueTemplateRequest);
    }

    public void updateVenueTemplate(Long venueTemplateId, UpdateVenueTemplate updateVenueTemplate) {
        msVenueDatasource.updateVenueTemplate(venueTemplateId, updateVenueTemplate);
    }

    public Long createVenueTemplateView(Long venueTemplateId, UpdateVenueTemplateView body) {
        return msVenueDatasource.createVenueTemplateView(venueTemplateId, body);
    }

    public VenueTemplateViews getVenueTemplateViews(Long venueTemplateId, VenueTemplateViewsFilter filter) {
        return msVenueDatasource.getVenueTemplateViews(venueTemplateId, filter);
    }

    public void updateVenueTemplateVipViews(Long venueTemplateId, List<UpdateVenueTemplateVipView> body) {
        msVenueDatasource.updateVenueTemplateVipViews(venueTemplateId, body);
    }

    public void updateVenueTemplateViews(Long venueTemplateId, List<UpdateVenueTemplateViewBulk> viewList) {
        msVenueDatasource.updateVenueTemplateViews(venueTemplateId, viewList);
    }

    public void updateVenueTemplateView(Long venueTemplateId, Long viewId, UpdateVenueTemplateView body) {
        msVenueDatasource.updateVenueTemplateView(venueTemplateId, viewId, body);
    }

    public VenueTemplateView getVenueTemplateView(Long venueTemplateId, String containerId) {
        return msVenueDatasource.getVenueTemplateView(venueTemplateId, containerId);
    }

    public void deleteVenueTemplateView(Long venueTemplateId, Long viewId) {
        msVenueDatasource.deleteVenueTemplateView(venueTemplateId, viewId);
    }

    public Long createVenueTemplateViewLink(Long venueTemplateId, Integer viewId, CreateVenueTemplateViewLink body) {
        return msVenueDatasource.createVenueTemplateViewLink(venueTemplateId, viewId, body);
    }

    public void deleteVenueTemplateViewLink(Long venueTemplateId, Long viewId, Long linkId) {
        msVenueDatasource.deleteVenueTemplateViewLink(venueTemplateId, viewId, linkId);
    }

    public void updateVenueTemplateViewTemplate(Long venueTemplateId, Long viewId, String template) {
        msVenueDatasource.updateVenueTemplateViewTemplate(venueTemplateId, viewId, template);
    }

    public List<VenueTemplateImage> getVenueTemplateImages(Long venueTemplateId) {
        return msVenueDatasource.getVenueTemplateImages(venueTemplateId);
    }

    public VenueTemplateImage upsertVenueTemplateImage(Long venueTemplateId, UpsertVenueTemplateImage body) {
        return msVenueDatasource.upsertVenueTemplateImage(venueTemplateId, body);
    }

    public void deleteVenueTemplateImage(Long venueTemplateId, Long imageId) {
        msVenueDatasource.deleteVenueTemplateImage(venueTemplateId, imageId);
    }

    public RowDetail getVenueTemplateRow(Long venueTemplateId, Integer rowId) {
        return msVenueDatasource.getVenueTemplateRow(venueTemplateId, rowId);
    }

    public Long createVenueTemplateRow(Long venueTemplateId, CreateVenueTemplateRow requestDTO) {
        return msVenueDatasource.createVenueTemplateRow(venueTemplateId, requestDTO);
    }

    public List<IdDTO> createVenueTemplateRows(Long venueTemplateId, List<CreateVenueTemplateRow> requestDTO) {
        return msVenueDatasource.createVenueTemplateRows(venueTemplateId, requestDTO);
    }

    public void updateVenueTemplateRow(Long venueTemplateId, Long rowId, UpdateVenueTemplateRow requestDTO) {
        msVenueDatasource.updateVenueTemplateRow(venueTemplateId, rowId, requestDTO);
    }

    public void updateVenueTemplateRows(Long venueTemplateId, List<UpdateVenueTemplateRowBulk> requestDTO) {
        msVenueDatasource.updateVenueTemplateRows(venueTemplateId, requestDTO);
    }

    public void deleteVenueTemplateRow(Long venueTemplateId, Long rowId) {
        msVenueDatasource.deleteVenueTemplateRow(venueTemplateId, rowId);
    }

    public VenueTemplateSeat getVenueTemplateSeat(Long venueTemplateId, Integer seatId) {
        return msVenueDatasource.getVenueTemplateSeat(venueTemplateId, seatId);
    }

    public List<VenueTemplateBaseSeat> getVenueTemplateSeatsByRows(Long venueTemplateId, List<Integer> rowIds) {
        return msVenueDatasource.getVenueTemplateSeatsByRows(venueTemplateId, rowIds);
    }

    public List<IdDTO> createVenueTemplateSeats(Long venueTemplateId, List<CreateVenueTemplateSeat> requestDTO) {
        return msVenueDatasource.createVenueTemplateSeats(venueTemplateId, requestDTO);
    }

    public void deleteVenueTemplateSeats(Long venueTemplateId, List<Integer> seatIds) {
        msVenueDatasource.deleteVenueTemplateSeat(venueTemplateId, seatIds);
    }

    public List<BlockingReason> getBlockingReasons(Long venueTemplateId) {
        return msVenueDatasource.getBlockingReasons(venueTemplateId);
    }

    public void updateAssignTags(Long venueTemplateId, UpdateVenueTemplateSeat[] requestDTO) {
        msVenueDatasource.updateAssignTags(venueTemplateId, requestDTO);
    }

    public void updateNotNumberedZoneTags(Long venueTemplateId, VenueTagDTO[] notNumberedZone) {
        msVenueDatasource.updateNotNumberedZoneTags(venueTemplateId, notNumberedZone);
    }

    public InputStream getVenueTemplateMap(Long id) {
        return msVenueDatasource.getVenueTemplateMap(id);
    }

    public Long createBlockingReason(Long venueTemplateId, BlockingReasonRequest requestDTO) {
        return msVenueDatasource.createBlockingReason(venueTemplateId, requestDTO);
    }

    public void updateBlockingReason(Long venueTemplateId, Long blockingReasonId, BlockingReasonRequest requestDTO) {
        msVenueDatasource.updateBlockingReason(venueTemplateId, blockingReasonId, requestDTO);
    }

    public void deleteBlockingReason(Long venueTemplateId, Long blockingReasonId) {
        msVenueDatasource.deleteBlockingReason(venueTemplateId, blockingReasonId);
    }

    public List<PriceType> getPriceTypes(Long venueTemplateId) {
        return msVenueDatasource.getPriceTypes(venueTemplateId);
    }

    public Long createPriceType(Long venueTemplateId, PriceTypeRequestDTO requestDTO) {
        return msVenueDatasource.createPriceType(venueTemplateId, requestDTO);
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequestDTO requestDTO) {
        msVenueDatasource.updatePriceType(venueTemplateId, priceTypeId, requestDTO);
    }

    public void deletePriceType(Long venueTemplateId, Long priceTypeId) {
        msVenueDatasource.deletePriceType(venueTemplateId, priceTypeId);
    }

    public List<Quota> getQuotas(Long venueTemplateId) {
        return msVenueDatasource.getQuotas(venueTemplateId);
    }

    public Long createQuota(Long venueTemplateId, CreateVenueTagConfigRequestDTO requestDTO) {
        return msVenueDatasource.createQuota(venueTemplateId, requestDTO);
    }

    public void updateQuota(Long venueTemplateId, Long quotaId, VenueTagConfigRequestDTO requestDTO) {
        msVenueDatasource.updateQuota(venueTemplateId, quotaId, requestDTO);
    }

    public void deleteQuota(Long venueTemplateId, Long quotaId) {
        msVenueDatasource.deleteQuota(venueTemplateId, quotaId);
    }

    public List<Gate> getGates(Long venueTemplateId) {
        return msVenueDatasource.getGates(venueTemplateId);
    }

    public Long createGate(Long venueTemplateId, GateRequestDTO requestDTO) {
        return msVenueDatasource.createGate(venueTemplateId, requestDTO);
    }

    public void updateGate(Long venueTemplateId, Long gateId, GateRequestDTO requestDTO) {
        msVenueDatasource.updateGate(venueTemplateId, gateId, requestDTO);
    }

    public void deleteGate(Long venueTemplateId, Long gateId) {
        msVenueDatasource.deleteGate(venueTemplateId, gateId);
    }

    public List<TagWithGroup> getTags(Long venueTemplateId) {
        return msVenueDatasource.getTags(venueTemplateId);
    }

    public List<Sector> getSectors(Long venueTemplateId) {
        return msVenueDatasource.getSectors(venueTemplateId);
    }

    public Sector getSector(Long venueTemplateId, Long sectorId) {
        return msVenueDatasource.getSector(venueTemplateId, sectorId);
    }

    public ProviderSector getProviderSector(String provider, String sectorCode) {
        return msVenueDatasource.getProviderSector(provider, sectorCode);
    }

    public Long createSector(Long venueTemplateId, VenueTemplateSector requestDTO) {
        return msVenueDatasource.createSector(venueTemplateId, requestDTO);
    }

    public Long cloneSector(Long venueTemplateId, Long sectorId, CloneVenueTemplateSector requestDTO) {
        return msVenueDatasource.cloneSector(venueTemplateId, sectorId, requestDTO);
    }

    public void updateSector(Long venueTemplateId, Long sectorId, VenueTemplateSector requestDTO) {
        msVenueDatasource.updateSector(venueTemplateId, sectorId, requestDTO);
    }

    public void deleteSector(Long venueTemplateId, Long sectorId) {
        msVenueDatasource.deleteSector(venueTemplateId, sectorId);
    }

    public NotNumberedZoneCapacity getNotNumberedZone(Long venueTemplateId, Long zoneId) {
        return msVenueDatasource.getNotNumberedZone(venueTemplateId, zoneId);
    }

    public List<NotNumberedZone> getNotNumberedZones(Long venueTemplateId, NotNumberedZoneFilter request) {
        return msVenueDatasource.getNotNumberedZones(venueTemplateId, request);
    }

    public List<NotNumberedZoneCapacity> getNotNumberedZonesBySectorId(Long venueTemplateId, Long sectorId) {
        return msVenueDatasource.getNotNumberedZones(venueTemplateId, sectorId);
    }

    public IdDTO createNotNumberedZone(Long venueTemplateId, NotNumberedZone body) {
        return msVenueDatasource.createNotNumberedZone(venueTemplateId, body);
    }

    public List<IdDTO> createNotNumberedZones(Long venueTemplateId, Set<NotNumberedZone> nnZone) {
        return msVenueDatasource.createNotNumberedZones(venueTemplateId, nnZone);
    }

    public Long cloneNotNumberedZone(Long venueTemplateId, Long nnZoneId, NotNumberedZone body) {
        return msVenueDatasource.cloneNotNumberedZone(venueTemplateId, nnZoneId, body);
    }

    public void updateNotNumberedZone(Long venueTemplateId, Long notNumberedZoneId, UpdateNotNumberedZone requestDTO) {
        msVenueDatasource.updateNotNumberedZone(venueTemplateId, notNumberedZoneId, requestDTO);
    }

    public void updateNotNumberedZoneBulk(Long venueTemplateId, Set<UpdateNotNumberedZone> body) {
        msVenueDatasource.updateNotNumberedZoneBulk(venueTemplateId, body);
    }

    public void deleteNotNumberedZone(Long venueTemplateId, Long notNumberedZoneId) {
        msVenueDatasource.deleteNotNumberedZone(venueTemplateId, notNumberedZoneId);
    }

    public IdNameListWithMetadata getVenueTemplatesFilterOptions(String filterName, VenueTemplatesFiltersRequest request) {
        return msVenueDatasource.getVenueTemplatesFilterOptions(filterName, request);
    }

    public List<PriceTypeCommunicationElement> getPriceTypeCommElements(Long venueTemplateId, Long priceTypeId, PriceTypeChannelContentFilterDTO filter) {
        return msVenueDatasource.getPriceTypeCommElements(venueTemplateId, priceTypeId, filter);
    }

    public void upsertPriceTypeCommElements(Long venueTemplateId, Long priceTypeId,
                                            List<PriceTypeCommunicationElement> commElements) {
        msVenueDatasource.upsertPriceTypeCommElements(venueTemplateId, priceTypeId, commElements);
    }

    public List<PriceTypeCapacity> getPriceTypeCapacity(Long venueTemplateId) {
        return msVenueDatasource.getPriceTypeCapacity(venueTemplateId);
    }

    public List<QuotaCapacity> getQuotasCapacity(Long venueTemplateId) {
        return msVenueDatasource.getQuotaCapacity(venueTemplateId);
    }

    public void updateQuotasCapacity(Long venueTemplateId, List<QuotaCapacity> requestDTO) {
        msVenueDatasource.updateQuotaCapacity(venueTemplateId, requestDTO);
    }

    public InteractiveVenue getInteractiveVenue(Long venueTemplateId) {
        return msVenueDatasource.getInteractiveVenue(venueTemplateId);
    }

    public void updateInteractiveVenue(Long venueTemplateId,
                                       UpdateInteractiveVenue requestDTO) {
        msVenueDatasource.updateInteractiveVenue(venueTemplateId, requestDTO);
    }

    public ExportProcess generateVenueTemplateSectorsReport(Long venueTemplateId, ExportFilter<VenueTemplatesSectorExportFileField> filter) {
        return msVenueDatasource.generateVenueTemplateSectorsReport(venueTemplateId, filter);
    }

    public ExportProcess generateVenueTemplateSeatsReport(Long venueTemplateId, ExportFilter<VenueTemplatesSeatExportFileField> filter) {
        return msVenueDatasource.generateVenueTemplateSeatsReport(venueTemplateId, filter);
    }

    public ExportProcess generateVenueTemplateViewsReport(Long venueTemplateId, ExportFilter filter) {
        return msVenueDatasource.generateVenueTemplateViewsReport(venueTemplateId, filter);
    }

    public ExportProcess getVenueTemplatesReportStatus(Long venueTemplateId, String exportId, Long id, String type) {
        return msVenueDatasource.getVenueTemplatesReportStatus(venueTemplateId, exportId, id, type);
    }

    public List<DynamicTagGroup> getVenueTemplateDynamicTagGroups(Long venueTemplateId) {
        return msVenueDatasource.getVenueTemplateDynamicTagGroups(venueTemplateId);
    }

    public Long createVenueTemplateDynamicTagGroup(Long venueTemplateId, DynamicTagGroup request) {
        return msVenueDatasource.createVenueTemplateDynamicTagGroup(venueTemplateId, request);
    }

    public void updateVenueTemplateDynamicTagGroup(Long venueTemplateId, Long tagGroupId, DynamicTagGroup request) {
        msVenueDatasource.updateVenueTemplateDynamicTagGroup(venueTemplateId, tagGroupId, request);
    }

    public void deleteVenueTemplateDynamicTagGroup(Long venueTemplateId, Long tagGroupId) {
        msVenueDatasource.deleteVenueTemplateDynamicTagGroup(venueTemplateId, tagGroupId);
    }

    public List<DynamicTag> getDynamicTagGroupTags(Long venueTemplateId, Long tagGroupId) {
        return msVenueDatasource.getVenueTemplateDynamicTagGroupTags(venueTemplateId, tagGroupId);
    }

    public Long createDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, CreateVenueTagConfigRequestDTO requestDTO) {
        return msVenueDatasource.createDynamicTagGroupTag(venueTemplateId, tagGroupId, requestDTO);
    }

    public void updateDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, Long tagId, VenueTagConfigRequestDTO requestDTO) {
        msVenueDatasource.updateDynamicTagGroupTag(venueTemplateId, tagGroupId, tagId, requestDTO);
    }

    public void deleteDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, Long tagId) {
        msVenueDatasource.deleteDynamicTagGroupTag(venueTemplateId, tagGroupId, tagId);
    }

    public IdDTO createVenue(VenueItemPostRequest newVenue) {
        return msVenueDatasource.createVenue(newVenue);
    }

    public void updateVenue(VenueItemPutRequest patchedVenue) {
        msVenueDatasource.updateVenue(patchedVenue);
    }

    public void deleteVenue(Long venueId, Long entityId) {
        msVenueDatasource.deleteVenueById(venueId, entityId);
    }

    public List<IdNameCodeDTO> getProviderVenues(String provider) {
        return msVenueDatasource.getProviderVenues(provider);
    }

    public List<IdNameCodeDTO> getProviderVenueTemplates(String provider, Long externalVenueId) {
        return msVenueDatasource.getProviderVenueTemplates(provider, externalVenueId);
    }
}
