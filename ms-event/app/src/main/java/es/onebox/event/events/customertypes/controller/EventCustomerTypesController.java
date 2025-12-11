package es.onebox.event.events.customertypes.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.customertypes.dto.EventCustomerTypeDTO;
import es.onebox.event.events.customertypes.dto.UpdateEventCustomerTypesDTO;
import es.onebox.event.events.customertypes.service.EventCustomerTypesService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(EventCustomerTypesController.BASE_URI)
public class EventCustomerTypesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/customer-types";

    private final EventCustomerTypesService service;

    @Autowired
    public EventCustomerTypesController(EventCustomerTypesService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventCustomerTypeDTO> getEventCustomerTypes(@PathVariable(value = "eventId")
                                                            @Min(value = 1, message = "eventId must be above 0")
                                                            Integer eventId) {
        return service.getEventCustomerTypes(eventId);
    }

    @PutMapping
    public void putEventCustomerTypes(@PathVariable(value = "eventId")
                                      @Min(value = 1, message = "eventId must be above 0")
                                      Integer eventId, @RequestBody UpdateEventCustomerTypesDTO dto) {
        service.putEventCustomerTypes(eventId, dto);
    }
}
