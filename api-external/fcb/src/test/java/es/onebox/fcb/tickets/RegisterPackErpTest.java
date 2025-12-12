package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.CategoryDTO;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.repository.ProductsRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaCercaClient;
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

import static org.mockito.ArgumentMatchers.any;

public class RegisterPackErpTest extends BaseErpTest {

    @InjectMocks
    private FCBTicketService fcbTicketService;

    @Mock
    private PeopleSoftRepository peopleSoftRepository;

    @Mock
    private MsOrderRepository msOrderRepository;

    @Mock
    private MsEventRepository msEventRepository;

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
    @Mock
    private ChannelRepository channelRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fcbTicketService, "fcbEntity", 125L);
    }

    private static String code = "TEST";
    private static String packCode = "TEST_PACK";
    private static String fcbCode = "24012345678V";
    private static Long eventId = 201L;
    private static Long sessionId = 101L;
    private static Integer channelId = 50;
    private static String paymentMethod = "TARGETA";
    private static String acquirer = "SABADELL";
    private static String channel = "OB-FUTBOL";

    @Test
    public void sessionAndProductOrder() {
        double price = 25d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        // product 1
        double informativePrice1 = 8d;
        long productId1 = 1000;
        long variantId1 = 2000;
        String sku1 = "SKU0001";

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, null,true, null,
                null, null, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice1,
                productId1, variantId1, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);

        ProductVariant productVariant = new ProductVariant();
        productVariant.setSku(sku1);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(new RespostaCercaClient());
        Mockito.when(productsRepository.getProductVariant(productId1, variantId1)).thenReturn(productVariant);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue1 = BigDecimal.valueOf(price).subtract(BigDecimal.valueOf(informativePrice1)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal informativePriceValue1 = BigDecimal.valueOf(informativePrice1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 2, 2);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Product
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, informativePriceValue1, ZERO, ZERO, code, fcbCode, sku1, null, modeCobrament, "", "", description);
    }

    @Test
    public void sessionAndTwoProductOrder() {
        double price = 40d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        // product 1
        double informativePrice1 = 8d;
        long productId1 = 1000;
        long variantId1 = 2000;
        String sku1 = "SKU0001";

        // product 2
        double informativePrice2 = 10.5d;
        long productId2 = 1001;
        long variantId2 = 2002;
        String sku2 = "SKU0002";

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, true, null,
                null, null, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice1,
                productId1, variantId1, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice2,
                productId2, variantId2, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);

        ProductVariant productVariant1 = new ProductVariant();
        productVariant1.setSku(sku1);

        ProductVariant productVariant2 = new ProductVariant();
        productVariant2.setSku(sku2);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(productsRepository.getProductVariant(productId1, variantId1)).thenReturn(productVariant1);
        Mockito.when(productsRepository.getProductVariant(productId2, variantId2)).thenReturn(productVariant2);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue1 = BigDecimal.valueOf(price).subtract(BigDecimal.valueOf(informativePrice1 + informativePrice2)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal informativePriceValue1 = BigDecimal.valueOf(informativePrice1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal informativePriceValue2 = BigDecimal.valueOf(informativePrice2).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 3, 3);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Product 1
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, informativePriceValue1, ZERO, ZERO, code, fcbCode, sku1, null, modeCobrament, "", "", description);

        // Product 2
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, informativePriceValue2, ZERO, ZERO, code, fcbCode, sku2, null, modeCobrament, "", "", description);
    }

    @Test
    public void museoAndTwoProductOrder() {
        double price = 40d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        // product 1
        double informativePrice1 = 8d;
        long productId1 = 1000;
        long variantId1 = 2000;
        String sku1 = "SKU0001";

        // product 2
        double informativePrice2 = 10.5d;
        long productId2 = 1001;
        long variantId2 = 2002;
        String sku2 = "SKU0002";

        // museo
        double museoPrice = 2d;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, eventId, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, true, null,
                null, null, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice1,
                productId1, variantId1, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice2,
                productId2, variantId2, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);

        ProductVariant productVariant1 = new ProductVariant();
        productVariant1.setSku(sku1);

        ProductVariant productVariant2 = new ProductVariant();
        productVariant2.setSku(sku2);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setCategory(new CategoryDTO());
        eventDTO.getCategory().setCode(MUSEO);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(msEventRepository.getEvent(eventId)).thenReturn(eventDTO);
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(productsRepository.getProductVariant(productId1, variantId1)).thenReturn(productVariant1);
        Mockito.when(productsRepository.getProductVariant(productId2, variantId2)).thenReturn(productVariant2);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue1 = BigDecimal.valueOf(price).subtract(BigDecimal.valueOf(informativePrice1 + informativePrice2 + museoPrice)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal informativePriceValue1 = BigDecimal.valueOf(informativePrice1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal informativePriceValue2 = BigDecimal.valueOf(informativePrice2).setScale(2, RoundingMode.HALF_UP);
        BigDecimal museuPriceValue = BigDecimal.valueOf(museoPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 4, 4);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Product 1
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, informativePriceValue1, ZERO, ZERO, code, fcbCode, sku1, null, modeCobrament, "", "", description);

        // Product 2
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, informativePriceValue2, ZERO, ZERO, code, fcbCode, sku2, null, modeCobrament, "", "", description);

        // Museo
        Linia museo = fcb.getLinies().getLinia().get(3);
        checkLine(4, museo, channel, date, museuPriceValue, ZERO, ZERO, code, fcbCode, PRODUCT_AUDIOGUIA, null, modeCobrament, "", "", description);
    }

    @Test
    public void museoAndProductAndNoErpOrder() {
        double price = 40d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        // product 1
        double informativePrice1 = 8d;
        long productId1 = 1000;
        long variantId1 = 2000;
        String sku1 = "SKU0001";

        // product 2
        double informativePrice2 = 10.5d;
        long productId2 = 1001;
        long variantId2 = 2002;
        String sku2 = NO_ERP;

        // museo
        double museoPrice = 2d;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.getProducts().add(getOrderProductDTO(sessionId, eventId, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, true, null,
                null, null, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice1,
                productId1, variantId1, null));

        orderDTO.getProducts().add(getOrderProductDTO(null, null, EventType.PRODUCT, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge, promoterChannelCharge, promoterCharge,
                packCode, null, false, informativePrice2,
                productId2, variantId2, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);

        ProductVariant productVariant1 = new ProductVariant();
        productVariant1.setSku(sku1);

        ProductVariant productVariant2 = new ProductVariant();
        productVariant2.setSku(sku2);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setCategory(new CategoryDTO());
        eventDTO.getCategory().setCode(MUSEO);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(msEventRepository.getEvent(eventId)).thenReturn(eventDTO);
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(productsRepository.getProductVariant(productId1, variantId1)).thenReturn(productVariant1);
        Mockito.when(productsRepository.getProductVariant(productId2, variantId2)).thenReturn(productVariant2);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue1 = BigDecimal.valueOf(price).subtract(BigDecimal.valueOf(informativePrice1 + museoPrice)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal informativePriceValue1 = BigDecimal.valueOf(informativePrice1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal museuPriceValue1 = BigDecimal.valueOf(museoPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 3, 3);

        // Session
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Product 1
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, informativePriceValue1, ZERO, ZERO, code, fcbCode, sku1, null, modeCobrament, "", "", description);

        // Museo
        Linia museo = fcb.getLinies().getLinia().get(2);
        checkLine(3, museo, channel, date, museuPriceValue1, ZERO, ZERO, code, fcbCode, PRODUCT_AUDIOGUIA, null, modeCobrament, "", "", description);
    }

}
