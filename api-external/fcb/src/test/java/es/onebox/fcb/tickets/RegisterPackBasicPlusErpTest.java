package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.PackDTO;
import es.onebox.common.datasources.ms.event.enums.PackPricingType;
import es.onebox.common.datasources.ms.event.dto.CategoryDTO;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.repository.PackRepository;
import es.onebox.common.datasources.ms.event.repository.ProductsRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderPriceDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.core.utils.common.NumberUtils;
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


/**
 +------------------------+-----------------------+------------------------------------------------------------+-----------------------------+-----+
 |       Basic Plus       |                       | ERP code                                                   |                             |     |
 +------------------------+-----------------------+------------------------------------------------------------+-----------------------------+-----+
 | Entrada partido Futbol | Sesión                |                                                            | Pack incremental - Total    | 21% |
 | Entrada Flexible Museu | Sesión                | PACK.FLEXIBLE TOUR                                         | 12€                         | 10% |
 | Pack merchandising     | Producto              | BLM REGULAR PLUS,BLM REGULAR PLUS CHL,BLM REGULAR PLUS AMT | 7,5                         | 21% |
 | Extra basic Plus       | Producto Virtual ERP  | EXTRAPLUS,EXTRAPLUS CHL,EXTRAPLUS AMT                      | Total Extra Basic Plus - PA | 21% |
 +------------------------+-----------------------+------------------------------------------------------------+-----------------------------+-----+
 Where ERP codes are?
 - session -> partido AVET (calculate code)
 - pack session -> ERP code in price type code, (productprice - incremental pack price)
 - pack product -> ERP code in variant reference, price in product variant
 */
public class RegisterPackBasicPlusErpTest extends BaseErpTest {

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
    private VenueTemplateRepository venueTemplateRepository;

