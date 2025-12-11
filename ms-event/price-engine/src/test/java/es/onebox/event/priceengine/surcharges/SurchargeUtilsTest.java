package es.onebox.event.priceengine.surcharges;

import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SurchargeUtilsTest {

    @Test
    public void testSurchargeRangeCalculation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SurchargeUtils.class.getDeclaredMethod("calculateSurcharge", Double.class, List.class);
        method.setAccessible(true);

        var ranges = new ArrayList<SurchargeRange>();
        var range = new SurchargeRange();
        range.setFixedValue(null);
        range.setPercentageValue(10d);
        range.setFrom(0d);
        range.setTo(Double.MAX_VALUE);
        range.setMinimumValue(1d);
        range.setMaximumValue(1.5d);

        ranges.add(range);

        var value = (Double) method.invoke(null, 9d, ranges);
        Assertions.assertEquals(1d, value);

         value = (Double) method.invoke(null, 12d, ranges);
        Assertions.assertEquals(1.2d, value);

         value = (Double) method.invoke(null, 16d, ranges);
        Assertions.assertEquals(1.5d, value);
    }
}
