package es.onebox.event.events.dynamicpricing;

import es.onebox.event.config.ApiConfig;
import es.onebox.jooq.dp.tables.records.OneboxTimeSlotTierAssignmentsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/dynamicpricing")
public class DynamicPricingController {

    private final DynamicPricingService dynamicPricingService;

    @Autowired
    public DynamicPricingController(DynamicPricingService dynamicPricingService) {
        this.dynamicPricingService = dynamicPricingService;
    }

    @GetMapping()
    public List<OneboxTimeSlotTierAssignmentsRecord> getAllPrices() {
        return dynamicPricingService.getAllPrices();
    }

    @GetMapping("/lastexecution")
    public List<OneboxTimeSlotTierAssignmentsRecord> getLastExecution() {
        return dynamicPricingService.getLastExecution();
    }

    @RequestMapping(method = PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePrices() {
        dynamicPricingService.updatePrices();
    }
}
