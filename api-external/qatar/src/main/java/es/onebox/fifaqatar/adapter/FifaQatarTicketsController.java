package es.onebox.fifaqatar.adapter;

import es.onebox.common.config.ApiConfig;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import es.onebox.fifaqatar.adapter.dto.response.TicketsResponse;
import es.onebox.fifaqatar.adapter.dto.response.error.ErrorResponse;
import es.onebox.fifaqatar.adapter.dto.response.orderdetail.OrderDetailResponse;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketDetailResponse;
import es.onebox.fifaqatar.config.context.AppRequestContext;
import es.onebox.fifaqatar.error.FifaQatarBaseException;
import es.onebox.hazelcast.core.service.HazelcastLockService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = ApiConfig.QatarApiConfig.BASE_URL)
public class FifaQatarTicketsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarTicketsController.class);

    private final FifaQatarService fifaQatarService;
    private final HazelcastLockService hazelcastLockService;

    public FifaQatarTicketsController(FifaQatarService fifaQatarService, HazelcastLockService hazelcastLockService) {
        this.fifaQatarService = fifaQatarService;
        this.hazelcastLockService = hazelcastLockService;
    }

    @GetMapping("/4.1/users/{userId}/tickets")
    public TicketsResponse tickets(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page) throws Exception {
        MeResponseDTO currentUser = AppRequestContext.getCurrentUser();
        return hazelcastLockService.lockedExecution(() -> fifaQatarService.tickets(page), String.valueOf(currentUser.getId()), 30000, false);
    }

    @GetMapping("/4.3/tickets/{ticketId}")
    public TicketDetailResponse ticketDetail(
            @PathVariable Integer ticketId) {
        return fifaQatarService.ticketDetail(ticketId);
    }

    @GetMapping(value = "/4.2/orders/{orderId}")
    public OrderDetailResponse orderDetail(
            @PathVariable Integer orderId) {
        return fifaQatarService.orderDetail(orderId);
    }

    @GetMapping(value = "/qr-code/{code}", produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity<byte[]> getQrImage(
            @PathVariable String code,
            @RequestParam String signature) throws IOException {
        return fifaQatarService.getQrImage(code, signature);
    }

    @ExceptionHandler(FifaQatarBaseException.class)
    public ResponseEntity<ErrorResponse> handleAppException(HttpServletRequest request, FifaQatarBaseException ex) {
        LOGGER.error("Error in call: " + request.getRequestURL().toString(), ex);
        Class<? extends FifaQatarBaseException> aClass = ex.getClass();
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(aClass, ResponseStatus.class);

        return ResponseEntity.status(responseStatus.code()).body(null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request, Exception e) {
        LOGGER.error("Error in call: " + request.getRequestURL().toString(), e);
        var error = new ErrorResponse();
        error.setCode("GENERIC_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
