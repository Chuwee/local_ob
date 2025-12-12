package es.onebox.ath.orders.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.ath.orders.dto.OrderActivatorDTO;
import es.onebox.ath.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController("athOrderController")
@RequestMapping(ApiConfig.ATHApiConfig.BASE_URL + "/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/activators")
    public List<OrderActivatorDTO> getOrdersActivators(@RequestParam(value = "purchaseDateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime purchaseDateFrom,
                                            @RequestParam(value = "purchaseDateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime purchaseDateTo,
                                            @RequestParam(value = "channelId") Long channelId) {

        return orderService.getOrdersActivators(purchaseDateFrom, purchaseDateTo, channelId);
    }
}
