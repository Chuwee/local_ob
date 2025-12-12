package es.onebox.internal.sgtm.controller;

import es.onebox.internal.sgtm.dto.SgtmWebhookRequestDTO;
import es.onebox.internal.sgtm.service.SgtmService;
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
@RequestMapping("/internal-api/v1/sgtm")
public class SgtmWebhookController {
    
    private final SgtmService sgtmService;

    @Autowired
    public SgtmWebhookController(SgtmService sgtmService) {
        this.sgtmService = sgtmService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> processWebhook(@RequestBody @Valid SgtmWebhookRequestDTO webhookRequest,
                                                HttpServletRequest httpServletRequest,
                                                @RequestParam("channelId") List<Long> channelIds) {
        sgtmService.processWebhook(webhookRequest, httpServletRequest, channelIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
