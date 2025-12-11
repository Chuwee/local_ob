package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.JoinField;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionCustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelTaxes;
import es.onebox.event.catalog.elasticsearch.utils.EventDataUtils;
import es.onebox.event.packs.dto.RelatedPackDTO;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;

import java.util.List;
import java.util.Map;

import static es.onebox.event.catalog.elasticsearch.utils.EventDataUtils.KEY_CHANNEL_SESSION;

public class ChannelSessionDataBuilder extends ChannelSessionBaseBuilder {

    private ChannelSessionDataBuilder() {
        super();
    }

    public static ChannelSessionDataBuilder builder() {
        return new ChannelSessionDataBuilder();
    }

    public ChannelSessionDataBuilder sessionId(Long sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ChannelSessionDataBuilder eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public ChannelSessionDataBuilder channelId(Long channelId) {
        this.channelId = channelId;
        return this;
    }

    public ChannelSessionDataBuilder forSale(Boolean forSale) {
        this.forSale = forSale;
        return this;
    }

    public ChannelSessionDataBuilder timeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public ChannelSessionDataBuilder dates(ChannelCatalogDates dates) {
        this.dates = dates;
        return this;
    }

    public ChannelSessionDataBuilder mandatoryAttendants(Boolean mandatoryAttendants) {
        this.mandatoryAttendants = mandatoryAttendants;
        return this;
    }

    public ChannelSessionDataBuilder mustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
        return this;
    }

    public ChannelSessionDataBuilder productIds(List<Long> productIds) {
        this.productIds = productIds;
        return this;
    }

    public ChannelSessionDataBuilder seasonPackSession(Boolean seasonPackSession) {
        this.isSeasonPackSession = seasonPackSession;
        return this;
    }

    public ChannelSessionDataBuilder presale(Boolean presale) {
        this.presales = presale;
        return this;
    }

    public ChannelSessionDataBuilder preview(Boolean preview) {
        this.preview = preview;
        return this;
    }

    public ChannelSessionDataBuilder secondaryMarketConfig(SecondaryMarketConfigDTO config) {
        this.secondaryMarketConfig = config;
        return this;
    }

    public ChannelSessionDataBuilder venueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
        return this;
    }

    public ChannelSessionDataBuilder quotas(List<Long> quotas) {
        this.quotas = quotas;
        return this;
    }

    public ChannelSessionDataBuilder channelSessionCustomersLimits(ChannelSessionCustomersLimits channelSessionCustomersLimits) {
        this.channelSessionCustomersLimits = channelSessionCustomersLimits;
        return this;
    }

    public ChannelSessionDataBuilder sessionDynamicPriceConfig(SessionDynamicPriceConfig config) {
        this.sessionDynamicPriceConfig = config;
        return this;
    }

    public ChannelSessionDataBuilder relatedPacksByPackId(Map<Long, RelatedPackDTO> relatedPacksByPackId) {
        this.relatedPacksByPackId = relatedPacksByPackId;
        return this;
    }

    public ChannelSessionDataBuilder channelTaxes(ChannelTaxes channelTaxes) {
        this.channelTaxes = channelTaxes;
        return this;
    }

    public ChannelSessionData build() {
        ChannelSessionData channelSessionData = buildChannelSessionData(channelId, sessionId);
        channelSessionData.setMustBeIndexed(this.mustBeIndexed);
        ChannelSession channelSession = new ChannelSession();
        channelSession.setChannelId(channelId);
        channelSession.setSessionId(sessionId);
        channelSession.setEventId(eventId);
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
        channelSession.setSessionDynamicPriceConfig(sessionDynamicPriceConfig);
        channelSession.setVenueConfigId(venueConfigId);
        channelSession.setQuotas(quotas);
        channelSession.setCustomersLimits(channelSessionCustomersLimits);
        channelSession.setChannelTaxes(channelTaxes);
        channelSessionData.setChannelSession(channelSession);
        return channelSessionData;
    }

    public static ChannelSessionData buildChannelSessionData(Long channelId, Long sessionId) {
        ChannelSessionData channelSessionData = new ChannelSessionData();
        channelSessionData.setId(EventDataUtils.getChannelSessionKey(channelId, sessionId));
        channelSessionData.setJoin(new JoinField(KEY_CHANNEL_SESSION, EventDataUtils.getSessionKey(sessionId)));
        return channelSessionData;
    }

}

