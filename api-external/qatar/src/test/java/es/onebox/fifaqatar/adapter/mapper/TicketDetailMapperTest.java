package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.dto.response.session.secmkt.SessionSecMktConfig;
import es.onebox.common.datasources.orderitems.dto.Barcode;
import es.onebox.common.datasources.orderitems.dto.ItemTicket;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.dto.validation.ItemTicketValidation;
import es.onebox.common.datasources.orders.dto.ItemPrice;
import es.onebox.common.datasources.orders.dto.TicketAllocation;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.fifaqatar.BaseTest;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketCode;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketCodeValidity;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketManagement;
import es.onebox.fifaqatar.config.config.DeliverySettings;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TicketDetailMapperTest extends BaseTest {

    private final TicketDetailMapper mapper = mock(TicketDetailMapper.class, CALLS_REAL_METHODS);

    @Test
    void test_mapTicketPrice() {
        List<BigDecimal> finalPrice = List.of(BigDecimal.valueOf(12.3d), BigDecimal.valueOf(10d), BigDecimal.valueOf(101.45d));
        var mockedPriceItems = finalPrice.stream().map(price -> {
            var item = new OrderItem();
            var itemPrice = new ItemPrice();
            itemPrice.setFinalAmount(price);
            item.setPrice(itemPrice);

            return item;
        }).collect(Collectors.toList());

        BigDecimal totalPrice = mapper.mapTicketPrice(mockedPriceItems);

        assertEquals(BigDecimal.valueOf(123.75d), totalPrice);
    }

    @Test
    void test_mapManagement() throws IOException {
        final Long SESSION_ID = 100L;
        MapperContext config = new MapperContext();
        config.setCustomerAccessToken("JWT");
        config.setCurrentLang("en-US");
        config.setAccountTicketsUrl("https://test.com/account");
        config.setDictionary(getDictionary());

        TicketManagement management = mapper.mapManagement(config, SESSION_ID, "CODE");

        assertEquals("https://test.com/account/100?hl=en-US&orderCode=CODE&token=JWT", management.getUrl());
    }

    @Test
    void test_mapTransferManagement() throws IOException {
        final Long SESSION_ID = 100L;
        MapperContext config = new MapperContext();
        config.setCustomerAccessToken("JWT");
        config.setCurrentLang("en-US");
        config.setAccountTicketsTransferUrl("https://test.com/account");
        config.setDictionary(getDictionary());

        TicketManagement management = mapper.mapTransferManagement(config, SESSION_ID, "CODE");

        assertEquals("https://test.com/account/100?hl=en-US&orderCode=CODE&token=JWT", management.getUrl());
    }

    @Test
    void test_mapSecMktManagement() throws IOException {
        FifaQatarConfigDocument configDocument = mock(FifaQatarConfigDocument.class);
        when(configDocument.getSecMktEnabled()).thenReturn(Boolean.TRUE);

        SessionSecMktConfig secMktConfig = mock(SessionSecMktConfig.class);
        when(secMktConfig.getEnabled()).thenReturn(Boolean.TRUE);

        MapperContext mapperContext = mock(MapperContext.class);
        when(mapperContext.getMainConfig()).thenReturn(configDocument);
        when(mapperContext.getSessionSecMktConfig()).thenReturn(secMktConfig);
        when(mapperContext.getCustomerAccessToken()).thenReturn("JWT");
        when(mapperContext.getCurrentLang()).thenReturn("en-US");
        when(mapperContext.getAccountSecMktUrl()).thenReturn("https://test.com/account");
        when(mapperContext.getDictionary()).thenReturn(getDictionary());

        TicketManagement management = mapper.maSecMktManagement(mapperContext);
        assertEquals("https://test.com/account?hl=en-US&token=JWT", management.getUrl());

        when(mapperContext.getMainConfig().getSecMktEnabled()).thenReturn(null);
        management = mapper.maSecMktManagement(mapperContext);
        assertNull(management);

        when(mapperContext.getMainConfig().getSecMktEnabled()).thenReturn(Boolean.FALSE);
        management = mapper.maSecMktManagement(mapperContext);
        assertNull(management);

        when(mapperContext.getMainConfig().getSecMktEnabled()).thenReturn(Boolean.TRUE);
        when(secMktConfig.getEnabled()).thenReturn(Boolean.FALSE);
        management = mapper.maSecMktManagement(mapperContext);
        assertNull(management);

        when(mapperContext.getMainConfig().getSecMktEnabled()).thenReturn(Boolean.TRUE);
        when(mapperContext.getSessionSecMktConfig()).thenReturn(null);
        management = mapper.maSecMktManagement(mapperContext);
        assertNull(management);
    }

    @Test
    void test_mullFillInfo() {
        Customer customer = new Customer();
        assertTrue(mapper.mustFillInfo(customer));

        customer.setAdditionalProperties(new HashMap<>());
        assertTrue(mapper.mustFillInfo(customer));

        customer.getAdditionalProperties().put("nationality", null);
        customer.getAdditionalProperties().put("your_team", null);
        assertTrue(mapper.mustFillInfo(customer));

        customer.getAdditionalProperties().put("nationality", "ES");
        assertTrue(mapper.mustFillInfo(customer));

        customer.getAdditionalProperties().put("nationality", null);
        customer.getAdditionalProperties().put("your_team", "SPAIN");
        assertTrue(mapper.mustFillInfo(customer));

        customer.getAdditionalProperties().put("nationality", "ES");
        customer.getAdditionalProperties().put("your_team", "SPAIN");
        assertFalse(mapper.mustFillInfo(customer));
    }

    @Test
    void test_buildProfileUrl() {
        MapperContext mapperContext = new MapperContext();
        mapperContext.setAccountProfileUrl("https://test.com");
        mapperContext.setCurrentLang("en-US");
        mapperContext.setCustomerAccessToken("JWT");

        String profileUrl = mapper.buildProfileUrl(mapperContext);
        assertEquals("https://test.com?hl=en-US&token=JWT", profileUrl);
    }

    @Test
    void test_buildBarcodeUrl() {
        MapperContext mapperContext = new MapperContext();
        mapperContext.setBarcodeUrl("https://test.com");
        mapperContext.setBarcodeSigningKey("KEY");
        var signature = GeneratorUtils.getHashSHA256("ABCDEF" + mapperContext.getBarcodeSigningKey());

        String url = mapper.buildBarcodeUrl(mapperContext, "ABCDEF");
        assertEquals("https://test.com/ABCDEF?signature=" + signature, url);
    }

    @Test
    void test_buildCodes() {
        MapperContext mapperContext = new MapperContext();
        mapperContext.setBarcodeUrl("https://test.com");
        mapperContext.setBarcodeSigningKey("KEY");

        List<OrderItem> mockedItems = List.of("AAA", "BBB", "CCC").stream().map(code -> {
            OrderItem item = new OrderItem();
            ItemTicket itemTicket = new ItemTicket();
            Barcode barcode = new Barcode();
            barcode.setCode(code);
            itemTicket.setBarcode(barcode);
            ;
            item.setTicket(itemTicket);

            return item;
        }).collect(Collectors.toList());

        List<TicketCode> ticketCodes = mapper.mapCodes(mockedItems, mapperContext);
        assertEquals(3, ticketCodes.size());
        ticketCodes.forEach(ticketCode -> {
            var signature = GeneratorUtils.getHashSHA256(ticketCode.getCode() + mapperContext.getBarcodeSigningKey());
            assertEquals("https://test.com/" + ticketCode.getCode() + "?signature=" + signature, ticketCode.getImage());
        });
    }

    @Test
    void test_isDeliveryActive() {
        MapperContext mapperContext = mock(MapperContext.class);

        FifaQatarConfigDocument configDocument = mock(FifaQatarConfigDocument.class);
        when(mapperContext.getMainConfig()).thenReturn(configDocument);

        DeliverySettings settings = new DeliverySettings();
        settings.setEnabled(Boolean.TRUE);
        settings.setHoursBefore(24);
        when(mapperContext.getMainConfig().getDeliverySettings()).thenReturn(settings);

        SessionCatalog sessionCatalog = mock(SessionCatalog.class);
        when(mapperContext.getSessionCatalog()).thenReturn(sessionCatalog);


        //Session in range
        when(mapperContext.getSessionCatalog().getBeginSessionDate()).thenReturn(ZonedDateTime.now().plusHours(23).toInstant().toEpochMilli());
        when(mapperContext.getSessionCatalog().getEndSessionDate()).thenReturn(ZonedDateTime.now().plusHours(27).toInstant().toEpochMilli());
        assertFalse(mapper.isDeliveryActive(mapperContext));

        //Session out of range
        when(mapperContext.getSessionCatalog().getBeginSessionDate()).thenReturn(ZonedDateTime.now().plusHours(25).toInstant().toEpochMilli());
        when(mapperContext.getSessionCatalog().getEndSessionDate()).thenReturn(ZonedDateTime.now().plusHours(27).toInstant().toEpochMilli());
        assertTrue(mapper.isDeliveryActive(mapperContext));

        //Session WIP
        when(mapperContext.getSessionCatalog().getBeginSessionDate()).thenReturn(ZonedDateTime.now().minusHours(25).toInstant().toEpochMilli());
        when(mapperContext.getSessionCatalog().getEndSessionDate()).thenReturn(ZonedDateTime.now().plusHours(27).toInstant().toEpochMilli());
        assertFalse(mapper.isDeliveryActive(mapperContext));

        //Session finished
        when(mapperContext.getSessionCatalog().getBeginSessionDate()).thenReturn(ZonedDateTime.now().minusHours(48).toInstant().toEpochMilli());
        when(mapperContext.getSessionCatalog().getEndSessionDate()).thenReturn(ZonedDateTime.now().minusHours(3).toInstant().toEpochMilli());
        assertFalse(mapper.isDeliveryActive(mapperContext));

        settings.setEnabled(Boolean.FALSE);
        assertFalse(mapper.isDeliveryActive(mapperContext));
    }

    @Test
    void test_buildExtraInfo() throws IOException {
        var dictionary = getDictionary();

        MapperContext context = new MapperContext();
        context.setDictionary(dictionary);
        context.setCurrentLang("en-GB");

        OrderItem item = new OrderItem();
        item.setTicket(new ItemTicket());
        item.getTicket().setAllocation(new TicketAllocation());
        item.getTicket().getAllocation().setAccessibility(null);

        String nullResult = mapper.mapExtraInfo(List.of(item), context);
        Assertions.assertNull(nullResult);

        item.getTicket().getAllocation().setAccessibility("NORMAL");
        String noMatchResult = mapper.mapExtraInfo(List.of(item), context);
        Assertions.assertNull(noMatchResult);

        item.getTicket().getAllocation().setAccessibility("DISABILITY");
        String withResult = mapper.mapExtraInfo(List.of(item), context);
        Assertions.assertNotNull(withResult);
    }

    @Test
    void test_codeValidity() {
        OrderItem item = new OrderItem();
        item.setTicket(new ItemTicket());

        item.getTicket().setValidations(new ArrayList<>());

        ItemTicketValidation ticketValidation = new ItemTicketValidation();
        ticketValidation.setDate(ZonedDateTime.now());
        ticketValidation.setStatus("VALIDATED");
        item.getTicket().getValidations().add(ticketValidation);

        TicketCodeValidity codeValidity = mapper.buildCodeValidity(item);
        Assertions.assertNotNull(codeValidity);
        assertEquals(0, codeValidity.getRemainingUses());

        item.getTicket().setValidations(new ArrayList<>());

        codeValidity = mapper.buildCodeValidity(item);
        Assertions.assertNull(codeValidity);

        ItemTicketValidation validatedOutValidation = new ItemTicketValidation();
        validatedOutValidation.setDate(ZonedDateTime.now().plusMinutes(5));
        validatedOutValidation.setStatus("VALIDATED_OUT");
        item.getTicket().getValidations().add(ticketValidation);
        item.getTicket().getValidations().add(validatedOutValidation);

        codeValidity = mapper.buildCodeValidity(item);
        Assertions.assertNull(codeValidity);


    }
}
