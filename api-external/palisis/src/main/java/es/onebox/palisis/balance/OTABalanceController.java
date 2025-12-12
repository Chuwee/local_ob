package es.onebox.palisis.balance;

import es.onebox.common.config.ApiConfig;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Validated
@RequestMapping(value = ApiConfig.PalisisApiConfig.BASE_URL)
public class OTABalanceController {

    private final OTABalanceService service;

    public OTABalanceController(OTABalanceService service) {
        this.service = service;
    }

    @GetMapping("/entities/{entityId}/balance")
    public Map<String, Double> getOTAEntitiesBalance(@PathVariable Long entityId) {
        return service.getOTABalance(entityId);
    }

}
