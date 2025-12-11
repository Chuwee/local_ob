package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.CreateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.SearchSeasonTicketsResponse;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSearchFilter;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketValidationsRequestDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketValidationsResponseDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketRequestDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketStatusRequestDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_TAQ;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = SeasonTicketController.BASE_URI)
public class SeasonTicketController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";

    private static final String AUDIT_COLLECTION = "SEASON_TICKETS";
    private static final int SEASON_TICKET_NAME_LENGTH = 50;
    private static final String SEASON_TICKET_ID = "seasonTicketId";

    private final SeasonTicketService seasonTicketService;

    @Autowired
    public SeasonTicketController(SeasonTicketService seasonTicketService) {
        this.seasonTicketService = seasonTicketService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{seasonTicketId}")
    public SeasonTicketDTO getSeasonTicket(@PathVariable Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        ConverterUtils.checkField(seasonTicketId, SEASON_TICKET_ID);
        return seasonTicketService.getSeasonTicket(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_TAQ})
    @GetMapping
    public SearchSeasonTicketsResponse getSeasonTickets(@BindUsingJackson @Valid SeasonTicketSearchFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return seasonTicketService.searchSeasonTickets(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createSeasonTicket(@RequestBody CreateSeasonTicketRequestDTO seasonTicketData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        if (CommonUtils.isBlank(seasonTicketData.getName())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "season ticket name is mandatory", null);
        } else if (seasonTicketData.getName().length() > SEASON_TICKET_NAME_LENGTH) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "season ticket name length cannot be above "
                    + SEASON_TICKET_NAME_LENGTH + " characters", null);
        }
        ConverterUtils.checkField(seasonTicketData.getEntityId(), "entity_id");
        ConverterUtils.checkField(seasonTicketData.getProducerId(), "producer_id");
        ConverterUtils.checkField(seasonTicketData.getCategoryId(), "category_id");
        if (seasonTicketData.getTaxId() == null && !Boolean.TRUE.equals(seasonTicketData.getAutomaticTaxes())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "tax_id is mandatory", null);
        }
        if (seasonTicketData.getChargesTaxId() == null && !Boolean.TRUE.equals(seasonTicketData.getAutomaticTaxes())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "charges_tax_id is mandatory", null);
        }
        ConverterUtils.checkField(seasonTicketData.getVenueConfigId(), "venue_config_id");
        return new IdDTO(seasonTicketService.createSeasonTicket(seasonTicketData));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{seasonTicketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicket(@PathVariable Long seasonTicketId, @Valid @RequestBody UpdateSeasonTicketRequestDTO seasonTicketData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (seasonTicketData.getId() != null && !seasonTicketData.getId().equals(seasonTicketId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "seasonTicketId is different between pathVariable and requestBody", null);
        }
        seasonTicketService.updateSeasonTicket(seasonTicketId, seasonTicketData);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{seasonTicketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeasonTicket(@PathVariable Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        ConverterUtils.checkField(seasonTicketId, SEASON_TICKET_ID);
        seasonTicketService.deleteSeasonTicket(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping( value = "/{seasonTicketId}/status")
    public SeasonTicketStatusResponseDTO getSeasonTicketStatus(@PathVariable Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_STATUS);

        ConverterUtils.checkField(seasonTicketId, SEASON_TICKET_ID);
        return seasonTicketService.getSeasonTicketStatus(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{seasonTicketId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketStatus(@PathVariable Long seasonTicketId,
                                                                 @RequestBody UpdateSeasonTicketStatusRequestDTO updateSeasonTicketStatusRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_STATUS);

        ConverterUtils.checkField(seasonTicketId, SEASON_TICKET_ID);
        seasonTicketService.updateSeasonTicketStatus(seasonTicketId, updateSeasonTicketStatusRequestDTO);
    }

    private static void checkParams(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId < 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Invalid seasonTicketId", null);
        }
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping( value = "/{seasonTicketId}/validations")
    public SeasonTicketValidationsResponseDTO getValidations(@PathVariable Long seasonTicketId, @BindUsingJackson @Valid SeasonTicketValidationsRequestDTO requestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, "VALIDATIONS");
        checkParams(seasonTicketId);
        return seasonTicketService.getValidations(seasonTicketId, requestDTO);
    }
}
