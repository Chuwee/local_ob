package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestricted;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateRateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTax;
import es.onebox.mgmt.datasources.ms.event.dto.session.EventCommunicationElementBulk;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionRefundConditions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleConstraint;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.session.UpdateSaleRestriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Predicate;

@Repository
public class SessionsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public SessionsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public List<EventCommunicationElement> getSessionCommunicationElements(Long eventId, Long sessionId, CommunicationElementFilter<EventTagType> filter) {
        return msEventDatasource.getSessionCommunicationElements(eventId, sessionId, filter);
    }

    public void updateSessionCommunicationElements(Long eventId, Long sessionId, List<EventCommunicationElement> elements) {
        msEventDatasource.updateSessionCommunicationElements(eventId, sessionId, elements);
    }

    public void updateSessionCommunicationElementsBulk(Long eventId, EventCommunicationElementBulk data) {
        msEventDatasource.updateSessionCommunicationElementsBulk(eventId, data);
    }

    public void deleteSessionCommunicationElementsBulk(Long eventId, String language, List<Long> sessionIds) {
        msEventDatasource.deleteSessionCommunicationElementsBulk(eventId, language, sessionIds);
    }

    public Session getSession(Long eventId, Long sessionId) {
        return msEventDatasource.getSession(eventId, sessionId);
    }

    public void upsertSaleConstraints(Long eventId, Long sessionId, SessionSaleConstraint request) {
        msEventDatasource.upsertSaleConstraints(eventId, sessionId, request);
    }

    public SessionSaleConstraint getSaleConstraints(Long eventId, Long sessionId) {
        return msEventDatasource.getSaleConstraints(eventId, sessionId);
    }

    public PriceTypes getPriceTypes(Long eventId, Long sessionId) {
        return msEventDatasource.getPriceTypes(eventId, sessionId);
    }

    public void updatePriceTypes(Long eventId, Long sessionId, Long priceTypeId, PriceType priceType) {
        msEventDatasource.updatePriceTypes(eventId, sessionId, priceTypeId, priceType);
    }

    public SessionRefundConditions getSessionRefundConditions(Long eventId, Long sessionId) {
        return msEventDatasource.getSessionRefundConditions(eventId, sessionId);
    }

    public void updateSessionRefundConditions(final Long eventId, final Long sessionId,
                                              final SessionRefundConditions sessionRefundConditions) {

        msEventDatasource.updateSessionRefundConditions(eventId,sessionId,sessionRefundConditions);
    }

    public Session getSession(Long sessionId) {
        return msEventDatasource.getSession(sessionId);
    }

    public void upsertSaleRestrictions(Long eventId, Long sessionId, Long lockedPriceTypeId, UpdateSaleRestriction request) {
        msEventDatasource.upsertSaleRestrictions(eventId, sessionId, lockedPriceTypeId, request);
    }

    public SessionSaleRestriction getSaleRestrictions(Long eventId, Long sessionId, Long lockedPriceTypeId) {
        return msEventDatasource.getSaleRestriction(eventId, sessionId, lockedPriceTypeId);
    }

    public void deleteRestriction(Long eventId, Long sessionId, Long lockedPriceTypeId) {
        msEventDatasource.deleteRestriction(eventId, sessionId, lockedPriceTypeId);
    }

    public List<IdNameDTO> getSessionRestrictions(Long eventId, Long sessionId) {
        return msEventDatasource.getSessionRestrictions(eventId, sessionId);
    }

    public List<RateRestricted> getSessionRatesRestrictions(Long eventId, Long sessionId) {
        return msEventDatasource.getSessionRatesRestrictions(eventId, sessionId);
    }

    public void upsertSessionRatesRestrictions(Long eventId, Long sessionId, Long rateId, UpdateRateRestrictions restrictions) {
        msEventDatasource.upsertSessionRatesRestrictions(eventId, sessionId, rateId, restrictions);
    }

    public void deleteSessionRateRestrictions(Long eventId, Long sessionId, Long rateId) {
        msEventDatasource.deleteSessionRatesRestrictions(eventId, sessionId, rateId);
    }


    public List<EventCommunicationElement> getChannelSessionCommunicationElements(Long eventId, Long sessionId, Long channelId, CommunicationElementFilter<EventTagType> filter, Predicate<EventTagType> tagType) {
        ChannelContentsUtils.addEventTagsToFilter(filter, tagType);
        return msEventDatasource.getChannelSessionCommunicationElements(eventId, sessionId, channelId, filter);
    }

    public void updateChannelSessionCommunicationElements(Long eventId, Long sessionId, Long channelId, List<EventCommunicationElement> elements) {
        msEventDatasource.updateChannelSessionCommunicationElements(eventId, sessionId, channelId, elements );
    }

    public void updateSessionTaxes(Long sessionId, SeasonTicketTax taxes) {
        msEventDatasource.updateSessionTaxes(sessionId, taxes);
    }
}
