package es.onebox.event.pricesengine;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.pricesengine.dto.VenueConfigPricesSimulationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = PriceSimulationController.BASE_URI,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class PriceSimulationController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/price-engine/{saleRequestId}/simulation";

    private final PriceSimulationService priceSimulationService;

    @Autowired
    public PriceSimulationController(PriceSimulationService priceSimulationService) {
        this.priceSimulationService = priceSimulationService;
    }


    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<VenueConfigPricesSimulationDTO> pricesSimulation(@PathVariable Long saleRequestId) {
        return priceSimulationService.getPriceSimulationBySaleRequestId(saleRequestId);
    }

}
