package es.onebox.event.priceengine.simulation.util;

import es.onebox.event.priceengine.simulation.domain.Promotion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PriceUtilsTest {

    @Test
    public void testRoundUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PricesUtils.class.getDeclaredMethod("applyPromoToBasePrice", Double.class, Promotion.class);
        method.setAccessible(true);

        var promo = new Promotion();
        promo.setDiscountType(1);
        promo.setPercentualDiscountValue(15d);
        var value = (Double) method.invoke(null, 31.5, promo);

        Assertions.assertEquals(26.77, value);
    }
}
