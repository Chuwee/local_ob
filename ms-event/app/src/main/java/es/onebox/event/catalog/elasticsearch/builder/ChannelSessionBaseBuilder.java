package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionCustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelTaxes;
import es.onebox.event.packs.dto.RelatedPackDTO;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;

import java.util.List;
import java.util.Map;

public abstract class ChannelSessionBaseBuilder {

    protected Long channelId;
    protected Long sessionId;
    protected Long eventId;
    protected Boolean forSale;
    protected String timeZone;
    protected ChannelCatalogDates dates;
    protected Boolean mandatoryAttendants;
    protected Boolean mustBeIndexed;
    protected List<Long> productIds;
    protected Map<Long, RelatedPackDTO> relatedPacksByPackId;
    protected Boolean isSeasonPackSession;
    protected Boolean presales;
    protected Boolean preview;
    protected SecondaryMarketConfigDTO secondaryMarketConfig;
    protected SessionDynamicPriceConfig sessionDynamicPriceConfig;
    protected Long venueConfigId;
    protected List<Long> quotas;
    protected ChannelSessionCustomersLimits channelSessionCustomersLimits;
    protected ChannelTaxes channelTaxes;
}
