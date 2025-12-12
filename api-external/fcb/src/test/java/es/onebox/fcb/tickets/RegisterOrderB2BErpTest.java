package es.onebox.fcb.tickets;

import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.client.dto.Client;
import es.onebox.common.datasources.ms.client.repository.ClientsRepository;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderPaymentDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderUserDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.dal.dto.couch.enums.UserType;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaAltaClient;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

public class RegisterOrderB2BErpTest extends BaseErpTest {

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
    private ChannelRepository channelRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fcbTicketService, "fcbEntity", 125L);
    }

    @Test
    public void simpleOrder_existingClient() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        Long clientId = 342L;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-B2B";
        double price = 25d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.setClient(getClientDTO(orderDTO, clientId));

        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null);
        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);

        RespostaCercaClient respostaCercaClient = getRespostaCercaClient();
        respostaCercaClient.getDadesClient().getIdentificadorsOrigen().getIdentificadors().getId().add("B2B_342");

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(respostaCercaClient);

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal b2bPriceValue = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = FCBTicketService.SALDO;
        String clau = "B2B_" + clientId;
        String description = "LOC: " + code;

        checkOrder(fcb, code, BigDecimal.ZERO, 2, 2);

        // Product
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, clau, description);

        // B2B
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, b2bPriceValue.negate(), ZERO, ZERO, code, fcbCode, ANTICIPO, null, modeCobrament, "", clau, description);
    }

    @Test
    public void simpleOrder_altaClient() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        Long clientId = 342L;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-B2B";
        double price = 25d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);

        orderDTO.setClient(getClientDTO(orderDTO, clientId));

        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null);
        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);
        EntityDTO entityDTO = getEntityDTO();

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(null);
        Mockito.when(entitiesRepository.getById(any())).thenReturn(entityDTO);
        Mockito.when(clientsRepository.getClient(any(),any())).thenReturn(new Client());
        Mockito.when(peopleSoftRepository.addClient(any())).thenReturn(new RespostaAltaClient());

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal b2bPriceValue = BigDecimal.valueOf(price).subtract(promotions1).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
        String modeCobrament = FCBTicketService.SALDO;
        String clau = "B2B_" + clientId;
        String description = "LOC: " + code;

        checkOrder(fcb, code, BigDecimal.ZERO, 2, 2);

        // Product
        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, channel, date, priceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, clau, description);

        // B2B
        linia = fcb.getLinies().getLinia().get(1);
        checkLine(2, linia, channel, date, b2bPriceValue.negate(), ZERO, ZERO, code, fcbCode, ANTICIPO, null, modeCobrament, "", clau, description);
    }

    @Test
    public void b2bBookingCheckoutAsBoxOffice_targeta() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        Long clientId = 342L;
        String paymentMethod = "TARGETA";
        String acquirer = "SABADELL";
        String channel = "OB-B2B";
        double price = 25d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        // set up for booking checkout order
        OrderUserDTO customer = new OrderUserDTO();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("companyName", "clauClientTest"); // clau client from buyer data
        customer.setAdditionalInfo(additionalData);
        orderDTO.setCustomer(customer);
        orderDTO.getOrderData().setUserType(UserType.B2B);
        orderDTO.setRelatedOriginalCode("QWERT123"); // refers to the code booking
        List<OrderPaymentDTO> payments =  new ArrayList<>();
        OrderPaymentDTO paymentDTO = new OrderPaymentDTO();
        paymentDTO.setValue(1.0);
        paymentDTO.setPaymentReference("234234234");
        payments.add(paymentDTO);
        orderDTO.setPayments(payments);
        orderDTO.setClient(getClientDTO(orderDTO, clientId));

        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null);
        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_TEST);
        SessionMatch session = getSessionMatch(1, 2);
        EntityDTO entityDTO = getEntityDTO();

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(null);
        Mockito.when(entitiesRepository.getById(any())).thenReturn(entityDTO);
        Mockito.when(clientsRepository.getClient(any(),any())).thenReturn(new Client());
        Mockito.when(peopleSoftRepository.addClient(any())).thenReturn(new RespostaAltaClient());

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());

        String modeCobrament = "TARGETA-SABADELL";
        String clau = orderDTO.getCustomer().getAdditionalInfo().get("companyName").toString(); // clau client
        String description = "LOC: " + code;
        String canalVenda = "TAQUILLA_WEB";
        BigDecimal totalImport = BigDecimal.valueOf(20.34);
        checkOrder(fcb, code, totalImport, 1, 1);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, canalVenda, date, priceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, clau, description);
    }

    @Test
    public void b2bBookingCheckoutAsBoxOffice_oneboxAccounting() {
        String code = "TEST";
        String fcbCode = "24012345678V";
        Long sessionId = 101L;
        Integer channelId = 50;
        Long clientId = 342L;
        String paymentMethod = "";
        String acquirer = "";
        String channel = "OB-B2B";
        double price = 25d;
        double automatic = 0.42d;
        double promotion = 0.3d;
        double discount = 0.94d;
        double channelAutomatic = 1d;
        double channelCollective = 2d;
        double channelCharge = 1d;
        double promoterChannelCharge = 1.5d;
        double promoterCharge = 2d;
        ZonedDateTime date = ZonedDateTime.now();

        OrderDTO orderDTO = getOrderDTO(code, date, channelId);
        // set up for booking checkout order
        OrderUserDTO customer = new OrderUserDTO();
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("companyName", "clauClientTest"); // clau client from buyer data
        customer.setAdditionalInfo(additionalData);
        orderDTO.setCustomer(customer);
        orderDTO.getOrderData().setUserType(UserType.B2B);
        orderDTO.setRelatedOriginalCode("QWERT123"); // refers to the code booking
        List<OrderPaymentDTO> payments =  new ArrayList<>();
        OrderPaymentDTO paymentDTO = new OrderPaymentDTO();
        paymentDTO.setValue(1.0);
        paymentDTO.setPaymentReference("234234234");
        paymentDTO.setGatewaySid(GATEWAY_ACCOUNTING);
        payments.add(paymentDTO);
        orderDTO.setPayments(payments);
        orderDTO.setClient(getClientDTO(orderDTO, clientId));

        OrderProductDTO orderProductDTO = getOrderProductDTO(sessionId, null, EventType.AVET, price,
                automatic, promotion, discount,
                channelAutomatic, channelCollective,
                channelCharge,promoterChannelCharge, promoterCharge,
                null, null, null, null,
                null, null, null);
        orderDTO.getProducts().add(orderProductDTO);

        PaymentOrder paymentOrder = getPaymentOrder(paymentMethod, acquirer, GATEWAY_ACCOUNTING);
        SessionMatch session = getSessionMatch(1, 2);
        EntityDTO entityDTO = getEntityDTO();

        Mockito.when(msOrderRepository.getOrderByCode(Mockito.anyString())).thenReturn(orderDTO);
        Mockito.when(channelRepository.getChannelConfig(Mockito.any())).thenReturn(getChannelConfig());
        Mockito.when(paymentRepository.getPaymentOrder(Mockito.anyString())).thenReturn(paymentOrder);
        Mockito.when(operationCodeService.getOrGenerateOperationId(any())).thenReturn(fcbCode);
        Mockito.when(intAvetConfigRepository.getSession(sessionId)).thenReturn(session);
        Mockito.when(fcbChannelMappingsProperties.get(FCBTicketService.DEFAULT_SELLER_CHANNEL)).thenReturn(channel);
        Mockito.when(peopleSoftRepository.checkNif(any(), any())).thenReturn(true);
        Mockito.when(peopleSoftRepository.searchNif(any())).thenReturn(null);
        Mockito.when(entitiesRepository.getById(any())).thenReturn(entityDTO);
        Mockito.when(clientsRepository.getClient(any(),any())).thenReturn(new Client());
        Mockito.when(peopleSoftRepository.addClient(any())).thenReturn(new RespostaAltaClient());

        PeticioFacturar fcb = fcbTicketService.registerOperation(code);

        BigDecimal promotions1 = BigDecimal.valueOf(automatic + promotion + discount + channelAutomatic + channelCollective).setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceValue1 = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
        BigDecimal charges = BigDecimal.valueOf(channelCharge + promoterChannelCharge + promoterCharge).setScale(2, RoundingMode.HALF_UP);
        String codeMatch = String.format("%d-%02d-%03d", 0, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());

        String modeCobrament = "SALDO";
        String clau = orderDTO.getCustomer().getAdditionalInfo().get("companyName").toString(); // clau client
        String description = "LOC: " + code;
        String canalVenda = "TAQUILLA_WEB";

        checkOrder(fcb, code, BigDecimal.ZERO, 2, 2);

        Linia linia = fcb.getLinies().getLinia().get(0);
        checkLine(1, linia, canalVenda, date, priceValue1, promotions1, charges, code, fcbCode, codeMatch, null, modeCobrament, FCBTicketService.PROMOTION_CODE, clau, description);
    }
}
