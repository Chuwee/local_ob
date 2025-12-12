package es.onebox.fusionauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.HMACUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fusionauth.dto.FusionAuthEventDTO;
import es.onebox.fusionauth.dto.FusionAuthNotificationDTO;
import es.onebox.fusionauth.eip.FusionAuthWebhookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FusionAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FusionAuthService.class);

    private final FusionAuthWebhookService webhookService;
    private final String secretKey;


    @Autowired
    public FusionAuthService(@Value("${chelsea.fusion-auth.webhook.secret}") String secretKey,
                             FusionAuthWebhookService webhookService) {
        this.secretKey = secretKey;
        this.webhookService = webhookService;
    }

    public void registerEvent(FusionAuthNotificationDTO payload, String hmacSignature) {
        verifySignature(payload, hmacSignature);
        FusionAuthEventDTO event = payload.getEvent();
        Object customerDTO = event.getUser();
        Object original = event.getOriginal();
        webhookService.fusionAuthWebhookProducer(customerDTO, event.getType(), original);
    }

    private void verifySignature(FusionAuthNotificationDTO payload, String hmacSignature) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String stringPayload = null;
        try {
            stringPayload = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            LOGGER.error("[FusionAuth Webhook] Error parsing payload", e);
            throw new OneboxRestException();
        }
        if(Boolean.FALSE.equals(HMACUtils.verifyHmac(secretKey, stringPayload, hmacSignature))) {
            LOGGER.error("[FusionAuth Webhook] HMAC signature verification failed");
            throw new OneboxRestException(ApiExternalErrorCode.UNAUTHORIZED_ACCESS, "No authorization", null);
        }
    }



}
