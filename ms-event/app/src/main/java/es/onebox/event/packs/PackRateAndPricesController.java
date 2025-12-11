package es.onebox.event.packs;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateProducer;
import es.onebox.event.packs.dto.CreatePackRateDTO;
import es.onebox.event.packs.dto.PackPriceDTO;
import es.onebox.event.packs.dto.UpdatePackPriceDTO;
import es.onebox.event.packs.dto.UpdatePackRateDTO;
import es.onebox.event.packs.service.PackRateAndPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(PackRateAndPricesController.BASE_URI)
public class PackRateAndPricesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/packs/{packId}";
    private static final String ORIGIN = "ms-event packRateAndPricesController";

    private final PackRateAndPricesService packRateAndPricesService;
    private final CatalogPacksUpdateProducer catalogPacksUpdateProducer;

    @Autowired
    public PackRateAndPricesController(PackRateAndPricesService packRateAndPricesService, CatalogPacksUpdateProducer catalogPacksUpdateProducer) {
        this.packRateAndPricesService = packRateAndPricesService;
        this.catalogPacksUpdateProducer = catalogPacksUpdateProducer;
    }

    @GetMapping("/rates")
    public List<RateDTO> getPackRates(@PathVariable(value = "packId") Long packId) {
        return packRateAndPricesService.getPackRates(packId);
    }

    @PostMapping("/rates")
    public IdDTO createPackRate(@PathVariable(value = "packId") Long packId,
                                @RequestBody CreatePackRateDTO packRateDTO) {
        IdDTO rateId = packRateAndPricesService.createPackRate(packId, packRateDTO);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " createPackRate");
        return rateId;
    }

    @PostMapping("/rates/refresh")
    public void refreshPackRates(@PathVariable(value = "packId") Long packId) {
        packRateAndPricesService.refreshPackRates(packId);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " refreshPackRates");
    }

    @PutMapping("/rates/{rateId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePackRate(@PathVariable(value = "packId") Long packId,
                               @PathVariable(value = "rateId") Long rateId,
                               @RequestBody UpdatePackRateDTO packRateDTO) {
        packRateAndPricesService.updatePackRate(packId, rateId, packRateDTO);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackRate");
    }

    @DeleteMapping("/rates/{rateId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deletePackRate(@PathVariable(value = "packId") Long packId,
                               @PathVariable(value = "rateId") Long rateId) {
        packRateAndPricesService.deletePackRate(packId, rateId);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " deletePackRate");
    }

    @GetMapping("/prices")
    public List<PackPriceDTO> getPackPrices(@PathVariable(value = "packId") Long packId) {
        return packRateAndPricesService.getPackPrices(packId);
    }

    @PutMapping("/prices")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePackPrices(@PathVariable(value = "packId") Long packId,
                                 @RequestBody UpdatePackPriceDTO[] prices) {
        packRateAndPricesService.updatePackPrices(packId, Arrays.asList(prices));
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackPrices");
    }

}
