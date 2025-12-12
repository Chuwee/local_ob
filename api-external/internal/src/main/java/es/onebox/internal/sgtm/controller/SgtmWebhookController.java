package es.onebox.internal.sgtm.controller;

import es.onebox.internal.config.InternalApiConfig;
import es.onebox.internal.sgtm.dto.SgtmWebhookRequestDTO;
import es.onebox.internal.sgtm.service.SgtmService;
import es.onebox.internal.sgtm.service.SkipProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(InternalApiConfig.SGTM.BASE_URL + "/webhook")
public class SgtmWebhookController {

    private final SgtmService sgtmService;

    @Autowired
    public SgtmWebhookController(SgtmService sgtmService) {
        this.sgtmService = sgtmService;
    }

    @PostMapping
    public ResponseEntity<Void> processWebhook(@RequestBody @Valid SgtmWebhookRequestDTO request, HttpServletRequest httpServletRequest,
                                              @RequestParam(value = "channelId", required = false) List<Long> channelIds) {
        try {
            sgtmService.processWebhook(request, httpServletRequest, channelIds);
            return ResponseEntity.ok().build();
        } catch (SkipProcessingException e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }
} 