package es.onebox.mgmt.sessions.dynamicprice;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceConfigDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicRatesPriceDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceStatusRequestDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.RequestDynamicPriceDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceZoneDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(
        value = DynamicPriceController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DynamicPriceController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/dynamic-prices";
    public static final String PRICE_ZONES = "/price-zones/{idPriceZone}";
    public static final String RATES = PRICE_ZONES + "/rates";
    public static final String ZONE_ID = PRICE_ZONES  + "/zone/{orderId}";
    private static final String FIELD_EVENT_ID = "eventId";
    private static final String FIELD_SESSION_ID = "sessionId";
    private static final String FIELD_ID_PRICE_ZONE = "idPriceZone";
    private final DynamicPriceService dynamicPriceService;

    public DynamicPriceController(DynamicPriceService dynamicPriceService) {
        this.dynamicPriceService = dynamicPriceService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping
    public DynamicPriceConfigDTO getDynamicPriceConfig(@PathVariable Long eventId, @PathVariable Long sessionId){
        checkEventAndSessionIds(eventId, sessionId);
        return dynamicPriceService.getDynamicPriceConfig(eventId, sessionId, Boolean.TRUE);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping
    public void activateDeactivateDynamicPriceConfig(@PathVariable Long eventId, @PathVariable Long sessionId,
                                                     @NotNull @RequestBody DynamicPriceStatusRequestDTO request){
        checkEventAndSessionIds(eventId, sessionId);
        dynamicPriceService.activateDynamicPriceConfig(eventId, sessionId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(PRICE_ZONES)
    public DynamicPriceZoneDTO getDynamicPriceZone(@PathVariable Long eventId, @PathVariable Long sessionId,
                                                   @PathVariable Long idPriceZone){
        checkEventSessionAndPriceZoneIds(eventId, sessionId, idPriceZone);
        return dynamicPriceService.getDynamicPriceZone(eventId, sessionId, idPriceZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(RATES)
    public List<DynamicRatesPriceDTO> getDynamicRatePriceRates(@PathVariable Long eventId, @PathVariable Long sessionId,
                                                          @PathVariable Long idPriceZone) {
        checkEventSessionAndPriceZoneIds(eventId, sessionId, idPriceZone);
        return dynamicPriceService.getDynamicRatePrice(eventId, sessionId, idPriceZone);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = PRICE_ZONES, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateDynamicPriceConfig(@PathVariable Long eventId, @PathVariable Long sessionId,
                                                 @PathVariable Long idPriceZone,
                                                 @Valid @NotNull @RequestBody RequestDynamicPriceDTO[] requests) {
        checkEventSessionAndPriceZoneIds(eventId, sessionId, idPriceZone);
        dynamicPriceService.createOrUpdateDynamicPriceConfig(eventId, sessionId, idPriceZone, Arrays.asList(requests));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(ZONE_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDynamicPrice(@PathVariable(value = "eventId") Long eventId,
                                   @PathVariable(value = "sessionId") Long sessionId,
                                   @PathVariable(value = "idPriceZone") Long idPriceZone,
                                   @NotNull @PathVariable(value = "orderId") Integer orderId) {
        checkEventSessionAndPriceZoneIds(eventId, sessionId, idPriceZone);
        dynamicPriceService.deleteDynamicPriceConfig(eventId, sessionId, idPriceZone, orderId);
    }

    private static void checkEventAndSessionIds(Long eventId, Long sessionId) {
        ConverterUtils.checkField(eventId, FIELD_EVENT_ID);
        ConverterUtils.checkField(sessionId, FIELD_SESSION_ID);
    }

    private static void checkEventSessionAndPriceZoneIds(Long eventId, Long sessionId, Long idPriceZone) {
        checkEventAndSessionIds(eventId, sessionId);
        ConverterUtils.checkField(idPriceZone, FIELD_ID_PRICE_ZONE);
    }
}
