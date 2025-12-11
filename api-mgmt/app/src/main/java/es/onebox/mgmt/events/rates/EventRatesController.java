package es.onebox.mgmt.events.rates;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.CreateEventRateRequestDTO;
import es.onebox.mgmt.events.dto.EventRateDTO;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.events.dto.UpdateEventRateListDTO;
import es.onebox.mgmt.events.dto.UpdateRateDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(EventRatesController.BASE_URI)
public class EventRatesController {

    private static final String AUDIT_COLLECTION = "EVENT_RATES";
    private static final String AUDIT_SUBCOLLECTION_RESTRICTIONS = "RESTRICTIONS";

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}";
    static final String RATES = "/rates";
    static final String RATE = RATES + "/{rateId}";
    private static final String RATE_RESTRICTIONS = RATE + "/restrictions";
    private static final String RATES_RESTRICTIONS = RATES + "/restrictions";
    private static final String RATES_EXTERNAL_TYPES = RATES + "/external-types";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String RATE_ID_MUST_BE_ABOVE_0 = "Rate Id must be above 0";

    private final EventRatesService eventRatesService;

    public EventRatesController(EventRatesService eventRatesService) {
        this.eventRatesService = eventRatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RATES)
    public List<EventRateDTO> getEventRates(@PathVariable Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventRatesService.getRates(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(RATES)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createEventRate(@PathVariable Long eventId,
                                 @RequestBody @Valid CreateEventRateRequestDTO reqDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        if (reqDTO.getName() == null || reqDTO.getName().isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("name is mandatory")
                    .build();
        }
        return eventRatesService.createRate(eventId, reqDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(RATES)
    public ResponseEntity<Serializable> updateEventRate(@PathVariable Long eventId,
                                                        @RequestBody @Valid UpdateEventRateListDTO rates) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        eventRatesService.updateRates(eventId, rates.getRates());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(RATE)
    public ResponseEntity<Serializable> updateEventRate(@PathVariable Long eventId, @PathVariable Long rateId,
                                                        @RequestBody @Valid UpdateRateDTO rate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        eventRatesService.updateRate(eventId, rateId, rate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(RATE)
    public ResponseEntity<Serializable> deleteEventRate(@PathVariable Long eventId, @PathVariable Long rateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        eventRatesService.deleteRate(eventId, rateId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RATES_RESTRICTIONS)
    public RatesRestrictedDTO getRestrictedRates(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_SEARCH);
        return eventRatesService.getRestrictedRates(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RATES_EXTERNAL_TYPES)
    public List<IdNameCodeDTO> getRatesExternalTypes(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_SEARCH);
        return eventRatesService.getRatesExternalTypes(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RATE_RESTRICTIONS)
    public RateRestrictionDTO getEventRateRestrictions(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_GET);
        return eventRatesService.getRateRestrictions(eventId, rateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(RATE_RESTRICTIONS)
    public ResponseEntity<Serializable> updateEventRateRestrictions(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId,
            @RequestBody @Valid RateRestrictionDTO restrictionDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_UPDATE);

        eventRatesService.updateRateRestrictions(eventId, rateId, restrictionDTO);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(RATE_RESTRICTIONS)
    public ResponseEntity<Serializable> deleteEventRateRestrictions(
            @PathVariable @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_DELETE);
        eventRatesService.deleteEventRateRestrictions(eventId, rateId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
