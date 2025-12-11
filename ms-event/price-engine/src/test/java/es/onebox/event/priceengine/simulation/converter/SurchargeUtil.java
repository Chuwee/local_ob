package es.onebox.event.priceengine.simulation.converter;

import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import es.onebox.event.priceengine.surcharges.dto.SurchargeRanges;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SurchargeUtil {

    private SurchargeUtil(){throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    //Surcharge with fixed value for channel and event
    public static ChannelEventSurcharges getEventSurchargesFixedValue(boolean twoRange) {

        ChannelEventSurcharges surcharges = new ChannelEventSurcharges();
        surcharges.setChannel(twoRange ? getSurchargeRangeTwoRange() : getSurchargeRangeOneRange());
        surcharges.setPromoter(twoRange ? getSurchargeRangeTwoRange() : getSurchargeRangeOneRange());
        return surcharges;
    }

    private static SurchargeRanges getSurchargeRangeOneRange() {
        SurchargeRanges surchargeRanges = new SurchargeRanges();
        surchargeRanges.setMain(getListSurchargeRangeOneRange());
        surchargeRanges.setPromotion(getListSurchargeRangeOneRange());
        return surchargeRanges;
    }

    private static SurchargeRanges getSurchargeRangeTwoRange() {
        SurchargeRanges surchargeRanges = new SurchargeRanges();
        surchargeRanges.setMain(getListSurchargeRangeTwoRanges());
        surchargeRanges.setPromotion(getListSurchargeRangeTwoRanges());
        return surchargeRanges;
    }

    private static List<SurchargeRange> getListSurchargeRangeOneRange() {
        return Collections.singletonList(getSurchargeRange(0D, Double.MAX_VALUE, 2D, true));
    }

    private static List<SurchargeRange> getListSurchargeRangeTwoRanges() {
        return Arrays.asList(getSurchargeRange(0D, 9.99D, 2.8, true),
                getSurchargeRange(10D, Double.MAX_VALUE, 3.8, true));
    }

    private static SurchargeRange getSurchargeRange(Double from, Double to, Double value, boolean isFixed) {
        SurchargeRange surchargeRange = new SurchargeRange();
        surchargeRange.setFrom(from);
        if (isFixed) {
            surchargeRange.setFixedValue(value);
        } else {
            surchargeRange.setPercentageValue(value);
        }
        surchargeRange.setTo(to);
        return surchargeRange;
    }

}
