package es.onebox.event.priceengine.simulation.util;

import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PromotionUtils {

    private static final String GROUP_DELIMITER = "\\|\\|";

    private PromotionUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<EventPromotionRecord> filterByChannelId(List<EventPromotionRecord> promotionsRecord, Integer channelId) {
        Boolean existPromoByChannels = promotionsRecord.stream().filter(Objects::nonNull)
                .anyMatch(promo -> promo.getSelectedChannels() != null && promo.getSelectedChannels().equals(1));
        if (BooleanUtils.isTrue(existPromoByChannels)) {
            List<EventPromotionRecord> promotionsRecordFiltered = new ArrayList<>();
            for (EventPromotionRecord promo : promotionsRecord) {
                List<Integer> selectedChannels = getSelectedChannels(promo.getChannels());
                if (promo.getSelectedChannels().equals(0) || (promo.getSelectedChannels().equals(1)  && selectedChannels.contains(channelId))) {
                    promotionsRecordFiltered.add(promo);
                }
            }
            return promotionsRecordFiltered;
        }
        return promotionsRecord;
    }

    private static List<Integer> getSelectedChannels(String channels) {
        if (StringUtils.isNotBlank(channels)) {
            return Arrays.stream(channels.split(GROUP_DELIMITER)).map(Integer::valueOf).toList();
        }
        return Collections.emptyList();
    }

}
