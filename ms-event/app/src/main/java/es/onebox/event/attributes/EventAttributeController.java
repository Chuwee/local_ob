package es.onebox.event.attributes;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events")
public class EventAttributeController {

    @Autowired
    private EventAttributeService eventAttributeService;
    @Autowired
    private RefreshDataService refreshDataService;

    @RequestMapping(method = RequestMethod.GET, value = "/{eventId}/attributes")
    public List<AttributeDTO> getEventAttributes(@PathVariable Long eventId) {
        return eventAttributeService.getEventAttributes(eventId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/attributes")
    public Map<Integer, Map<Integer, List<Integer>>> getEventsAttributes(@RequestParam List<Long> eventId) {
        return eventAttributeService.getEventsAttributes(eventId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{eventId}/attributes")
    public void putEventAttributes(@PathVariable Long eventId,
                                   @Valid @RequestBody AttributeRequestValueDTO[] attributeRequestValueDTO) {
        eventAttributeService.putEventAttributes(eventId, Arrays.asList(attributeRequestValueDTO));
        refreshDataService.refreshEvent(eventId, "putEventAttributes");
    }

}
