package es.onebox.event.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterUtilsTest {

    @Test
    public void isByteAsATrueNullTest() {
        Byte param = null;
        Boolean result = ConverterUtils.isByteAsATrue(param);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void isByteAsATrueByteZeroTest() {
        Byte param = Byte.valueOf("0");
        Boolean result = ConverterUtils.isByteAsATrue(param);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void isByteAsATrueByteOneTest() {
        Byte param = Byte.valueOf("1");
        Boolean result = ConverterUtils.isByteAsATrue(param);
        assertEquals(Boolean.TRUE, result);
    }
}
