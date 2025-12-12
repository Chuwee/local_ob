package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.order.dto.ChannelType;
import es.onebox.common.datasources.ms.order.dto.ClientB2BDTO;
import es.onebox.common.datasources.ms.order.dto.ClientDTO;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.InformativePrices;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderInvoiceDataDTO;
import es.onebox.common.datasources.ms.order.dto.OrderPackDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDataDTO;
import es.onebox.common.datasources.ms.order.dto.OrderStatusDTO;
import es.onebox.common.datasources.ms.order.dto.OrderUserDTO;
import es.onebox.common.datasources.ms.order.enums.OrderState;
import es.onebox.common.datasources.ms.order.enums.OrderType;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.core.serializer.dto.common.IdCodeDTO;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.DadesClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.Linia;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import es.onebox.fcb.utils.FcbDateUtils;
import org.junit.jupiter.api.Assertions;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class BaseErpTest {

    protected static final String TZ_BERLIN = "Europe/Berlin";
    protected static final String GATEWAY_TEST = "TEST_GATEWAY_SID";
    protected static final String GATEWAY_ACCOUNTING = FCBTicketService.ACCOUNTING_PAYMENT;
    protected static final String MUSEO = FCBTicketService.MUSEO;
    protected static final String ANTICIPO = FCBTicketService.ANTICIPO;
    protected static final String PRODUCT_AUDIOGUIA = FCBTicketService.PRODUCT_AUDIOGUIA;
    protected static final String NO_ERP = FCBTicketService.NO_ERP;
    protected static final String BASIC_PLUS_CODE = FCBTicketService.BASIC_PLUS_CODE;
    protected static final String EXTRA_BASIC_PLUS_CODE = FCBTicketService.EXTRA_BASIC_PLUS_CODE;
    protected static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    protected void checkOrder(PeticioFacturar fcb, String code, BigDecimal priceValue, int tickets, int lines) {
        Assertions.assertEquals("0", fcb.getGenerarFactura(), "Failed to check generar factura");
        Assertions.assertEquals(code, fcb.getMessageId(), "Failed to check code");
        Assertions.assertEquals(FCBTicketService.FCB_ONEBOX, fcb.getSistemaOrigen(), "Failed to check generar factura");
        Assertions.assertEquals(priceValue, fcb.getImportTotal(), "Failed to check importe total");
        Assertions.assertEquals(tickets, fcb.getTotalEntrades(), "Failed to check total entradas");
        Assertions.assertEquals(lines, fcb.getLinies().getLinia().size(), "Failed to check linies");
    }

    protected void checkLine(int numLine, Linia linia, String channel, ZonedDateTime date, BigDecimal priceValue, BigDecimal promotions, BigDecimal charges,
                                  String code, String fcbCode, String codeMatch, String complementAnalytic, String modeCobrament, String promotionCode, String clau, String description) {
        Assertions.assertEquals(numLine, linia.getNumLinia(), "Failed to check num linia");
        Assertions.assertEquals(channel, linia.getCanalVenda(), "Failed to check canal venda");
        Assertions.assertEquals(getPurchasedDate(date), linia.getDataOperacio(), "Failed to check data operacio");
        Assertions.assertEquals(priceValue, linia.getPreuVenda().setScale(2, RoundingMode.HALF_UP), "Failed to check preu venda");
        Assertions.assertEquals(promotions, linia.getImportPromocio().setScale(2, RoundingMode.HALF_UP), "Failed to check import promocio");
        Assertions.assertEquals(charges, linia.getImportComisio().setScale(2, RoundingMode.HALF_UP), "Failed to check import comisio");
        Assertions.assertEquals(code, linia.getLocalitzador(), "Failed to check localitzador");
        Assertions.assertEquals(1, linia.getQuantitat(), "Failed to check quantitat");
        Assertions.assertEquals(fcbCode, linia.getIdOperacio(), "Failed to check id operacion");
        Assertions.assertEquals(codeMatch, linia.getIdProducte(), "Failed to check id producto");
        Assertions.assertEquals(complementAnalytic, linia.getComplementAnalitic(), "Failed to check complement analitic");
        Assertions.assertEquals(modeCobrament, linia.getModeCobrament(), "Failed to check mode cobrament");
        Assertions.assertEquals(description, linia.getDescripcioTipusEntrada(), "Failed to check descripcio");
        Assertions.assertEquals(promotionCode, linia.getCodiPromocio(), "Failed to check codi promocio");
        Assertions.assertEquals(clau, linia.getClau(), "Failed to check clau");
    }

    protected SessionMatch getSessionMatch(Integer capacityId, Integer avetMatchId) {
        SessionMatch session = new SessionMatch();
        session.setCapacityId(capacityId);
        session.setAvetMatchId(avetMatchId);
        return session;
    }

    protected PaymentOrder getPaymentOrder(String paymentMethod, String acquirer, String gatewaySid) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setGatewaySid(gatewaySid);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setAcquirer(acquirer);
        return paymentOrder;
    }

    protected OrderProductDTO getOrderProductDTO(Long sessionId, Long eventId, EventType eventType, double price,
                                                 double automatic, double promotion, double discount,
                                                 double channelAutomatic, double channelCollective,
                                                 double channelCharge, double promoterChannelCharge, double promoterCharge,
                                                 String packCode, Long packId, Boolean mainItem, Double informativePrice,
                                                 Long productId, Long variantId, Integer priceTypeId) {
        OrderProductDTO orderProductDTO = new OrderProductDTO();

        if (!EventType.PRODUCT.equals(eventType)) {
            orderProductDTO.setEventType(eventType);
        } else {
            orderProductDTO.setType(ProductType.PRODUCT);
        }
        orderProductDTO.setSessionId(sessionId != null ? sessionId.intValue() : null);
        orderProductDTO.setEventId(eventId != null ? eventId.intValue() : null);
        orderProductDTO.setEventEntityId(125);
        orderProductDTO.getPrice().setBasePrice(price);
        orderProductDTO.getPrice().getPromotions().setAutomatic(automatic);
        orderProductDTO.getPrice().getPromotions().setPromotion(promotion);
        orderProductDTO.getPrice().getPromotions().setDiscount(discount);
        orderProductDTO.getPrice().getPromotions().setChannelAutomatic(channelAutomatic);
        orderProductDTO.getPrice().getPromotions().setChannelCollective(channelCollective);
        orderProductDTO.getPrice().getCharges().setChannel(channelCharge);
        orderProductDTO.getPrice().getCharges().setPromoterChannel(promoterChannelCharge);
        orderProductDTO.getPrice().getCharges().setPromoter(promoterCharge);
        if (informativePrice != null) {
            orderProductDTO.getPrice().setInformativePrices(new InformativePrices());
            orderProductDTO.getPrice().getInformativePrices().setPackItem(informativePrice);
        }
        if (packCode != null) {
            orderProductDTO.setPack(new OrderPackDTO());
            orderProductDTO.getPack().setCode(packCode);
            orderProductDTO.getPack().setId(packId);
            orderProductDTO.getPack().setMainItem(mainItem);
        }
        if (productId != null) {
            orderProductDTO.setProductData(new OrderProductDataDTO());
            orderProductDTO.getProductData().setId(productId);
            orderProductDTO.getProductData().setVariantId(variantId);
        }
        if (priceTypeId != null) {
            orderProductDTO.getTicketData().setPriceZoneId(priceTypeId);
        }

        return orderProductDTO;
    }

    protected OrderDTO getRefundOrderDTO(String code, ZonedDateTime date, Integer channelId, String originalCode) {
        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.getStatus().setType(OrderType.REFUND);
        orderDTO.getStatus().setState(OrderState.PAID);
        orderDTO.setRelatedOriginalCode(originalCode);

        return orderDTO;
    }

    protected OrderDTO getOrderDTO(String code, ZonedDateTime date, Integer channelId) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCode(code);
        orderDTO.setStatus(new OrderStatusDTO());
        orderDTO.getStatus().setType(OrderType.PURCHASE);
        orderDTO.getStatus().setState(OrderState.PAID);
        orderDTO.getDate().setTimeZone(TZ_BERLIN);
        orderDTO.getDate().setPurchased(date);
        orderDTO.getOrderData().setChannelId(channelId);
        orderDTO.getOrderData().setChannelEntityId(channelId + 10);

        orderDTO.setPayments(new ArrayList<>());
        return orderDTO;
    }

    protected OrderDTO getOrderDTOB2C(String code, ZonedDateTime date, Integer channelId) {
        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.setInvoiceData(getInvoiceData());
        orderDTO.setCustomer(getCustomerData());
        orderDTO.setPayments(new ArrayList<>());
        orderDTO.getOrderData().setChannelType(ChannelType.PORTAL);
        return orderDTO;
    }

    protected OrderUserDTO getCustomerData(){
        OrderUserDTO orderUserDTO = new OrderUserDTO();
        orderUserDTO.setEmail("test@oneboxtds.com");
        orderUserDTO.setPhone("123456789");
        orderUserDTO.setName("Test");
        orderUserDTO.setSurname("Onebox");
        return orderUserDTO;
    }

    protected OrderInvoiceDataDTO getInvoiceData() {
        OrderInvoiceDataDTO invoiceData = new OrderInvoiceDataDTO();
        invoiceData.setIdentification("12345678");
        invoiceData.setCity("ES");
        invoiceData.setCountrySubdivision("ES-B");
        invoiceData.setName("Name Test");
        invoiceData.setAddress("Address test");

        return invoiceData;
    }

    protected static RespostaCercaClient getRespostaCercaClient() {
        RespostaCercaClient respostaCercaClient = new RespostaCercaClient();
        DadesClient dadesClient = new DadesClient();
        DadesClient.IdentificadorsOrigen identificadorsOrigen = new DadesClient.IdentificadorsOrigen();
        DadesClient. IdentificadorsOrigen. Identificadors identificadors = new DadesClient.IdentificadorsOrigen.Identificadors();
        identificadorsOrigen.setIdentificadors(identificadors);
        dadesClient.setIdentificadorsOrigen(identificadorsOrigen);
        respostaCercaClient.setDadesClient(dadesClient);
        return respostaCercaClient;
    }

    protected static EntityDTO getEntityDTO(){
        EntityDTO entityDTO = new EntityDTO();
        IdCodeDTO codeDTO = new IdCodeDTO();
        codeDTO.setId(1L);
        codeDTO.setCode("ES_es");
        entityDTO.setLanguage(codeDTO);
        return entityDTO;
    }

    protected ClientDTO getClientDTO(OrderDTO orderDTO, Long clientId) {
        ClientDTO clientDTO = new ClientDTO();
        orderDTO.setClient(clientDTO);
        orderDTO.getClient().setClientB2B(new ClientB2BDTO());
        orderDTO.getClient().getClientB2B().setTaxId("00000001R");
        orderDTO.getClient().getClientB2B().setClientId(clientId);
        return clientDTO;
    }

    protected static ChannelConfigDTO getChannelConfig() {
        ChannelConfigDTO channelConfig = new ChannelConfigDTO();
        //channelConfig.setAdditionalProperties();
        return channelConfig;
    }

    private XMLGregorianCalendar getPurchasedDate(ZonedDateTime date) {
        TimeZone tz = TimeZone.getTimeZone(TZ_BERLIN);
        return DateUtils.convertDateToXMLGregorianCalendar(FcbDateUtils.convertDateTimeZone(Date.from(date.toInstant()), tz));
    }

}