    @Mock
    private PackRepository packRepository;

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
    public void basicPlusIncremental() {
        double price = 65d;
        double automatic = 0.42d;
        double promotion = 0.33d;
        double discount = 1.12d;
        double channelAutomatic = 1.4d;
        double channelCollective = 2d;
        double channelCharge = 0.6d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        double incrementalPrice = 55d;
        long packId = 105;

        // Session -> Entrada Flexible Museu
        String productEFM = "PACK.FLEXIBLE TOUR";
        double priceEFM = 12d;
        long venueConfigIdEFM = 44L;
        long sessionIdEFM = 55L;
        Integer priceTypeIdEFM = 66;

        // Session -> Pack merchandising
        String productPM = BASIC_PLUS_CODE;
        double informativePricePM = 7.5d;
        long productIdPM = 1001;
        long variantIdPM = 2002;

        // Virtual -> Extra basic Plus
        String productEBP = EXTRA_BASIC_PLUS_CODE;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        // Session -> AVET
        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, packId, true, null,
                null, null, null));

        // Session -> Entrada Flexible Museu
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.ACTIVITY, price,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, priceEFM,
                null, null, priceTypeIdEFM));

        // Session -> Pack merchandising
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.PRODUCT, price,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, informativePricePM,
                productIdPM, variantIdPM, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(1, 2);

        SessionDTO sessionEFM = new SessionDTO();
        sessionEFM.setVenueConfigId(venueConfigIdEFM);
        sessionEFM.setReference(productEFM);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(productEFM);

        ProductVariant productVariantPM = new ProductVariant();
        productVariantPM.setSku(productPM);

        PackDTO packDTO = new PackDTO();
        packDTO.setPriceIncrement(incrementalPrice);
        packDTO.setPricingType(PackPricingType.INCREMENTAL);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(Mockito.any())).thenReturn(new RespostaCercaClient());
        Mockito.when(msEventRepository.getSession(sessionIdEFM)).thenReturn(sessionEFM);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigIdEFM, priceTypeIdEFM.longValue())).thenReturn(priceTypeDTO);
        Mockito.when(productsRepository.getProductVariant(productIdPM, variantIdPM)).thenReturn(productVariantPM);
        Mockito.when(packRepository.getPack(packId)).thenReturn(packDTO);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue = BigDecimal.valueOf(NumberUtils.minus(price, incrementalPrice)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEFMValue = BigDecimal.valueOf(priceEFM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePMValue = BigDecimal.valueOf(informativePricePM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEBPValue = BigDecimal.valueOf(NumberUtils.minus(incrementalPrice, priceEFM, informativePricePM)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(match.getCapacityId()) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 4, 4);

        // Session -> AVET
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Session -> Entrada Flexible Museu
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceEFMValue, ZERO, ZERO, code, fcbCode, productEFM, null, modeCobrament, "", "", description);

        // Session -> Pack merchandising
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, pricePMValue, ZERO, ZERO, code, fcbCode, productPM, null, modeCobrament, "", "", description);

        // Virtual -> Extra basic Plus
        linia = fcb.getLinies().getLinia().get(3);
        checkLine(4, linia, channel, date, priceEBPValue, ZERO, ZERO, code, fcbCode, productEBP, null, modeCobrament, "", "", description);
    }

    @Test
    public void basicPlusCHLIncremental() {
        double price = 65d;
        double automatic = 0.42d;
        double promotion = 0.33d;
        double discount = 1.12d;
        double channelAutomatic = 1.4d;
        double channelCollective = 2d;
        double channelCharge = 0.6d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        double incrementalPrice = 55d;
        long packId = 105;

        // Session -> Entrada Flexible Museu
        String productEFM = "PACK.FLEXIBLE TOUR";
        double priceEFM = 12d;
        long venueConfigIdEFM = 44L;
        long sessionIdEFM = 55L;
        Integer priceTypeIdEFM = 66;

        // Session -> Pack merchandising
        String productPM = BASIC_PLUS_CODE + " CHL";
        double informativePricePM = 7.5d;
        long productIdPM = 1001;
        long variantIdPM = 2002;

        // Virtual -> Extra basic Plus
        String productEBP = EXTRA_BASIC_PLUS_CODE + " CHL";

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        // Session -> AVET
        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, packId, true, null,
                null, null, null));

        // Session -> Entrada Flexible Museu
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.ACTIVITY, price,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, priceEFM,
                null, null, priceTypeIdEFM));

        // Session -> Pack merchandising
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.PRODUCT, price,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, informativePricePM,
                productIdPM, variantIdPM, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(1, 2);

        SessionDTO sessionEFM = new SessionDTO();
        sessionEFM.setVenueConfigId(venueConfigIdEFM);
        sessionEFM.setReference(productEFM);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(productEFM);

        ProductVariant productVariantPM = new ProductVariant();
        productVariantPM.setSku(productPM);

        PackDTO packDTO = new PackDTO();
        packDTO.setPriceIncrement(incrementalPrice);
        packDTO.setPricingType(PackPricingType.INCREMENTAL);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(Mockito.any())).thenReturn(new RespostaCercaClient());
        Mockito.when(msEventRepository.getSession(sessionIdEFM)).thenReturn(sessionEFM);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigIdEFM, priceTypeIdEFM.longValue())).thenReturn(priceTypeDTO);
        Mockito.when(productsRepository.getProductVariant(productIdPM, variantIdPM)).thenReturn(productVariantPM);
        Mockito.when(packRepository.getPack(packId)).thenReturn(packDTO);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue = BigDecimal.valueOf(NumberUtils.minus(price, incrementalPrice)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEFMValue = BigDecimal.valueOf(priceEFM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePMValue = BigDecimal.valueOf(informativePricePM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEBPValue = BigDecimal.valueOf(NumberUtils.minus(incrementalPrice, priceEFM, informativePricePM)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(match.getCapacityId()) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 4, 4);

        // Session -> AVET
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Session -> Entrada Flexible Museu
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceEFMValue, ZERO, ZERO, code, fcbCode, productEFM, null, modeCobrament, "", "", description);

        // Session -> Pack merchandising
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, pricePMValue, ZERO, ZERO, code, fcbCode, productPM, null, modeCobrament, "", "", description);

        // Virtual -> Extra basic Plus
        linia = fcb.getLinies().getLinia().get(3);
        checkLine(4, linia, channel, date, priceEBPValue, ZERO, ZERO, code, fcbCode, productEBP, null, modeCobrament, "", "", description);
    }

    @Test
    public void basicPlusAMTAndMuseuIncremental() {
        double price = 65d;
        double automatic = 0.42d;
        double promotion = 0.33d;
        double discount = 1.12d;
        double channelAutomatic = 1.4d;
        double channelCollective = 2d;
        double channelCharge = 0.6d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        double incrementalPrice = 55d;
        long packId = 105;

        // Session -> Entrada Flexible Museu
        String productEFM = "PACK.FLEXIBLE TOUR";
        double priceEFM = 12d;
        long venueConfigIdEFM = 44L;
        long sessionIdEFM = 55L;
        Integer priceTypeIdEFM = 66;

        // Session -> Pack merchandising
        String productPM = BASIC_PLUS_CODE + " AMT";
        double informativePricePM = 7.5d;
        long productIdPM = 1001;
        long variantIdPM = 2002;

        // Virtual -> Extra basic Plus
        String productEBP = EXTRA_BASIC_PLUS_CODE + " AMT";

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        // Session -> AVET
        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, packId, true, null,
                null, null, null));

        // Session -> Entrada Flexible Museu
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.ACTIVITY, price,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, priceEFM,
                null, null, priceTypeIdEFM));

        // Session -> Pack merchandising
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.PRODUCT, price,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, informativePricePM,
                productIdPM, variantIdPM, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(1, 2);

        SessionDTO sessionEFM = new SessionDTO();
        sessionEFM.setVenueConfigId(venueConfigIdEFM);
        sessionEFM.setReference(productEFM);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(productEFM);

        ProductVariant productVariantPM = new ProductVariant();
        productVariantPM.setSku(productPM);

        PackDTO packDTO = new PackDTO();
        packDTO.setPriceIncrement(incrementalPrice);
        packDTO.setPricingType(PackPricingType.INCREMENTAL);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(Mockito.any())).thenReturn(new RespostaCercaClient());
        Mockito.when(msEventRepository.getSession(sessionIdEFM)).thenReturn(sessionEFM);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigIdEFM, priceTypeIdEFM.longValue())).thenReturn(priceTypeDTO);
        Mockito.when(productsRepository.getProductVariant(productIdPM, variantIdPM)).thenReturn(productVariantPM);
        Mockito.when(packRepository.getPack(packId)).thenReturn(packDTO);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue = BigDecimal.valueOf(NumberUtils.minus(price, incrementalPrice)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEFMValue = BigDecimal.valueOf(priceEFM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePMValue = BigDecimal.valueOf(informativePricePM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEBPValue = BigDecimal.valueOf(NumberUtils.minus(incrementalPrice, priceEFM, informativePricePM)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(match.getCapacityId()) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 4, 4);

        // Session -> AVET
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", description);

        // Session -> Entrada Flexible Museu
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceEFMValue, ZERO, ZERO, code, fcbCode, productEFM, null, modeCobrament, "", "", description);

        // Session -> Pack merchandising
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, pricePMValue, ZERO, ZERO, code, fcbCode, productPM, null, modeCobrament, "", "", description);

        // Virtual -> Extra basic Plus
        linia = fcb.getLinies().getLinia().get(3);
        checkLine(4, linia, channel, date, priceEBPValue, ZERO, ZERO, code, fcbCode, productEBP, null, modeCobrament, "", "", description);
    }

    @Test
    public void basicPlusNewPrice() {
        double price = 100d;
        double channelCharge = 0.6d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        long packId = 105;

        // Session -> Entrada Flexible Museu
        String productEFM = "PACK.FLEXIBLE TOUR";
        double priceSession = 23d;
        double priceEFM = 12d;
        long venueConfigIdEFM = 44L;
        long sessionIdEFM = 55L;
        Integer priceTypeIdEFM = 66;

        // Session -> Pack merchandising
        String productPM = BASIC_PLUS_CODE;
        double informativePricePM = 7.5d;
        long productIdPM = 1001;
        long variantIdPM = 2002;

        // Virtual -> Extra basic Plus
        String productEBP = EXTRA_BASIC_PLUS_CODE;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.setPrice(new OrderPriceDTO());
        orderDTO.getPrice().setBasePrice(price);

        // Session -> AVET
        orderDTO.getProducts().add(getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, packId, true, priceSession,
                null, null, null));

        // Session -> Entrada Flexible Museu
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.ACTIVITY, 0d,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, priceEFM,
                null, null, priceTypeIdEFM));

        // Session -> Pack merchandising
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.PRODUCT, 0d,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, informativePricePM,
                productIdPM, variantIdPM, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(1, 2);

        SessionDTO sessionEFM = new SessionDTO();
        sessionEFM.setVenueConfigId(venueConfigIdEFM);
        sessionEFM.setReference(productEFM);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(productEFM);

        ProductVariant productVariantPM = new ProductVariant();
        productVariantPM.setSku(productPM);

        PackDTO packDTO = new PackDTO();
        packDTO.setPriceIncrement(price);
        packDTO.setPricingType(PackPricingType.NEW_PRICE);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(Mockito.any())).thenReturn(new RespostaCercaClient());
        Mockito.when(msEventRepository.getSession(sessionIdEFM)).thenReturn(sessionEFM);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigIdEFM, priceTypeIdEFM.longValue())).thenReturn(priceTypeDTO);
        Mockito.when(productsRepository.getProductVariant(productIdPM, variantIdPM)).thenReturn(productVariantPM);
        Mockito.when(packRepository.getPack(packId)).thenReturn(packDTO);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal totalPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue = BigDecimal.valueOf(priceSession).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEFMValue = BigDecimal.valueOf(priceEFM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePMValue = BigDecimal.valueOf(informativePricePM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEBPValue = BigDecimal.valueOf(NumberUtils.minus(price, priceSession, priceEFM, informativePricePM)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(match.getCapacityId()) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 4, 4);

        // Session -> AVET
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue, ZERO, charges, code, fcbCode, codeMatch, null, modeCobrament, "", "", description);

        // Session -> Entrada Flexible Museu
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceEFMValue, ZERO, ZERO, code, fcbCode, productEFM, null, modeCobrament, "", "", description);

        // Session -> Pack merchandising
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, pricePMValue, ZERO, ZERO, code, fcbCode, productPM, null, modeCobrament, "", "", description);

        // Virtual -> Extra basic Plus
        linia = fcb.getLinies().getLinia().get(3);
        checkLine(4, linia, channel, date, priceEBPValue, ZERO, ZERO, code, fcbCode, productEBP, null, modeCobrament, "", "", description);
    }

    @Test
    public void basicPlusAudioguiaNewPrice() {
        double price = 100d;
        double channelCharge = 0.6d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;

        long packId = 105;

        // Session -> Entrada Flexible Museu
        String productEFM = "PACK.FLEXIBLE TOUR";
        double priceSession = 23d;
        double priceEFM = 12d;
        long venueConfigIdEFM = 44L;
        long sessionIdEFM = 55L;
        Integer priceTypeIdEFM = 66;

        // Session -> Pack merchandising
        String productPM = BASIC_PLUS_CODE;
        double informativePricePM = 7.5d;
        long productIdPM = 1001;
        long variantIdPM = 2002;

        // Virtual -> Extra basic Plus
        String productEBP = EXTRA_BASIC_PLUS_CODE;

        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.setPrice(new OrderPriceDTO());
        orderDTO.getPrice().setBasePrice(price);

        // Session -> AVET
        orderDTO.getProducts().add(getOrderProductDTO(sessionId, eventId, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                channelCharge,promoterChannelCharge, promoterCharge,
                packCode, packId, true, priceSession,
                null, null, null));

        // Session -> Entrada Flexible Museu
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, eventId, EventType.ACTIVITY, 0d,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, priceEFM,
                null, null, priceTypeIdEFM));

        // Session -> Pack merchandising
        orderDTO.getProducts().add(getOrderProductDTO(sessionIdEFM, null, EventType.PRODUCT, 0d,
                0d, 0d, 0d,
                0d, 0d,
                0d, 0d, 0d,
                packCode, packId, false, informativePricePM,
                productIdPM, variantIdPM, null));

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch match = getSessionMatch(1, 2);

        SessionDTO sessionEFM = new SessionDTO();
        sessionEFM.setVenueConfigId(venueConfigIdEFM);
        sessionEFM.setReference(productEFM);

        MsPriceTypeDTO priceTypeDTO = new MsPriceTypeDTO();
        priceTypeDTO.setCode(productEFM);

        ProductVariant productVariantPM = new ProductVariant();
        productVariantPM.setSku(productPM);

        PackDTO packDTO = new PackDTO();
        packDTO.setPriceIncrement(price);
        packDTO.setPricingType(PackPricingType.NEW_PRICE);

        EventDTO eventDTO = new EventDTO();
        eventDTO.setCategory(new CategoryDTO());
        eventDTO.getCategory().setCode(MUSEO);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(msEventRepository.getEvent(eventId)).thenReturn(eventDTO);
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(match);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(Mockito.any())).thenReturn(new RespostaCercaClient());
        Mockito.when(msEventRepository.getSession(sessionIdEFM)).thenReturn(sessionEFM);
        Mockito.when(venueTemplateRepository.getPriceType(venueConfigIdEFM, priceTypeIdEFM.longValue())).thenReturn(priceTypeDTO);
        Mockito.when(productsRepository.getProductVariant(productIdPM, variantIdPM)).thenReturn(productVariantPM);
        Mockito.when(packRepository.getPack(packId)).thenReturn(packDTO);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal totalPrice = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mainPriceValue = BigDecimal.valueOf(NumberUtils.minus(priceSession)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEFMValue = BigDecimal.valueOf(priceEFM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pricePMValue = BigDecimal.valueOf(informativePricePM).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceEBPValue = BigDecimal.valueOf(NumberUtils.minus(price, priceSession, priceEFM, informativePricePM)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(match.getCapacityId()) - 1, match.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String description = "";

        checkOrder(fcb, code, totalPrice, 4, 4);

        // Session -> AVET
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, mainPriceValue, ZERO, charges, code, fcbCode, codeMatch, null, modeCobrament, "", "", description);

        // Session -> Entrada Flexible Museu
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceEFMValue, ZERO, ZERO, code, fcbCode, productEFM, null, modeCobrament, "", "", description);

        // Session -> Pack merchandising
        linia = fcb.getLinies().getLinia().get(2);
        checkLine(3, linia, channel, date, pricePMValue, ZERO, ZERO, code, fcbCode, productPM, null, modeCobrament, "", "", description);

        // Virtual -> Extra basic Plus
        linia = fcb.getLinies().getLinia().get(3);
        checkLine(4, linia, channel, date, priceEBPValue, ZERO, ZERO, code, fcbCode, productEBP, null, modeCobrament, "", "", description);
    }

}
