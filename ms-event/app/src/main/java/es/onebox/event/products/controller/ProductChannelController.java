package es.onebox.event.products.controller;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.CreateProductChannelDTO;
import es.onebox.event.products.dto.CreateProductChannelsResponseDTO;
import es.onebox.event.products.dto.ProductChannelDTO;
import es.onebox.event.products.dto.ProductChannelSessionDTO;
import es.onebox.event.products.dto.ProductChannelSessionsFilter;
import es.onebox.event.products.dto.ProductChannelsDTO;
import es.onebox.event.products.dto.UpdateProductChannelDTO;
import es.onebox.event.products.service.ProductChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(value = ProductChannelController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductChannelController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/channels";

    private final ProductChannelService productChannelService;

    @Autowired
    public ProductChannelController(ProductChannelService productChannelService) {
        this.productChannelService = productChannelService;
    }

    @GetMapping()
    public ResponseEntity<ProductChannelsDTO> getProductChannels(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        ProductChannelsDTO productChannelsDTO = productChannelService.getProductChannels(productId);
        return new ResponseEntity<>(productChannelsDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{channelId}")
    public ResponseEntity<ProductChannelDTO> getProductChannel(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                                               @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId) {
        ProductChannelDTO productChannelDTO = productChannelService.getProductChannel(productId, channelId);
        return new ResponseEntity<>(productChannelDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductChannel(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                     @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId,
                                     @RequestBody @NotNull UpdateProductChannelDTO updateProductChannelDTO) {
        productChannelService.updateProductChannel(productId, channelId, updateProductChannelDTO);
    }

    @PostMapping
    public ResponseEntity<CreateProductChannelsResponseDTO> createProductChannels(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Valid @RequestBody CreateProductChannelDTO createProductChannelDTO) {
        CreateProductChannelsResponseDTO productChannelsDTO = productChannelService.createProductChannels(productId, createProductChannelDTO);
        return new ResponseEntity<>(productChannelsDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductChannel(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                     @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId) {
        productChannelService.deleteProductChannel(productId, channelId);
    }

    @GetMapping(value = "/{channelId}/sessions")
    public ListWithMetadata<ProductChannelSessionDTO> getProductChannelLinksByLanguage(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId,
            @BindUsingJackson @Valid ProductChannelSessionsFilter filter
    ) {
        return productChannelService.getProductChannelSessions(productId, channelId, filter);
    }
}
