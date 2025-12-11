package es.onebox.event.surcharges.product;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.surcharges.product.dto.ProductSurchargeListDTO;
import es.onebox.event.surcharges.product.enums.ProductSurchargeType;
import es.onebox.event.surcharges.product.dto.ProductSurchargeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ProductSurchargesController.BASE_URI)
public class ProductSurchargesController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/surcharges";

    private final ProductSurchargesService productSurchargesService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public ProductSurchargesController(ProductSurchargesService productSurchargesService,
                                       RefreshDataService refreshDataService) {
        this.productSurchargesService = productSurchargesService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping()
    public List<ProductSurchargeDTO> getProductSurcharge(@PathVariable Long productId,
                                                         @RequestParam(value = "types", required = false) List<ProductSurchargeType> types) {
        return productSurchargesService.getRanges(productId, types);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setProductSurcharge(@PathVariable Long productId, @RequestBody ProductSurchargeListDTO surchargeListDTO) {
        productSurchargesService.setSurcharges(productId, surchargeListDTO);
        refreshDataService.refreshProduct(productId);
    }
}
