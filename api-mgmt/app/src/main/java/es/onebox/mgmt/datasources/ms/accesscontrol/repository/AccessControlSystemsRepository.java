package es.onebox.mgmt.datasources.ms.accesscontrol.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.MsAccessControlDatasource;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.AddProductEventRequestDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.HandlePackageEventRequestDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ProductResponseDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.SkidataVenueConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccessControlSystemsRepository {

    private static final String ACCESS_CONTROL_VENUE_ID_CACHED = "access_control.venue_id";
    private static final String FORTRESS_SEASON_TICKET = "fortress.season_ticket";
    private final MsAccessControlDatasource msAccessControlDatasource;

    @Autowired
    public AccessControlSystemsRepository(final MsAccessControlDatasource msAccessControlDatasource) {
        this.msAccessControlDatasource = msAccessControlDatasource;
    }

    public List<AccessControlSystem> findAll() {
        return msAccessControlDatasource.getSystems(null, null);
    }

    public List<AccessControlSystem> findByEntityId(Long entityId) {
        return msAccessControlDatasource.getSystems(entityId, null);
    }

    public List<AccessControlSystem> findByVenueId(Long venueId) {
        return msAccessControlDatasource.getSystems(null, venueId);
    }

    @Cached(key = ACCESS_CONTROL_VENUE_ID_CACHED)
    public List<AccessControlSystem> findByVenueIdCached(@CachedArg Long venueId) {
        return msAccessControlDatasource.getSystems(null, venueId);
    }

    public void createAccessControlSystemEntity(Long entityId, AccessControlSystem system) {
        msAccessControlDatasource.createAccessControlSystemEntity(entityId, system.name());
    }

    public void deleteAccessControlSystemEntity(Long entityId, AccessControlSystem system) {
        msAccessControlDatasource.deleteAccessControlSystemEntity(entityId, system.name());
    }

    public void createAccessControlSystemVenue(Long venueId, AccessControlSystem system) {
        msAccessControlDatasource.createAccessControlSystemVenue(venueId, system.name());
    }

    public void deleteAccessControlSystemVenue(Long venueId, AccessControlSystem system) {
        msAccessControlDatasource.deleteAccessControlSystemVenue(venueId, system.name());
    }

    public SkidataVenueConfig getVenueSkidataConfig(Long venueId, AccessControlSystem system) {
        return msAccessControlDatasource.getVenueSkidataConfig(venueId, system.name());
    }

    public void createVenueSkidataConfig(Long venueId, AccessControlSystem system, SkidataVenueConfig config) {
        msAccessControlDatasource.createVenueSkidataConfig(venueId, system.name(), config);
    }

    public void modifyVenueSkidataConfig(Long venueId, AccessControlSystem system, SkidataVenueConfig config) {
        msAccessControlDatasource.modifyVenueSkidataConfig(venueId, system.name(), config);
    }

    public void deleteVenueSkidataConfig(Long venueId, AccessControlSystem system) {
        msAccessControlDatasource.deleteVenueSkidataConfig(venueId, system.name());
    }

    public void addFortressSession(Long entityId, Long eventId, AddProductEventRequestDTO request) {
        msAccessControlDatasource.addFortressSession(entityId, eventId, request);
    }

    public void assignFortressSessionToSeasonTicket(Long entityId, Long seasonTicketId, HandlePackageEventRequestDTO request){
        msAccessControlDatasource.assignFortressSessionToSeasonTicket(entityId, seasonTicketId, request);
    }

    public void unassignFortressSessionFromSeasonTicket(Long entityId, Long seasonTicketId, HandlePackageEventRequestDTO request){
        msAccessControlDatasource.unassignFortressSessionFromSeasonTicket(entityId, seasonTicketId, request);
    }

    @Cached(key = FORTRESS_SEASON_TICKET, expires = 60 * 10)
    public ProductResponseDTO getFortressSeasonTicket(@CachedArg Long entityId, @CachedArg Long seasonTicketId) {
        return msAccessControlDatasource.getFortressSeasonTicket(entityId, seasonTicketId);
    }

    public void createFortressSeasonTicket(Long entityId, Long seasonTicketId) {
        msAccessControlDatasource.createFortressSeasonTicket(entityId, seasonTicketId);
    }

    public void addOrUpdateFortressRate(Long entityId, Long eventId, Long rateId) {
        msAccessControlDatasource.addOrUpdateFortressRate(entityId, eventId, rateId);
    }

    public void addOrUpdateFortressVenueTemplate(Long entityId, Long venueTemplateId) {
        msAccessControlDatasource.addOrUpdateFortressVenueTemplate(entityId, venueTemplateId);
    }

}
