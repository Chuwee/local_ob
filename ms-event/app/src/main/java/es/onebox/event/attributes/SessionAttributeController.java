package es.onebox.event.attributes;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/sessions")
public class SessionAttributeController {

    private static final String ORIGIN = "putSessionAttributes";

    @Autowired
    private SessionAttributeService sessionAttributeService;
    @Autowired
    private RefreshDataService refreshDataService;

    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}/attributes")
    public List<AttributeDTO> getSessionAttributes(@PathVariable Long eventId, @PathVariable Long sessionId) {
        return sessionAttributeService.getSessionAttributes(eventId, sessionId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{sessionId}/attributes")
    public void putSessionAttributes(@PathVariable Long eventId,
                                     @PathVariable Long sessionId,
                                     @Valid @RequestBody AttributeRequestValueDTO[] attributeRequestValueDTO) {
        sessionAttributeService.putSessionAttributes(eventId, sessionId, Arrays.asList(attributeRequestValueDTO));
        refreshDataService.refreshSession(sessionId, ORIGIN);
    }

}
