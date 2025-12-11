package es.onebox.mgmt.venues.utils;

import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReason;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReasonCode;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;

import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateUtils {

    public static boolean isVisitOrThemePark(VenueTemplateType venueTemplateType) {
        return VenueTemplateType.ACTIVITY.equals(venueTemplateType) ||
                VenueTemplateType.THEME_PARK.equals(venueTemplateType);
    }

    public static List<BlockingReason> filterSocialDistancingBlockingReasons(VenueTemplate venueTemplate,
            List<BlockingReason> blockingReasons) {
        // Filter allowed AVET blocking reasons
        if (venueTemplate.getTemplateType().equals(VenueTemplateType.AVET)) {
            return blockingReasons.stream().filter(br -> BlockingReasonCode.SOCIAL_DISTANCING.equals(br.getCode()))
                    .collect(Collectors.toList());
        }
        return blockingReasons;
    }

}
