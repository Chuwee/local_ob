package es.onebox.event.forms.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.forms.dto.FormFieldDTO;
import es.onebox.event.forms.dto.UpdateFormDTO;
import es.onebox.event.forms.enums.FormTypeDTO;
import es.onebox.event.forms.service.SeasonTicketFormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping(SeasonTicketFormsController.BASE_URI)
public class SeasonTicketFormsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/forms/{formType}";

    private final SeasonTicketFormsService seasonTicketFormsService;

    @Autowired
    public SeasonTicketFormsController(SeasonTicketFormsService seasonTicketFormsService) {
        this.seasonTicketFormsService = seasonTicketFormsService;
    }

    @GetMapping
    public List<List<FormFieldDTO>> getSeasonTicketForm(@PathVariable Long seasonTicketId, @PathVariable FormTypeDTO formType) {
        return seasonTicketFormsService.getSeasonTicketForm(seasonTicketId, formType);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketForm(@PathVariable Long seasonTicketId, @PathVariable FormTypeDTO formType, @RequestBody UpdateFormDTO form) {
        seasonTicketFormsService.updateSeasonTicketForm(seasonTicketId, formType, form);
    }
} 