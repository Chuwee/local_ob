package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.Linia;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class RegisterOperationTest extends BaseErpTest {

    @InjectMocks
    private FCBTicketService fcbTicketService;

    @Mock private PeopleSoftRepository peopleSoftRepository;
    @Mock private FcbChannelMappingsProperties fcbChannelMappingsProperties;
    @Mock private OperationCodeService operationCodeService;
    @Mock private ChannelCouchDao channelCouchDao;
    @Mock private IntAvetConfigRepository intAvetConfigRepository;
    @Mock private MsOrderRepository msOrderRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private ChannelRepository channelRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fcbTicketService, "fcbEntity", 125L);
    }

    @Test
    public void whenNoFCBProducts_thenReturnNull() {
        String code = "ORDER_NO_FCB";
        ZonedDateTime date = ZonedDateTime.now();
        int channelId = 1;

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        OrderProductDTO product = getOrderProductDTO(100L, null, EventType.AVET, 10.0,
                0d, 0d, 0d, 0d, 0d,
                0d, 0d, 0d,
                null, null, null, null,
                null, null, null);
        product.setEventEntityId(999); // Non-FCB
        orderDTO.getProducts().add(product);

        Mockito.when(msOrderRepository.getOrderByCode(code)).thenReturn(orderDTO);

        PeticioFacturar result = fcbTicketService.registerOperation(code);
        Assertions.assertNull(result);
    }

    @Test
    public void whenAllProductsAreFCB_thenProcessNormally() {
        String code = "ORDER_ALL_FCB";
        String fcbCode = "FCB123456";
        ZonedDateTime date = ZonedDateTime.now();
        int channelId = 1;
        double price = 10.0;
        String paymentMethod = "CARD";
        String acquirer = "BANK";
        String channel = "FCB-CHANNEL";

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        OrderProductDTO product = getOrderProductDTO(100L, null, EventType.AVET, price,
                0d, 0d, 0d, 0d, 0d,
                0d, 0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(product);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(code)).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(fcbChannelMappingsProperties.containsKey(Mockito.anyString())).thenReturn(true);
        Mockito.when(fcbChannelMappingsProperties.get(Mockito.anyString())).thenReturn(channel);
        Mockito.when(intAvetConfigRepository.getSession(Mockito.anyLong())).thenReturn(session);

        PeticioFacturar result = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.ZERO.setScale(2);
        BigDecimal charges = BigDecimal.ZERO.setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, session.getCapacityId() - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;

        checkOrder(result, code, priceValue, 1, 1);

        Linia linia = result.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges,
                code, fcbCode, codeMatch, null, modeCobrament, "", "", "");
    }

    @Test
    public void whenMixedProducts_thenOnlyFCBProductsAreRegistered() {
        String code = "ORDER_SOME_FCB";
        String fcbCode = "FCB123456";
        ZonedDateTime date = ZonedDateTime.now();
        int channelId = 1;
        double price = 10.0;
        String paymentMethod = "CARD";
        String acquirer = "BANK";
        String channel = "FCB-CHANNEL";

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        OrderProductDTO fcbProduct = getOrderProductDTO(100L, null, EventType.AVET, price,
                0d, 0d, 0d, 0d, 0d,
                0d, 0d, 0d,
                null, null, null, null,
                null, null, null);

        OrderProductDTO nonFcbProduct = getOrderProductDTO(101L, null, EventType.AVET, price,
                0d, 0d, 0d, 0d, 0d,
                0d, 0d, 0d,
                null, null, null, null,
                null, null, null);

        nonFcbProduct.setEventEntityId(999); // Non-FCB
        orderDTO.getProducts().add(fcbProduct);
        orderDTO.getProducts().add(nonFcbProduct);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(code)).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(fcbChannelMappingsProperties.containsKey(Mockito.anyString())).thenReturn(true);
        Mockito.when(fcbChannelMappingsProperties.get(Mockito.anyString())).thenReturn(channel);
        Mockito.when(intAvetConfigRepository.getSession(Mockito.anyLong())).thenReturn(session);

        PeticioFacturar result = fcbTicketService.registerOperation(code);

        Assertions.assertNotNull(result, "Result should not be null since FCB products exist");
        Assertions.assertEquals(1, result.getLinies().getLinia().size(), "Only FCB products should be processed");

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.ZERO.setScale(2);
        BigDecimal charges = BigDecimal.ZERO.setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, session.getCapacityId() - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;

        checkOrder(result, code, priceValue, 1, 1);

        Linia linia = result.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges,
                code, fcbCode, codeMatch, null, modeCobrament, "", "", "");
    }
}
