package es.onebox.event.events.avetrestrictions.controller;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionCreateDTO;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionDetailDTO;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionsDTO;
import es.onebox.event.events.avetrestrictions.dto.UpdateAvetSectorRestrictionDTO;
import es.onebox.event.events.avetrestrictions.service.AvetSectorRestrictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(value = AvetSectorRestrictionController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class AvetSectorRestrictionController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/avet-sector-restrictions";

    private final AvetSectorRestrictionService avetSectorRestrictionService;

    @Autowired
    public AvetSectorRestrictionController(AvetSectorRestrictionService avetSectorRestrictionService) {
        this.avetSectorRestrictionService = avetSectorRestrictionService;
    }

    @GetMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.OK)
    public AvetSectorRestrictionDetailDTO get(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                              @PathVariable(value = "sid") @NotNull String restrictionId) {
        return avetSectorRestrictionService.getAvetSectorRestriction(eventId, restrictionId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public AvetSectorRestrictionsDTO getAvetSectorRestrictions
            (@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
             @RequestParam(required = false) Boolean fullPayload) {
        return avetSectorRestrictionService.getAvetSectorRestrictions(eventId, fullPayload);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CodeDTO> create(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                          @RequestBody @Valid AvetSectorRestrictionCreateDTO avetSectorRestrictionCreateDTO) {
        String restrictionId = avetSectorRestrictionService.createAvetSectorRestriction(eventId, avetSectorRestrictionCreateDTO);

        return new ResponseEntity<>(new CodeDTO(restrictionId), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                       @PathVariable(value = "sid") @NotNull String restrictionId,
                       @Valid @RequestBody UpdateAvetSectorRestrictionDTO updateAvetSectorRestrictionDTO) {
        avetSectorRestrictionService.updateAvetSectorRestriction(eventId, restrictionId, updateAvetSectorRestrictionDTO);
    }

    @DeleteMapping(value = "/{sid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                       @PathVariable(value = "sid") @NotNull String restrictionId) {
        avetSectorRestrictionService.deleteAvetSectorRestriction(eventId, restrictionId);
    }
}
