package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.repository.ProductsRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.ProductDelivery;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.Linia;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;



public class RegisterComplementErpTest extends BaseErpTest {

    @InjectMocks
    private FCBTicketService fcbTicketService;

    @Mock
    private PeopleSoftRepository peopleSoftRepository;

    @Mock
    private MsOrderRepository msOrderRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MsEventRepository msEventRepository;

    @Mock
    private VenueTemplateRepository venueTemplateRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OperationCodeService operationCodeService;

    @Mock
    private IntAvetConfigRepository intAvetConfigRepository;

    @Mock
    private FcbChannelMappingsProperties fcbChannelMappingsProperties;

    @Mock
    private ProductsRepository productsRepository;

    @Mock
    private ChannelCouchDao channelCouchDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fcbTicketService, "fcbEntity", 125L);
    }

    private static String code = "TEST";
    private static String fcbCode = "24012345678V";
    private static Long sessionId = 101L;
    private static Long venueConfigId = 301L;
    private static Integer priceTypeId = 401;
    private static Integer channelId = 50;
    private static String paymentMethod = "TARGETA";
    private static String acquirer = "SABADELL";
    private static String channel = "OB-FUTBOL";

    /*
        Test to check a complement to a AVET session:
        - Session complement: sessionId referenced in field reference
        - Product: sessionId referenced in delivery point
     */

    @Test
    public void sessionComplementOrder() {
        double price = 27d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1.4d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2.1d;
        String priceReference = "PRICE.TYPE.REFERENCE";
        String reference = "2324";

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.NORMAL, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, priceTypeId));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(Integer.valueOf(reference), 2);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(priceReference);

        SessionDTO session = new SessionDTO();
        session.setVenueConfigId(venueConfigId);
        session.setReference(reference);

        SessionDTO sessionReference = new SessionDTO();
        sessionReference.setId(Long.valueOf(reference));
        sessionReference.setEntityId(2L);


        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(Long.valueOf(reference))).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(msEventRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(msEventRepository.getSession(Long.valueOf(reference))).thenReturn(sessionReference);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigId, priceTypeId.longValue())).thenReturn(priceTypeDTO);


        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Long.parseLong(reference) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, priceValue, 1, 1);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue1, promotions1, charges, code, fcbCode, priceReference, codeMatch, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);
    }

    @Test
    public void sessionAndSessionComplementOrder() {
        double price1 = 27d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;
        String reference = "242342"; // Session ID
        String priceTypeCode = "PRICE.TYPE.CODE"; // Session ID

        // product 1
        double price2 = 8d;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price1,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null));

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.NORMAL, price2,
                0d, 0d, 0d,
                0d, 0d,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, priceTypeId));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match1 = getSessionMatch(1, 2);
        SessionMatch match2 = getSessionMatch(Integer.valueOf(reference), 2);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(priceTypeCode);

        SessionDTO session = new SessionDTO();
        session.setVenueConfigId(venueConfigId);
        session.setReference(reference);

        SessionDTO sessionReference = new SessionDTO();
        sessionReference.setId(Long.valueOf(reference));
        sessionReference.setEntityId(2L);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match1);
        Mockito.when(intAvetConfigRepository.getSession(Long.valueOf(reference))).thenReturn(match2);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(msEventRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(msEventRepository.getSession(Long.valueOf(reference))).thenReturn(sessionReference);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigId, priceTypeId.longValue())).thenReturn(priceTypeDTO);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue2 = BigDecimal.valueOf(price2).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = priceValue1.add(BigDecimal.valueOf(price2)).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch1 = String.format("%d-%02d-%03d", 0, Math.abs(match1.getCapacityId()) - 1, match1.getAvetMatchId());
        String codeMatch2 = String.format("%d-%02d-%03d", 0, Math.abs(match2.getCapacityId()) - 1, match2.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 2, 2);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue1, promotions1, charges, code, fcbCode, codeMatch1, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Product
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceValue2, ZERO, charges, code, fcbCode, priceTypeCode, codeMatch2, modeCobrament, "", "", description);
    }

    @Test
    public void sessionAndProductComplementOrder() {
        double price = 27d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        // product 1
        double productPrice = 8d;
        long productId1 = 1000;
        long variantId1 = 2000;
        String sku1 = "SKU0001";
        long sessionReference = 111;
        long eventReference = 112;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null));

        OrderProductDTO product1 = getOrderProductDTO(sessionId, null, EventType.PRODUCT, productPrice,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                null, null, null, null,
                productId1, variantId1, priceTypeId);
        product1.getProductData().setDelivery(new ProductDelivery());
        product1.getProductData().getDelivery().setSessionId(sessionReference);
        orderDTO.getProducts().add(product1);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(1, 2);

        ProductVariant productVariant1 = new ProductVariant();
        productVariant1.setSku(sku1);

        SessionDTO session = new SessionDTO();
        session.setId(sessionReference);
        session.setEventId(eventReference);
        session.setEntityId(2L);

        EventDTO event = new EventDTO();
        event.setId(eventReference);
        event.setType(es.onebox.common.datasources.ms.event.enums.EventType.AVET);


        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(msEventRepository.getSession(sessionReference)).thenReturn(session);
        Mockito.when(msEventRepository.getEvent(eventReference)).thenReturn(event);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match);
        Mockito.when(intAvetConfigRepository.getSession(sessionReference)).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(productsRepository.getProductVariant(productId1, variantId1)).thenReturn(productVariant1);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).add(BigDecimal.valueOf(productPrice)).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal productPriceValue1 = BigDecimal.valueOf(productPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(match.getCapacityId()) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 2, 2);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Product
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, productPriceValue1, ZERO, ZERO, code, fcbCode, sku1, codeMatch, modeCobrament, "", "", description);
    }

}
