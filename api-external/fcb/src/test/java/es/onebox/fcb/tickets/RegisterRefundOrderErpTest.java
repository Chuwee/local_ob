package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
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

import static org.mockito.ArgumentMatchers.anyString;

public class RegisterRefundOrderErpTest extends BaseErpTest {

    @InjectMocks
    private FCBTicketService fcbTicketService;

    @Mock
    private PeopleSoftRepository peopleSoftRepository;

    @Mock
    private MsOrderRepository msOrderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OperationCodeService operationCodeService;

    @Mock
    private IntAvetConfigRepository intAvetConfigRepository;

    @Mock
    private FcbChannelMappingsProperties fcbChannelMappingsProperties;

    @Mock
    private ChannelCouchDao channelCouchDao;
    @Mock
    private ChannelRepository channelRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fcbTicketService, "fcbEntity", 125L);
    }

    @Test
    public void simpleOrder() {
        String code = "TEST";
        String originalCode = "ORIGINAL_TEST";
        String originalFcbCode = "24012345678V";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-FUTBOL";
        double price = 10d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getRefundOrderDTO(code, date, channelId, originalCode);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price * -1,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(originalCode)).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getRefundOperationId(originalCode)).thenReturn(fcbCode);
        Mockito.when(operationCodeService.getOperationId(originalCode)).thenReturn(originalFcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal totalPrice = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;

        checkOrder(fcb, code, totalPrice.negate(), 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, totalPrice.negate(), promotions.negate(), charges.negate(), code, fcbCode, codeMatch, null, modeCobrament, "", "", "REF: " + originalFcbCode);
    }


    @Test
    public void promotionOrder() {
        String code = "TEST";
        String originalCode = "ORIGINAL_TEST";
        String originalFcbCode = "24012345678V";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-FUTBOL";
        double price = -10d;
        double automatic = -0.2d;
        double promotion = -0.3d;
        double discount = -0.5d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getRefundOrderDTO(code, date, channelId, originalCode);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getRefundOperationId(originalCode)).thenReturn(fcbCode);
        Mockito.when(operationCodeService.getOperationId(originalCode)).thenReturn(originalFcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal totalPrice = BigDecimal.valueOf(price - automatic - promotion - discount).setScale(2);
        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(automatic+promotion+discount).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;

        checkOrder(fcb, code, totalPrice, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", "REF: " + originalFcbCode);
    }

    @Test
    public void completeOrder() {
        String code = "TEST";
        String originalCode = "ORIGINAL_TEST";
        String originalFcbCode = "24012345678V";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-FUTBOL";
        double price = -10d;
        double automatic = -0.2d;
        double promotion = -0.3d;
        double discount = -0.7d;
        double channelAutomatic = -1d;
        double channelCollective = -2d;
        double channelCharge = -1d;
        double promoterChannelCharge = -1.5d;
        double promoterCharge = -2d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getRefundOrderDTO(code, date, channelId, originalCode);

        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                0d, 0d,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null);
        orderDTO.getProducts().add(orderProductDTO);

        orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null);
        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getRefundOperationId(originalCode)).thenReturn(fcbCode);
        Mockito.when(operationCodeService.getOperationId(originalCode)).thenReturn(originalFcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal promotions2 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = BigDecimal.valueOf(price).add(BigDecimal.valueOf(price)).subtract(promotions1).subtract(promotions2).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue2 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;

        checkOrder(fcb, code, totalPrice, 2, 2);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue1, promotions1, charges.abs(), code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", "REF: " + originalFcbCode);

        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, priceValue2, promotions2, charges.abs(), code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, "", "REF: " + originalFcbCode);
    }

}
