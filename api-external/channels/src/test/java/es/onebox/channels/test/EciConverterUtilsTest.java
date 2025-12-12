package es.onebox.channels.test;

import es.onebox.channels.catalog.eci.ECIConverterUtils;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.core.serializer.mapper.JsonMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class EciConverterUtilsTest {

    @Test
    public void overridePricesTest() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("session-prices.json");

        SessionPrices sessionPrices = JsonMapper.jacksonMapper().readValue(resourceAsStream, SessionPrices.class);
        ChannelSession channelSession = new ChannelSession();
        ECIConverterUtils.overrideMinPrice(channelSession, sessionPrices);

        Assertions.assertEquals(10d,channelSession.getPrice().getMin().getValue());
        Assertions.assertEquals(5d, channelSession.getPrice().getMin().getSurcharge().getPromoter());
        Assertions.assertEquals(0d, channelSession.getPrice().getMin().getSurcharge().getChannel());

    }
}
