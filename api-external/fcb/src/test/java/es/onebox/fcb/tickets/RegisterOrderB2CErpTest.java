package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.client.repository.ClientsRepository;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.fcb.dao.B2CPeopleSoftCounterCouchDao;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaModificacioClient;
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
import java.time.ZonedDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class RegisterOrderB2CErpTest extends BaseErpTest {

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
    private EntitiesRepository entitiesRepository;
    @Mock
    private ClientsRepository clientsRepository;
    @Mock
    private B2CPeopleSoftCounterCouchDao b2cPeopleSoftCounterCouchDao;
    @Mock
    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fcbTicketService, "fcbEntity", 125L);
    }

    @Test
    void simpleOrder_existingClient() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-FUTBOL";
        double price = 10d;
        boolean autoFactura = false;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTOB2C(code, date, channelId);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        RespostaCercaClient respostaCercaClient = getRespostaCercaClient();
        respostaCercaClient.getDadesClient().getIdentificadorsOrigen().getIdentificadors().getId().add("B2C_123");

        Long b2cIdentifierMock = 123L;

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(respostaCercaClient);
        Mockito.when(b2cPeopleSoftCounterCouchDao.getAndIncrement()).thenReturn(b2cIdentifierMock);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String clau = "B2C_" + b2cIdentifierMock;
        String descripcioTipusEntrada = "LOC: " + code;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", clau, descripcioTipusEntrada);
        assertEquals(autoFactura, linia.isAutofactura(), "linia.isAutofactura for B2C must be false");
    }

    @Test
    void simpleOrder_existingClientWithOutOBIdentifier() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-FUTBOL";
        double price = 10d;
        boolean autoFactura = false;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTOB2C(code, date, channelId);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        RespostaCercaClient respostaCercaClient = getRespostaCercaClient();
        respostaCercaClient.getDadesClient().getIdentificadorsOrigen().getIdentificadors().getId().add("456"); // the user exists but don't have OB identifier (B2C_XXX)

        Long b2cIdentifierMock = 123L;

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(respostaCercaClient);
        Mockito.when(b2cPeopleSoftCounterCouchDao.getAndIncrement()).thenReturn(b2cIdentifierMock);
        Mockito.when(peopleSoftRepository.modificarClient(any())).thenReturn(new RespostaModificacioClient());

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String clau = "B2C_" + b2cIdentifierMock;
        String descripcioTipusEntrada = "LOC: " + code;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", clau, descripcioTipusEntrada);
        assertEquals(autoFactura, linia.isAutofactura(), "linia.isAutofactura for B2C must be false");
    }

    @Test
    void simpleOrder_altaClient() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-FUTBOL";
        double price = 10d;
        boolean autoFactura = false;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTOB2C(code, date, channelId);
        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                0d, 0d, 0d,
                0d, 0d,
                0d,0d, 0d,
                null, null, null, null,
                null, null, null);

        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);

        EntityDTO entityDTO = getEntityDTO();
        Long b2cIdentifierMock = 123L;

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(Mockito.any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(null); // null in order to registerClient
        Mockito.when(b2cPeopleSoftCounterCouchDao.getAndIncrement()).thenReturn(b2cIdentifierMock);
        Mockito.when(entitiesRepository.getById(any())).thenReturn(entityDTO);
        Mockito.when(peopleSoftRepository.addClient(any())).thenReturn(new RespostaAltaClient());

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal priceValue = BigDecimal.valueOf(price).setScale(2);
        BigDecimal promotions = BigDecimal.valueOf(0d).setScale(2);
        BigDecimal charges = BigDecimal.valueOf(0d).setScale(2);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = paymentMethod + "-" + acquirer;
        String clau = "B2C_" + b2cIdentifierMock;
        String descripcioTipusEntrada = "LOC: " + code;

        checkOrder(fcb, code, priceValue, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue, promotions, charges, code, fcbCode, codeMatch, null, modeCobrament, "", clau, descripcioTipusEntrada);
        assertEquals(autoFactura, linia.isAutofactura(), "linia.isAutofactura for B2C must be false");
    }
}