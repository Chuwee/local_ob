package es.onebox.ath.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.ath.config.AthIntegrationProperties;
import es.onebox.ath.integration.dto.ConsultRequestDTO;
import es.onebox.ath.integration.dto.ConsultResponseDTO;
import es.onebox.ath.integration.dto.LoginRequestDTO;
import es.onebox.ath.integration.dto.LoginResponseDTO;
import es.onebox.ath.integration.dto.PayloadRequestDTO;
import es.onebox.ath.integration.dto.SeatManagementResponseDTO;
import es.onebox.common.access.AccessService;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.datasources.webhook.WebhookDatasource;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthConsultResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthLoginResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthSeatManagementResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.CesionLocalidad;
import es.onebox.common.datasources.webhook.dto.ath.PartidoCesion;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;

class AthIntegrationServiceTest {

    private static final String LOGIN_URL = "login.url";
    private static final String CESION_URL = "cesion.url";
    private static final String OB_USER = "username";
    private static final String OB_PASSWORD = "password";
    private static final String SEASON = "21-22";
    private static final String EVENT_ID = "123";

    @Mock
    private AccessService accessService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private WebhookDatasource webhookDatasource;

    private AthIntegrationService athIntegrationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        AthIntegrationProperties properties = new AthIntegrationProperties();
        properties.setLoginUrl(LOGIN_URL);
        properties.setTransferUrl(CESION_URL);
        properties.setUsername(OB_USER);
        properties.setPassword(OB_PASSWORD);

        athIntegrationService = new AthIntegrationService(accessService, ordersRepository, webhookDatasource, properties);
    }

    @Test
    void loginTest() throws JsonProcessingException {
        LoginRequestDTO body = ObjectRandomizer.random(LoginRequestDTO.class);
        AthLoginResponseDTO responseDTO = ObjectRandomizer.random(AthLoginResponseDTO.class);
        responseDTO.setCodigoError("200");

        when(webhookDatasource.loginATH(Mockito.eq(LOGIN_URL), Mockito.any(OrderNotificationMessageDTO.class))).thenReturn(responseDTO);

        LoginResponseDTO response = athIntegrationService.login(body);

        Assertions.assertEquals(response.getToken(), responseDTO.getToken());
    }

    @Test
    void releaseTest() throws JsonProcessingException {
        PayloadRequestDTO body = ObjectRandomizer.random(PayloadRequestDTO.class);
        mockMemberOrder(body.getOrderCode(), body.getEntityId());
        AthSeatManagementResponseDTO responseDTO = ObjectRandomizer.random(AthSeatManagementResponseDTO.class);

        when(webhookDatasource.sendATHCreateModifyNotification(Mockito.eq(CESION_URL + "/crear"), Mockito.any(OrderNotificationMessageDTO.class))).thenReturn(responseDTO);

        SeatManagementResponseDTO response = athIntegrationService.release(body);

        Assertions.assertEquals(response.getToken(), responseDTO.getToken());
    }

    @Test
    void recoverTest() throws JsonProcessingException {
        PayloadRequestDTO body = ObjectRandomizer.random(PayloadRequestDTO.class);
        mockMemberOrder(body.getOrderCode(), body.getEntityId());
        AthConsultResponseDTO listDTO = new AthConsultResponseDTO();
        CesionLocalidad cesionLocalidad = ObjectRandomizer.random(CesionLocalidad.class);
        PartidoCesion partidoCesion = ObjectRandomizer.random(PartidoCesion.class);
        partidoCesion.setPartido(SEASON + EVENT_ID);
        cesionLocalidad.setEstado("Confirmada");
        cesionLocalidad.setPartidoCesion(partidoCesion);
        listDTO.setCesionesLocalidad(List.of(cesionLocalidad));
        AthSeatManagementResponseDTO responseDTO = ObjectRandomizer.random(AthSeatManagementResponseDTO.class);

        when(webhookDatasource.getATHCessionsList(Mockito.eq(CESION_URL + "/consultarEnviadas"), Mockito.any(OrderNotificationMessageDTO.class))).thenReturn(listDTO);
        when(webhookDatasource.sendATHCreateModifyNotification(Mockito.eq(CESION_URL + "/modificar"), Mockito.any(OrderNotificationMessageDTO.class))).thenReturn(responseDTO);

        SeatManagementResponseDTO response = athIntegrationService.recover(body);

        Assertions.assertEquals(response.getToken(), responseDTO.getToken());
    }

    @Test
    void getTransfersListTest() throws JsonProcessingException {
        ConsultRequestDTO body = ObjectRandomizer.random(ConsultRequestDTO.class);
        AthConsultResponseDTO listDTO = new AthConsultResponseDTO();
        CesionLocalidad cesionLocalidad = ObjectRandomizer.random(CesionLocalidad.class);
        PartidoCesion partidoCesion = ObjectRandomizer.random(PartidoCesion.class);
        partidoCesion.setPartido(SEASON + EVENT_ID);
        cesionLocalidad.setEstado("Confirmada");
        cesionLocalidad.setPartidoCesion(partidoCesion);
        listDTO.setCesionesLocalidad(List.of(cesionLocalidad));

        when(webhookDatasource.getATHCessionsList(Mockito.eq(CESION_URL + "/consultarEnviadas"), Mockito.any(OrderNotificationMessageDTO.class))).thenReturn(listDTO);

        ConsultResponseDTO response = athIntegrationService.getTransfersList(body);

        Assertions.assertEquals(response.getToken(), listDTO.getToken());
    }

    private void mockMemberOrder(String memberOrderId, Long entity) {
        String accessToken = ObjectRandomizer.randomString();
        HashMap memberOrder = generateMemberOrder();
        when(accessService.getAccessToken(entity)).thenReturn(accessToken);
        when(ordersRepository.getRawMemberOrder(memberOrderId, accessToken)).thenReturn(memberOrder);
    }

    private HashMap generateMemberOrder() {
        HashMap memberOrder = new HashMap();
        HashMap item = new HashMap();
        HashMap member = new HashMap();
        HashMap allocation = new HashMap();
        HashMap event = new HashMap();
        member.put("partner_id", ObjectRandomizer.randomString());
        member.put("person_id", ObjectRandomizer.randomString());
        member.put("name", ObjectRandomizer.randomString());
        member.put("surname", ObjectRandomizer.randomString());
        member.put("email", ObjectRandomizer.randomString());
        allocation.put("external_event", event);
        event.put("id", EVENT_ID);
        item.put("season", SEASON);
        item.put("member", member);
        item.put("allocation", allocation);
        memberOrder.put("items", List.of(item));
        memberOrder.put("type", "TEST_ORDER");

        return memberOrder;
    }
}
