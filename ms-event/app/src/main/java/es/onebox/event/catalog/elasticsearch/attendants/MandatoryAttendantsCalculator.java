package es.onebox.event.catalog.elasticsearch.attendants;

import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.events.enums.ChannelSubtype;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MandatoryAttendantsCalculator {

    private static final List<Integer> ATTENDANTS_COMPATIBLE_CHANNELS = Arrays.asList(
            ChannelSubtype.BOX_OFFICE_ONEBOX.getIdSubtipo(),
            ChannelSubtype.PORTAL_WEB.getIdSubtipo(),
            ChannelSubtype.BOX_OFFICE_WEB.getIdSubtipo(),
            ChannelSubtype.PORTAL_B2B.getIdSubtipo()
    );

    private Integer channelId;
    private Integer channelSubtypeId;
    private EventAttendantsConfigDTO eventAttendantsConfig;
    private SessionAttendantsConfigDTO sessionAttendantsConfig;

    private MandatoryAttendantsCalculator() {
        super();
    }

    public static MandatoryAttendantsCalculator init() {
        return new MandatoryAttendantsCalculator();
    }

    public MandatoryAttendantsCalculator channelId(Integer channelId) {
        this.channelId = channelId;
        return this;
    }

    public MandatoryAttendantsCalculator channelSubtypeId(Integer channelSubtypeId) {
        this.channelSubtypeId = channelSubtypeId;
        return this;
    }

    public MandatoryAttendantsCalculator eventAttendantsConfig(EventAttendantsConfigDTO eventAttendantsConfig) {
        this.eventAttendantsConfig = eventAttendantsConfig;
        return this;
    }

    public MandatoryAttendantsCalculator sessionAttendantsConfig(SessionAttendantsConfigDTO sessionAttendantsConfig) {
        this.sessionAttendantsConfig = sessionAttendantsConfig;
        return this;
    }

    public Boolean calculate() {
        return isSesionCanalMandatoryAttendants(channelId, channelSubtypeId, sessionAttendantsConfig, eventAttendantsConfig);
    }

    private static boolean isSesionCanalMandatoryAttendants(
            Integer channelId,
            Integer channelSubtype,
            SessionAttendantsConfigDTO sessionAttendantsConfig,
            EventAttendantsConfigDTO eventAttendantsConfig) {

        return MandatoryAttendantsCalculator.channelSubtypeIsAttendantsCompatible(channelSubtype)
                && MandatoryAttendantsCalculator.channelAttendantsIsMandatory(channelId, sessionAttendantsConfig, eventAttendantsConfig);
    }

    private static boolean channelSubtypeIsAttendantsCompatible(Integer channelSubtype) {
        return Optional.ofNullable(channelSubtype)
                .map(ATTENDANTS_COMPATIBLE_CHANNELS::contains)
                .orElse(false);
    }

    private static boolean channelAttendantsIsMandatory(Integer channelId,
                                                        SessionAttendantsConfigDTO sessionAttendantsConfig,
                                                        EventAttendantsConfigDTO eventAttendantsConfig) {
        if (sessionAttendantsConfig == null) {
            return eventAttendantsConfig != null && BooleanUtils.isTrue(eventAttendantsConfig.getActive()) &&
                    BooleanUtils.isTrue(eventAttendantsConfig.getAllChannelsActive())
                            || (eventAttendantsConfig != null && eventAttendantsConfig.getActiveChannels() != null
                            && eventAttendantsConfig.getActiveChannels().contains(channelId.longValue()));
        }
        return BooleanUtils.isTrue(sessionAttendantsConfig.getActive()) &&
                (BooleanUtils.isTrue(sessionAttendantsConfig.getAllChannelsActive()) ||
                        (sessionAttendantsConfig.getActiveChannels() != null &&
                                sessionAttendantsConfig.getActiveChannels().contains(channelId.longValue())));
    }
}
