package es.onebox.event.tags.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.tags.service.SessionTagService;
import es.onebox.event.tags.dto.SessionTagRequestDTO;
import es.onebox.event.tags.dto.SessionTagResponseDTO;
import es.onebox.event.tags.dto.SessionTagsResponseDTO;
import org.springframework.http.HttpStatus;
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

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping(SessionTagController.BASE_URI)
public class SessionTagController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/sessions/{sessionId}/tags";

    private final SessionTagService sessionTagService;

    public SessionTagController(SessionTagService sessionTagService) {
        this.sessionTagService = sessionTagService;
    }

    @GetMapping
    public SessionTagsResponseDTO getSessionTags(@PathVariable(value = "eventId") Long eventId,
                                                 @PathVariable(value = "sessionId") Long sessionId) {
        return sessionTagService.getSessionTags(eventId, sessionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionTagResponseDTO createSessionTag(@PathVariable(value = "eventId") Long eventId,
                                                  @PathVariable(value = "sessionId") Long sessionId,
                                                  @RequestBody @Valid SessionTagRequestDTO sessionTagRequestDTO) {
        return sessionTagService.createSessionTag(eventId, sessionId, sessionTagRequestDTO);
    }

    @PutMapping("/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionTag(@PathVariable(value = "eventId") Long eventId,
                              @PathVariable(value = "sessionId") Long sessionId,
                              @PathVariable(value = "positionId") Long positionId,
                              @RequestBody @Valid SessionTagRequestDTO sessionTagRequestDTO) {
        sessionTagService.updateSessionTag(eventId, sessionId, positionId, sessionTagRequestDTO);
    }

    @DeleteMapping("/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSessionTag(@PathVariable(value = "eventId") Long eventId,
                                 @PathVariable(value = "sessionId") Long sessionId,
                                 @PathVariable(value = "positionId") Long positionId) {
        sessionTagService.deleteSessionTag(eventId, sessionId, positionId);
    }
}
