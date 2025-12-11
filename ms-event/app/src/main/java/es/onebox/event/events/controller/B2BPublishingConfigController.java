package es.onebox.event.events.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.B2BSeatPublishingConfigDTO;
import es.onebox.event.events.service.B2BPublishingConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/channels/{channelId}/b2b/venue-templates")
public class B2BPublishingConfigController {

    private final B2BPublishingConfigService b2BPublishingConfigService;

    @Autowired
    public B2BPublishingConfigController(B2BPublishingConfigService b2BPublishingConfigService) {
        this.b2BPublishingConfigService = b2BPublishingConfigService;
    }

    @GetMapping("/{templateId}/publishing-config")
    public B2BSeatPublishingConfigDTO getConfig(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                @PathVariable @Min(value = 1, message = "templateId must be above 0") Long templateId) {

        return b2BPublishingConfigService.getConfig(eventId, channelId, templateId);
    }

    @PutMapping("/{templateId}/publishing-config")
    public void updateConfig(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                             @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                             @PathVariable @Min(value = 1, message = "templateId must be above 0") Long templateId,
                             @RequestBody @NotNull B2BSeatPublishingConfigDTO b2BSeatPublishingConfigDTO) {
        b2BPublishingConfigService.updateConfig(eventId, channelId, templateId, b2BSeatPublishingConfigDTO);
    }
}
