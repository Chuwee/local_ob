package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.RateRestrictionDTO;
import es.onebox.mgmt.events.dto.RatesRestrictedDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.rates.CreateSeasonTicketRateRequestDTO;
import es.onebox.mgmt.seasontickets.dto.rates.SeasonTicketRateDTO;
import es.onebox.mgmt.seasontickets.dto.rates.UpdateSeasonTicketRateDTO;
import es.onebox.mgmt.seasontickets.dto.rates.UpdateSeasonTicketRatesDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketRatesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(SeasonTicketRatesController.BASE_URI)
public class SeasonTicketRatesController {

    private static final String AUDIT_COLLECTION = "SEASON_TICKET_RATES";
    private static final String AUDIT_SUBCOLLECTION_RESTRICTIONS = "RESTRICTIONS";

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/rates";

    static final String RATE = "/{rateId}";
    private static final String RATE_RESTRICTIONS = RATE + "/restrictions";
    private static final String RATES_RESTRICTIONS = "/restrictions";
    private static final String SEASON_TICKET_ID_MUST_BE_ABOVE_0 = "Season Ticket Id must be above 0";
    private static final String RATE_ID_MUST_BE_ABOVE_0 = "Rate Id must be above 0";


    @Autowired
    private SeasonTicketRatesService seasonTicketRatesService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SeasonTicketRateDTO> getSeasonTicketRates(@PathVariable Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return seasonTicketRatesService.getRates(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createSeasonTicketRate(@PathVariable Long seasonTicketId,
                                        @RequestBody @Valid CreateSeasonTicketRateRequestDTO reqDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        if (reqDTO.getName() == null || reqDTO.getName().isEmpty()) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER)
                    .setMessage("name is mandatory")
                    .build();
        }
        return seasonTicketRatesService.createRate(seasonTicketId, reqDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketRate(@PathVariable Long seasonTicketId,
                                       @RequestBody @Valid UpdateSeasonTicketRatesDTO rates) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketRatesService.updateRates(seasonTicketId, rates.getRates());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{rateId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketRate(@PathVariable Long seasonTicketId, @PathVariable Long rateId,
                                       @RequestBody @Valid UpdateSeasonTicketRateDTO rate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketRatesService.updateRate(seasonTicketId, rateId, rate);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{rateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeasonTicketRate(@PathVariable Long seasonTicketId, @PathVariable Long rateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        seasonTicketRatesService.deleteRate(seasonTicketId, rateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RATE_RESTRICTIONS)
    public RateRestrictionDTO getSeasonTicketRateRestrictions(
            @PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketRatesService.getRateRestrictions(seasonTicketId, rateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(RATE_RESTRICTIONS)
    public ResponseEntity<Serializable> updateSeasonTicketRateRestrictions(
            @PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId,
            @RequestBody @Valid RateRestrictionDTO restrictionDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_UPDATE);

        seasonTicketRatesService.updateRateRestrictions(seasonTicketId, rateId, restrictionDTO);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(RATE_RESTRICTIONS)
    public ResponseEntity<Serializable> deleteSeasonTicketRateRestrictions(
            @PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @PathVariable @Min(value = 1, message = RATE_ID_MUST_BE_ABOVE_0) Long rateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_DELETE);
        seasonTicketRatesService.deleteSeasonTicketRateRestrictions(seasonTicketId, rateId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(RATES_RESTRICTIONS)
    public RatesRestrictedDTO getRestrictedRates(
            @PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_RESTRICTIONS, AuditTag.AUDIT_ACTION_SEARCH);
        return seasonTicketRatesService.getRestrictedRates(seasonTicketId);
    }

}
