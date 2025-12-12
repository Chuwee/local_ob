package es.onebox.eci.promotions.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.eci.common.GenericRequest;
import es.onebox.eci.promotions.dto.Promotion;
import es.onebox.eci.promotions.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL + "/{channelIdentifier}/promotions")
public class PromotionController {


    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping()
    public List<Promotion> getPromotions(@RequestParam(value = "session_start_date[gte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime gte,
                                         @RequestParam(value = "session_start_date[lte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime lte,
                                         @PathVariable("channelIdentifier") String channelIdentifier,
                                         final @Valid @BindUsingJackson GenericRequest request) {

        return promotionService.getPromotions(gte, lte, request.getLimit(), request.getOffset(), channelIdentifier);
    }

    @GetMapping(value = "/{promotionId}")
    public Promotion getPromotion(@PathVariable("channelIdentifier") String channelIdentifier,
                                  @PathVariable("promotionId") Long promotionId) {

        return promotionService.getPromotion(channelIdentifier, promotionId);
    }
}
