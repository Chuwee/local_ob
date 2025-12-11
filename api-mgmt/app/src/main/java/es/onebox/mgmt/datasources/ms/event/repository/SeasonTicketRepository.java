package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.*;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.AssignSessionResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceCompleteRelation;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceFilter;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceRelations;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CountRenewalsPurgeResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalCandidatesSeasonTicketsRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalEntitiesRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalSeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketChangeSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketDatasourceStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketFilter;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRates;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketReleaseSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewalsRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRedemption;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTransferSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketRedemption;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketTransferSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionValidationMsEventResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessions;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionsEventList;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTickets;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UnAssignSessionResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateChangeSeatSeasonTicketPriceRelation;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalPurgeFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventsFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeasonTicketRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public SeasonTicketRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public SeasonTicket getSeasonTicket(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicket(seasonTicketId);
    }

    public SeasonTickets getSeasonTickets(SeasonTicketFilter filter) {
        return msEventDatasource.getSeasonTickets(filter);
    }

    public Long create(CreateSeasonTicketData createSeasonTicketData) {
        return msEventDatasource.createSeasonTicket(createSeasonTicketData);
    }

    public void updateSeasonTicket(Long seasonTicketId, SeasonTicket seasonTicket) {
        msEventDatasource.updateSeasonTicket(seasonTicketId, seasonTicket);
    }

    public Long createSeasonTicketRate(Long seasonTicketId, SeasonTicketRate rate) {
        return msEventDatasource.createSeasonTicketRate(seasonTicketId.intValue(), rate);
    }

    public SeasonTicketRates getSeasonTicketRates(Integer seasonTicketId) {
        return msEventDatasource.getSeasonTicketRates(seasonTicketId);
    }

    public void updateSeasonTicketRate(Long seasonTicketId, Long rateId, SeasonTicketRate rate) {
        msEventDatasource.updateSeasonTicketRate(seasonTicketId, rateId, rate);
    }

    public void updateSeasonTicketRates(Long seasonTicketId, List<SeasonTicketRate> rates) {
        msEventDatasource.updateSeasonTicketRates(seasonTicketId, rates);
    }

    public void deleteSeasonTicketRate(Long seasonTicketId, Long rateId) {
        msEventDatasource.deleteSeasonTicketRate(seasonTicketId, rateId);
    }

    public List<VenueTemplatePrice> getPrices(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketPrices(seasonTicketId);
    }

    public void updatePrices(Long seasonTicketId, List<VenueTemplatePrice> prices) {
        msEventDatasource.updateSeasonTicketPrices(seasonTicketId, prices);
    }

    public SeasonTicketDatasourceStatus getSeasonTicketStatus(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketStatus(seasonTicketId);
    }

    public void updateSeasonTicketStatus(Long seasonTicketId, UpdateSeasonTicketStatus updateSeasonTicketStatus) {
        msEventDatasource.updateSeasonTicketStatus(seasonTicketId, updateSeasonTicketStatus);
    }

    public void deleteSeasonTicket(Long seasonticketId) {
        msEventDatasource.deleteSeasonTicket(seasonticketId);
    }

    public SeasonTicketSessions getSeasonTicketCandidateSessions(SeasonTicketSessionsSearchFilter filter, Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketCandidateSessions(filter, seasonTicketId);
    }

    public SeasonTicketSessionsEventList getSeasonTicketSessionsEvents(Long seasonTicketId, SeasonTicketSessionsEventsFilter filter){
        return msEventDatasource.getSeasonTicketSessionsEvents(seasonTicketId, filter);
    }

    public SeasonTicketSessionValidationMsEventResponse verifySessionsFromSeasonTicket(Long seasonTicketId, Long sessionId, Boolean includeSeats) {
        return msEventDatasource.verifySessionsFromSeasonTicket(seasonTicketId, sessionId, includeSeats);
    }

    public AssignSessionResponse assignSession(Long seasonTicketId, Long targetSessionId, Boolean updateBarcodes) {
        return msEventDatasource.assignSession(seasonTicketId, targetSessionId, updateBarcodes);
    }

    public UnAssignSessionResponse unAssignSession(Long seasonTicketId, Long targetSessionId, Boolean updateBarcodes) {
        return msEventDatasource.unAssignSession(seasonTicketId, targetSessionId, updateBarcodes);
    }

    public List<SeasonTicketSurcharge> getSurcharges(Long seasonTicketId, List<SurchargeTypeDTO> types) {
        return msEventDatasource.getSeasonTicketSurcharges(seasonTicketId, types);
    }

    public void setSurcharge(Long seasonTicketId, List<EventSurcharge> requests) {
        msEventDatasource.setSeasonTicketSurcharge(seasonTicketId, requests);
    }

    public RenewalCandidatesSeasonTicketsRepositoryResponse searchRenewalCandidatesSeasonTickets(Long seasonTicketId) {
        return msEventDatasource.searchRenewalCandidatesSeasonTickets(seasonTicketId);
    }

    public RenewalEntitiesRepositoryResponse getRenewalEntities(Long seasonTicketId,
                                                                SeasonTicketRenewalFilter filter) {
        return msEventDatasource.getRenewalEntities(seasonTicketId, filter);
    }

    public void setRenewalSeasonTicketDTO(Long seasonTicketId, RenewalSeasonTicket renewalSeasonTicket) {
        msEventDatasource.setRenewalSeasonTicketDTO(seasonTicketId, renewalSeasonTicket);
    }

    public SeasonTicketRenewalsRepositoryResponse getRenewalsSeasonTicket(Long seasonTicketId, SeasonTicketRenewalFilter filter) {
        return msEventDatasource.getRenewalsSeasonTicket(seasonTicketId, filter);
    }

    public SeasonTicketRenewalsRepositoryResponse getRenewals(SeasonTicketRenewalsFilter filter) {
        return msEventDatasource.getRenewals(filter);
    }

    public UpdateRenewalResponse updateRenewalSeats(Long seasonTicketId, UpdateRenewalRequest request) {
        return msEventDatasource.updateRenewalSeats(seasonTicketId, request);
    }

    public void deleteRenewalSeat(Long seasonTicketId, String renewalId) {
        msEventDatasource.deleteRenewalSeat(seasonTicketId, renewalId);
    }

    public DeleteRenewalsResponse deleteRenewalSeats(Long seasonTicketId, DeleteRenewalsRequest request) {
        return msEventDatasource.deleteRenewalSeats(seasonTicketId, request);
    }

    public void updateBarcodes(Long seasonTicketId) {
        msEventDatasource.updateBarcodes(seasonTicketId);
    }

    public void purgeSeasonTicketRenewalSeats(Long seasonTicketId, SeasonTicketRenewalPurgeFilter filter) {
        msEventDatasource.purgeSeasonTicketRenewalSeats(seasonTicketId, filter);
    }

    public CountRenewalsPurgeResponse countRenewalsPurge(Long seasonTicketId, SeasonTicketRenewalPurgeFilter filter) {
        return msEventDatasource.countRenewalsPurge(seasonTicketId, filter);
    }

    public void createChangeSeatPricesTable(Long seasonTicketId, ChangeSeatSeasonTicketPriceRelations priceRelations){
        msEventDatasource.createChangeSeatPricesTable(seasonTicketId, priceRelations);
    }

    public SeasonTicketChangeSeat getSeasonTicketChangeSeat(Long seasonTicketId){
        return msEventDatasource.getSeasonTicketChangeSeat(seasonTicketId);
    }

    public void updateSeasonTicketChangeSeat(Long seasonTicketId, SeasonTicketChangeSeat updateChangeSeat){
        msEventDatasource.updateSeasonTicketChangeSeat(seasonTicketId, updateChangeSeat);
    }


    public List<ChangeSeatSeasonTicketPriceCompleteRelation> searchChangeSeatPriceRelations(Long seasonTicketId, ChangeSeatSeasonTicketPriceFilter priceFilter){
        return msEventDatasource.searchChangeSeatPriceRelations(seasonTicketId, priceFilter);
    }

    public void updateChangeSeatPriceRelations(Long seasonTicketId, List<UpdateChangeSeatSeasonTicketPriceRelation> updatePriceRelations){
        msEventDatasource.updateChangeSeatPriceRelations(seasonTicketId, updatePriceRelations);
    }

    public SeasonTicketReleaseSeat getSeasonTicketReleaseSeat(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketReleaseSeat(seasonTicketId);
    }

    public void updateSeasonTicketReleaseSeat(Long seasonTicketId, SeasonTicketReleaseSeat seasonTicketReleaseSeat) {
        msEventDatasource.updateSeasonTicketReleaseSeat(seasonTicketId, seasonTicketReleaseSeat);
    }

    public SeasonTicketTransferSeat getSeasonTicketTransferSeat(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketTransferSeat(seasonTicketId);
    }

    public void updateSeasonTicketTransferSeat(Long seasonTicketId, UpdateSeasonTicketTransferSeat seasonTicketTransferSeat) {
        msEventDatasource.updateSeasonTicketTransferSeat(seasonTicketId, seasonTicketTransferSeat);
    }

    public SeasonTicketLoyaltyPointsConfig getSeasonTicketLoyaltyPoints(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketLoyaltyPoints(seasonTicketId);
    }

    public void updateSeasonTicketLoyaltyPoints(Long seasonTicketId, SeasonTicketLoyaltyPointsConfig seasonTicketLoyaltyPointsConfigs) {
        msEventDatasource.updateSeasonTicketLoyaltyPoints(seasonTicketId, seasonTicketLoyaltyPointsConfigs);
    }

    public Form getSeasonTicketForm(Long seasonTicketId, String formType) {
        return msEventDatasource.getSeasonTicketForm(seasonTicketId, formType);
    }

    public void updateSeasonTicketForm(Long seasonTicketId, String formType, Form updateForm) {
        msEventDatasource.updateSeasonTicketForm(seasonTicketId, formType, updateForm);
    }

    public SeasonTicketRedemption getSeasonTicketRedemption(Long seasonTicketId) {
        return msEventDatasource.getSeasonTicketRedemption(seasonTicketId);
    }

    public void updateSeasonTicketRedemption(Long seasonTicketId, UpdateSeasonTicketRedemption seasonTicketRedemption) {
        msEventDatasource.updateSeasonTicketRedemption(seasonTicketId, seasonTicketRedemption);
    }

}