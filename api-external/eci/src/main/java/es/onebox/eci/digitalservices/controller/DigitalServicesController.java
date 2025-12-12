package es.onebox.eci.digitalservices.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.eci.common.GenericRequest;
import es.onebox.eci.digitalservices.dto.Order;
import es.onebox.eci.digitalservices.service.DigitalServicesService;
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
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL + "/{channelIdentifier}/digitalservices")
public class DigitalServicesController {
    @Autowired
    private DigitalServicesService digitalServicesService;

    @GetMapping()
    public List<Order> getOrdersByChannel(@RequestParam(value = "order_date[gte]") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime orderGTE,
                                          @RequestParam(value = "order_date[lte]") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime orderLTE,
                                          @PathVariable("channelIdentifier") String channelIdentifier,
                                          final @Valid @BindUsingJackson GenericRequest request) {
        return digitalServicesService.getOrdersByChannel(orderGTE, orderLTE, channelIdentifier, request.getLimit(), request.getOffset());
    }
}
