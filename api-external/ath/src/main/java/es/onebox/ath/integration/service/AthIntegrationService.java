package es.onebox.ath.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.ath.config.AthIntegrationProperties;
import es.onebox.ath.integration.converter.BluewayMessageConverter;
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
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AthIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AthIntegrationService.class);

    private final static String CONFIRMED = "Confirmada";

    private final AccessService accessService;
    private final OrdersRepository ordersRepository;
    private final WebhookDatasource webhookDatasource;
    private final String login_url;
    private final String transfer_url;
    private final String ob_user;
    private final String ob_password;

    @Autowired
    public AthIntegrationService(AccessService accessService, OrdersRepository ordersRepository,
                                 WebhookDatasource webhookDatasource, AthIntegrationProperties athIntegrationProperties) {
        this.accessService = accessService;
        this.ordersRepository = ordersRepository;
        this.webhookDatasource = webhookDatasource;
        this.login_url = athIntegrationProperties.getLoginUrl();
        this.transfer_url = athIntegrationProperties.getTransferUrl();
        this.ob_user = athIntegrationProperties.getUsername();
        this.ob_password = athIntegrationProperties.getPassword();
    }

    public LoginResponseDTO login(LoginRequestDTO body) throws JsonProcessingException {

        OrderNotificationMessageDTO messageCreateDTO = BluewayMessageConverter.convertLogin(body, ob_user, ob_password);
        AthLoginResponseDTO responseDTO = webhookDatasource.loginATH(login_url, messageCreateDTO);
        if (!responseDTO.getCodigoError().equals("200")) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.REQUEST_FAILED, responseDTO.getMensaje());
        }
        LOGGER.info("[ATH INTEGRATION] Login successful for user {}", body.getUsername());
        return BluewayMessageConverter.convertLoginResponse(responseDTO);
    }

    public SeatManagementResponseDTO release(PayloadRequestDTO body) throws JsonProcessingException {
        HashMap<String, Object> memberOrder = getMemberOrder(body);
        LOGGER.info("[ATH INTEGRATION] [{}] Starting notification of type {}", body.getOrderCode(), memberOrder.get("type"));

        OrderNotificationMessageDTO messageCreateDTO = BluewayMessageConverter.convertCreate(memberOrder, body.getToken(), ob_user, ob_password);
        AthSeatManagementResponseDTO responseDTO = webhookDatasource.sendATHCreateModifyNotification(transfer_url + "/crear", messageCreateDTO);
        LOGGER.info("[ATH INTEGRATION] [{}] Notification of type {} sent", body.getOrderCode(), memberOrder.get("type"));
        return BluewayMessageConverter.convertSeatManagementResponse(responseDTO);
    }

    public SeatManagementResponseDTO recover(PayloadRequestDTO body) throws JsonProcessingException {
        HashMap<String, Object> memberOrder = getMemberOrder(body);
        LOGGER.info("[ATH INTEGRATION] [{}] Starting notification of type {}", body.getOrderCode(), memberOrder.get("type"));

        List<CesionLocalidad> cessionsList = getTransfersList(extractMemberId(memberOrder), body.getToken()).getCesionesLocalidad();

        String cessionId = extractCessionId(cessionsList, memberOrder);
        OrderNotificationMessageDTO messageModifyDTO = BluewayMessageConverter.convertModify(cessionId, body.getToken(), ob_user, ob_password);
        AthSeatManagementResponseDTO responseDTO = webhookDatasource.sendATHCreateModifyNotification(transfer_url + "/modificar", messageModifyDTO);

        LOGGER.info("[ATH INTEGRATION] [{}] Notification of type {} sent", body.getOrderCode(), memberOrder.get("type"));
        return BluewayMessageConverter.convertSeatManagementResponse(responseDTO);
    }

    public ConsultResponseDTO getTransfersList(ConsultRequestDTO body) throws JsonProcessingException {
        LOGGER.info("[ATH INTEGRATION] [{}] Starting request of transfers", body.getUsername());

        return BluewayMessageConverter.convertConsultResponse(getTransfersList(body.getUsername(), body.getToken()));
    }

    private AthConsultResponseDTO getTransfersList(String username, String token) throws JsonProcessingException {
        OrderNotificationMessageDTO messageConsultDTO = BluewayMessageConverter.convertConsult(username,
                token, ob_user, ob_password);
        return webhookDatasource.getATHCessionsList(transfer_url + "/consultarEnviadas", messageConsultDTO);
    }

    private HashMap<String, Object> getMemberOrder(PayloadRequestDTO body) {
        String orderCode = body.getOrderCode();

        String accessToken = accessService.getAccessToken(body.getEntityId());
        try {
            return ordersRepository.getRawMemberOrder(orderCode, accessToken);
        } catch (Exception e) {
            LOGGER.error("[ATH INTEGRATION] [{}] error obtaining order", orderCode, e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
    }

    private String extractCessionId(List<CesionLocalidad> cessionsList, HashMap memberOrder) {
        if (cessionsList == null || cessionsList.isEmpty()) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND);
        }
        List<HashMap> items = (List<HashMap>) memberOrder.get("items");
        HashMap item = items.get(0);
        HashMap allocation = (HashMap) item.get("allocation");
        HashMap event = (HashMap) allocation.get("external_event");

        String matchId = item.get("season").toString() + event.get("id").toString();
        for (CesionLocalidad cession : cessionsList) {
            if (cession.getPartidoCesion() != null) {
                PartidoCesion matchInfo = cession.getPartidoCesion();
                if (matchId.equals(matchInfo.getPartido()) && CONFIRMED.equals(cession.getEstado())) {
                    return cession.getCesionLocalidadId();
                }
            }
        }
        throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND);
    }

    private String extractMemberId(HashMap memberOrder) {
        List<HashMap> items = (List<HashMap>) memberOrder.get("items");
        HashMap item = items.get(0);
        HashMap member = (HashMap) item.get("member");
        return member.get("person_id").toString();
    }
}
