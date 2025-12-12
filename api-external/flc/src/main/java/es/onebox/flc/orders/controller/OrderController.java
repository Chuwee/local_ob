package es.onebox.flc.orders.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.flc.orders.dto.Order;
import es.onebox.flc.orders.dto.groups.VisitorGroupDTO;
import es.onebox.flc.orders.service.OrderService;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.FLCApiConfig.BASE_URL + "/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/{code}")
    public Order getOrder(@PathVariable("code") String code) {

        return orderService.getOrder(code);
    }

    @GetMapping(value = "/groups")
    public List<VisitorGroupDTO> search(@RequestParam @Size(max = 50) List<Long> groupIds) {
        return orderService.searchGroups(groupIds);
    }

    @GetMapping(value = "/groups/{groupId}")
    public VisitorGroupDTO getGroup(@PathVariable Long groupId) {
        return orderService.getGroup(groupId);
    }
}
