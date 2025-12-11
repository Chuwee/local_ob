package es.onebox.event.passbook;

import es.onebox.event.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class PassbookController {

    private final PassbookService passbookService;

    @Autowired
    public PassbookController(PassbookService passbookService) {
        this.passbookService = passbookService;
    }

    @RequestMapping(
            method = GET,
            value = "/events/{eventId}/passbookConfig")
    public PassbookConfig getPassbookConfig(@PathVariable(value = "eventId") Long eventId) {
        return passbookService.getPassbookConfig(eventId);
    }

    @RequestMapping(
            method = GET,
            value = "/events/{eventId}/passbookTemplateCode")
    public EventPassbookTemplatesDTO getPassbookTemplate(@PathVariable(value = "eventId") Long eventId) {
        return passbookService.getPassbookTemplateCode(eventId);
    }
}
