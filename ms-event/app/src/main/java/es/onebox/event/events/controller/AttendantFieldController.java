package es.onebox.event.events.controller;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.AttendantFieldsDTO;
import es.onebox.event.events.dto.FieldsDTO;
import es.onebox.event.events.dto.UpdateAttendantFieldDTO;
import es.onebox.event.events.service.AttendantFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class AttendantFieldController {

    private final AttendantFieldsService attendantFieldsService;

    @Autowired
    public AttendantFieldController(AttendantFieldsService attendantFieldsService){
        this.attendantFieldsService = attendantFieldsService;
    }

    @RequestMapping(method = GET, value = "/attendants-available-fields")
    public FieldsDTO getAvailableFields() {
        return attendantFieldsService.getAvailableFields();
    }


    @RequestMapping(method = GET, value = "/events/{eventId}/fields")
    public AttendantFieldsDTO getAttendantFields(@PathVariable(value = "eventId") Integer eventId, BaseRequestFilter filter) {
        return attendantFieldsService.getAttendantFields(eventId, filter);
    }

    @RequestMapping(method = POST, value = "/events/{eventId}/fields")
    public void createAttendantFields(@RequestBody UpdateAttendantFieldDTO[] fields, @PathVariable(value = "eventId") Integer eventId) {
        attendantFieldsService.createAttendantFields(eventId, new HashSet<>(Arrays.asList(fields)));
    }

}
