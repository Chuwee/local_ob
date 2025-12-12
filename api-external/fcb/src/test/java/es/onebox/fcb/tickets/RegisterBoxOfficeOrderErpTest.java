package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.order.dto.ChannelType;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderPaymentDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderUserDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.dal.dto.couch.enums.PaymentType;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.Linia;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


public class RegisterBoxOfficeOrderErpTest extends BaseErpTest {

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
    public void cash() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String channel = "TAQUILLES";
        double price = 10d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.getOrderData().setChannelType(ChannelType.BOXOFFICE);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();
        orderPaymentDTO.setPaymentType(PaymentType.CASH);
        orderDTO.getPayments().add(orderPaymentDTO);

        OrderUserDTO orderUserDTO = new OrderUserDTO();
        orderUserDTO.setName("Name");
        orderUserDTO.setSurname("Surname");
        orderDTO.setCustomer(orderUserDTO);

        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = FCBTicketService.EFECTIU;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", "", "");
        Assertions.assertNull(linia.getDataCobrament());
        Assertions.assertNull(linia.getIdCobrament());
    }

    @Test
    public void valACarregar() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        String paymentReference = "00000001R";
        Long sessionId = 101L;
        Integer channelId = 50;
        String channel = "TAQUILLA_WEB";
        double price = 10d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.getOrderData().setChannelType(ChannelType.BOXOFFICE);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();
        orderPaymentDTO.setPaymentType(PaymentType.EXTERNAL);
        orderPaymentDTO.setPaymentReference(paymentReference);
        orderDTO.getPayments().add(orderPaymentDTO);

        OrderUserDTO orderUserDTO = new OrderUserDTO();
        orderUserDTO.setName("Name");
        orderUserDTO.setSurname("Surname");
        orderDTO.setCustomer(orderUserDTO);

        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = FCBTicketService.VAL_A_CARREGAR;
        String descripcioTipusEntrada = "LOC: " + code;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", paymentReference, descripcioTipusEntrada);
        Assertions.assertNull(linia.getDataCobrament());
        Assertions.assertNull(linia.getIdCobrament());
    }

    @Test
    public void tranferenciaPendiente() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        String paymentReference = "00000001R";
        Long sessionId = 101L;
        Integer channelId = 50;
        String channel = "TAQUILLA_WEB";
        double price = 10d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.getOrderData().setChannelType(ChannelType.BOXOFFICE);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();
        orderPaymentDTO.setPaymentType(PaymentType.BANK_TRANSFER);
        orderPaymentDTO.setPaymentReference(paymentReference);
        orderDTO.getPayments().add(orderPaymentDTO);

        OrderUserDTO orderUserDTO = new OrderUserDTO();
        orderUserDTO.setName("Name");
        orderUserDTO.setSurname("Surname");
        orderDTO.setCustomer(orderUserDTO);

        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = FCBTicketService.TRANSFERENCIA_PENDENT;
        String descripcioTipusEntrada = "LOC: " + code;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", paymentReference, descripcioTipusEntrada);
        Assertions.assertNull(linia.getDataCobrament());
        Assertions.assertNull(linia.getIdCobrament());
    }

    @Test
    public void tranferenciaCobrada() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        String paymentReference = "00000001R";
        Long sessionId = 101L;
        Integer channelId = 50;
        String channel = "TAQUILLA_WEB";
        double price = 10d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        orderDTO.getOrderData().setChannelType(ChannelType.BOXOFFICE);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        OrderPaymentDTO orderPaymentDTO = new OrderPaymentDTO();
        orderPaymentDTO.setPaymentType(PaymentType.BANK_TRANSFER);
        orderPaymentDTO.setPaymentReference(paymentReference);
        orderPaymentDTO.setTransferDate(ZonedDateTime.now());
        orderDTO.getPayments().add(orderPaymentDTO);

        OrderUserDTO orderUserDTO = new OrderUserDTO();
        orderUserDTO.setName("Name");
        orderUserDTO.setSurname("Surname");
        orderDTO.setCustomer(orderUserDTO);

        SessionMatch session = getSessionMatch(1, 2);

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = FCBTicketService.TRANSFERENCIA_COBRADA;
        String descripcioTipusEntrada = "LOC: " + code;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", paymentReference, descripcioTipusEntrada);
        Assertions.assertNotNull(linia.getDataCobrament());
        Assertions.assertEquals("Name Surname", linia.getIdCobrament());
    }

}
