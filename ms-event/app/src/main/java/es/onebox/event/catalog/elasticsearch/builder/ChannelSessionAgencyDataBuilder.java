package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelTaxes;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.packs.dto.RelatedPackDTO;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_CHANNEL_SESSION_AGENCY;

public class ChannelSessionAgencyDataBuilder extends ChannelSessionBaseBuilder {

    private Long agencyId;
    private Map<Long, Set<String>> priceTypeTags;

    private ChannelSessionAgencyDataBuilder() {
        super();
    }

    public static ChannelSessionAgencyDataBuilder builder() {
        return new ChannelSessionAgencyDataBuilder();
    }

    public ChannelSessionAgencyDataBuilder sessionId(Long sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ChannelSessionAgencyDataBuilder eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public ChannelSessionAgencyDataBuilder agencyId(Long agencyId) {
        this.agencyId = agencyId;
        return this;
    }

    public ChannelSessionAgencyDataBuilder channelId(Long channelId) {
        this.channelId = channelId;
        return this;
    }

    public ChannelSessionAgencyDataBuilder forSale(Boolean forSale) {
        this.forSale = forSale;
        return this;
    }

    public ChannelSessionAgencyDataBuilder timeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public ChannelSessionAgencyDataBuilder dates(ChannelCatalogDates dates) {
        this.dates = dates;
        return this;
    }

    public ChannelSessionAgencyDataBuilder mandatoryAttendants(Boolean mandatoryAttendants) {
        this.mandatoryAttendants = mandatoryAttendants;
        return this;
    }

    public ChannelSessionAgencyDataBuilder mustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
        return this;
    }

    public ChannelSessionAgencyDataBuilder productIds(List<Long> productIds) {
        this.productIds = productIds;
        return this;
    }

    public ChannelSessionAgencyDataBuilder relatedPacksByPackId(Map<Long, RelatedPackDTO> relatedPacksByPackId) {
        this.relatedPacksByPackId = relatedPacksByPackId;
        return this;
    }

    public ChannelSessionAgencyDataBuilder seasonPackSession(Boolean seasonPackSession) {
        this.isSeasonPackSession = seasonPackSession;
        return this;
    }

    public ChannelSessionAgencyDataBuilder presale(Boolean presale) {
        this.presales = presale;
        return this;
    }

    public ChannelSessionAgencyDataBuilder preview(Boolean preview) {
        this.preview = preview;
        return this;
    }

    public ChannelSessionAgencyDataBuilder secondaryMarketConfig(SecondaryMarketConfigDTO config) {
        this.secondaryMarketConfig = config;
        return this;
    }

    public ChannelSessionAgencyDataBuilder venueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
        return this;
    }

    public ChannelSessionAgencyDataBuilder quotas(List<Long> quotas) {
        this.quotas = quotas;
        return this;
    }

    public ChannelSessionAgencyDataBuilder priceTypeTags(Map<Long, Set<String>> priceTypeTags) {
        this.priceTypeTags = priceTypeTags;
        return this;
    }

    public ChannelSessionAgencyDataBuilder channelTaxes(ChannelTaxes channelTaxes) {
        this.channelTaxes = channelTaxes;
        return this;
    }


    public ChannelSessionAgencyData build() {
        ChannelSessionAgencyData response = buildChannelSessionAgencyData(channelId, sessionId, agencyId);
        response.setMustBeIndexed(this.mustBeIndexed);
        ChannelSessionAgency channelSession = new ChannelSessionAgency();
        channelSession.setChannelId(channelId);
        channelSession.setEventId(eventId);
        channelSession.setSessionId(sessionId);
        channelSession.setForSale(forSale);
        channelSession.setDate(dates);
        channelSession.setTimeZone(timeZone);
        channelSession.setMandatoryAttendants(mandatoryAttendants);
        channelSession.setProductIds(productIds);
        channelSession.setRelatedPacksByPackId(relatedPacksByPackId);
        channelSession.setSeasonPackSession(isSeasonPackSession);
        channelSession.setPresale(this.presales);
        channelSession.setPreview(preview);
        channelSession.setSecondaryMarketConfig(secondaryMarketConfig);
        channelSession.setVenueConfigId(venueConfigId);
        channelSession.setQuotas(quotas);
        channelSession.setAgencyId(agencyId);
        channelSession.setPriceTypeTags(priceTypeTags);
        response.setChannelSessionAgency(channelSession);

        return response;
    }

    public static ChannelSessionAgencyData buildChannelSessionAgencyData(Long channelId, Long sessionId, Long agencyId) {
        ChannelSessionAgencyData response = new ChannelSessionAgencyData();
        response.setId(EventDataUtils.getChannelSessionAgencyKey(channelId, sessionId, agencyId));
        response.setJoin(new JoinField(KEY_CHANNEL_SESSION_AGENCY, EventDataUtils.getSessionKey(sessionId)));
        return response;
    }
}
