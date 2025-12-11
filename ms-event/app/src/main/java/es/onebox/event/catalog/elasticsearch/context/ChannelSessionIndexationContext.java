package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;

import java.util.List;

public class ChannelSessionIndexationContext {

    private Long defaultQuotaId;
    private Boolean isActivity;
    private VenueDescriptor venueDescriptor;
    private List<Long>  sessionQuotas;
    private SessionAttendantsConfigDTO sessionAttendants;
    private SessionSecondaryMarketConfigDTO sessionSecondaryMarketConfig;

    public Long getDefaultQuotaId() {
        return defaultQuotaId;
    }

    public Boolean getActivity() {
        return isActivity;
    }

    public SessionAttendantsConfigDTO getSessionAttendants() {
        return sessionAttendants;
    }

    public List<Long> getSessionQuotas() {
        return sessionQuotas;
    }

    public SessionSecondaryMarketConfigDTO getSessionSecondaryMarketConfig() {
        return sessionSecondaryMarketConfig;
    }

    public VenueDescriptor getVenueDescriptor() {
        return venueDescriptor;
    }


    public static final class ChannelSessionIndexationContextBuilder {
        private Long defaultQuotaId;
        private Boolean isActivity;
        private VenueDescriptor venueDescriptor;
        private List<Long> sessionQuotas;
        private SessionAttendantsConfigDTO sessionAttendants;
        private SessionSecondaryMarketConfigDTO sessionSecondaryMarketConfig;

        public ChannelSessionIndexationContextBuilder() {
        }

        public ChannelSessionIndexationContextBuilder(ChannelSessionIndexationContext other) {
            this.defaultQuotaId = other.defaultQuotaId;
            this.isActivity = other.isActivity;
            this.venueDescriptor = other.venueDescriptor;
            this.sessionQuotas = other.sessionQuotas;
            this.sessionAttendants = other.sessionAttendants;
            this.sessionSecondaryMarketConfig = other.sessionSecondaryMarketConfig;
        }

        public static ChannelSessionIndexationContextBuilder builder() {
            return new ChannelSessionIndexationContextBuilder();
        }

        public ChannelSessionIndexationContextBuilder defaultQuotaId(Long defaultQuotaId) {
            this.defaultQuotaId = defaultQuotaId;
            return this;
        }

        public ChannelSessionIndexationContextBuilder isActivity(Boolean isActivity) {
            this.isActivity = isActivity;
            return this;
        }

        public ChannelSessionIndexationContextBuilder venueDescriptor(VenueDescriptor venueDescriptor) {
            this.venueDescriptor = venueDescriptor;
            return this;
        }

        public ChannelSessionIndexationContextBuilder sessionQuotas(List<Long> sessionQuotas) {
            this.sessionQuotas = sessionQuotas;
            return this;
        }

        public ChannelSessionIndexationContextBuilder sessionAttendants(SessionAttendantsConfigDTO sessionAttendants) {
            this.sessionAttendants = sessionAttendants;
            return this;
        }

        public ChannelSessionIndexationContextBuilder sessionSecondaryMarketConfig(SessionSecondaryMarketConfigDTO sessionSecondaryMarketConfig) {
            this.sessionSecondaryMarketConfig = sessionSecondaryMarketConfig;
            return this;
        }

        public ChannelSessionIndexationContext build() {
            ChannelSessionIndexationContext channelSessionIndexationContext = new ChannelSessionIndexationContext();
            channelSessionIndexationContext.sessionAttendants = this.sessionAttendants;
            channelSessionIndexationContext.sessionQuotas = this.sessionQuotas;
            channelSessionIndexationContext.sessionSecondaryMarketConfig = this.sessionSecondaryMarketConfig;
            channelSessionIndexationContext.isActivity = this.isActivity;
            channelSessionIndexationContext.venueDescriptor = this.venueDescriptor;
            channelSessionIndexationContext.defaultQuotaId = this.defaultQuotaId;
            return channelSessionIndexationContext;
        }
    }
}
