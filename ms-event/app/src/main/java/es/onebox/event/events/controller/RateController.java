package es.onebox.event.events.controller;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventRatesDTO;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.events.service.EventRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RateController.BASE_URI)
public class RateController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/rates";
    private static final String SEARCH = "/search";

    private final EventRateService eventRateService;

    @Autowired
    public RateController(EventRateService eventRateService) {
        this.eventRateService = eventRateService;
    }

    @PostMapping(value = SEARCH)
    public EventRatesDTO searchEventRates(@RequestBody RatesFilter filter) {
        return eventRateService.searchRatesByFilter(filter);
    }

}
