package es.onebox.fcb.tickets;

import es.onebox.common.amt.AMTCustomTag;
import es.onebox.common.config.ApiConfig;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import es.onebox.fcb.tickets.dto.NotificationMessageDTO;
import es.onebox.fcb.tickets.dto.OperationIdRequest;
import es.onebox.tracer.core.AMT;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Validated
@RequestMapping(value = ApiConfig.FCBApiConfig.BASE_URL)
public class FCBController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FCBController.class);

    private static final String EVENT_PREORDER = "PREORDER";
    private static final String B2BBALANCE = "B2BBALANCE";
    private static final String EVENT_ORDER = "ORDER";
    private static final String ACTION_ABANDONED = "ABANDONED";

    private final FCBTicketService fcbTicketService;
    private final OperationCodeService operationCodeService;

    @Autowired
    public FCBController(FCBTicketService fcbTicketService, OperationCodeService operationCodeService) {
        this.fcbTicketService = fcbTicketService;
        this.operationCodeService = operationCodeService;
    }

    @PostMapping("/webhook")
    public void registerOperation(@RequestBody @Valid NotificationMessageDTO notification,
                                  @RequestHeader(name = "Ob-Action") String action,
                                  @RequestHeader(name = "Ob-Event") String event) {

        LOGGER.info("[FCB WEBHOOK] Received notification: {} - {} - {}",
                notification.getCode() != null ? notification.getCode() : notification.getMovementId(),
                event,
                action);

        AMT.addAuditProperty(AMTCustomTag.ORDER_CODE.value(), notification.getCode() != null ? notification.getCode() : notification.getMovementId());

        try {
            if (EVENT_PREORDER.equals(event) && ACTION_ABANDONED.equals(action)) {
                fcbTicketService.storeAbandonedOrder(notification.getCode());
            } else if (EVENT_ORDER.equals(event)) {
                PeticioFacturar peticioFacturar = fcbTicketService.registerOperation(notification.getCode());
                if (peticioFacturar != null) {
                    LOGGER.info("[FCB WEBHOOK] Processed order: {} - {}",
                            peticioFacturar.getMessageId(),
                            peticioFacturar.getLinies() != null && CollectionUtils.isNotEmpty(peticioFacturar.getLinies().getLinia()) ? peticioFacturar.getLinies().getLinia().get(0).getIdOperacio() : "Not processed");
                }
            } else if (B2BBALANCE.equals(event)) {
                fcbTicketService.registerBalance(notification.getMovementId());
            }
        } catch (Exception e) {
            LOGGER.error("[FCB WEBHOOK] Notification processing failed: {} - {} - {}",
                    notification.getCode() != null ? notification.getCode() : notification.getMovementId(),
                    event,
                    action);
            throw e;
        }
    }

    @GetMapping(value = "/operation-id/{orderCode}")
    public String getOperationId(@PathVariable(name = "orderCode") String orderCode) {
        return this.operationCodeService.getOperationId(orderCode);
    }

    @PostMapping(value = "/operation-id")
    public String registerOperationId(@Valid @RequestBody OperationIdRequest request) {
        return this.operationCodeService.getOrGenerateOperationId(request);
    }

    @GetMapping(value = "/venue-mappings")
    public Map<String, String> getVenueMappings() {
        return fcbTicketService.getVenueMappings();
    }

}
