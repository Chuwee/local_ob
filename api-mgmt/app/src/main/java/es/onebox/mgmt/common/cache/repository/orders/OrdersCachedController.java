package es.onebox.mgmt.common.cache.repository.orders;

import es.onebox.mgmt.common.cache.enums.OrdersCachedMappingsType;
import es.onebox.mgmt.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(OrdersCachedController.BASE_URI)
public class OrdersCachedController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/orders/cache";
    private final OrdersCachedRepository cached;

    @Autowired
    public OrdersCachedController(OrdersCachedRepository cached) {
        this.cached = cached;
    }

    @DeleteMapping()
    public void deleteAll() {
        cached.deleteAll();
    }

    @DeleteMapping(value = "/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        cached.delete(id, OrdersCachedMappingsType.EVENTS_WITH_SALES);
    }

    @DeleteMapping(value = "/sessions/{id}")
    public void deleteSession(@PathVariable Long id) {
        cached.delete(id, OrdersCachedMappingsType.SESSIONS_WITH_SALES);
    }

    @GetMapping(value = "/events/{id}")
    public Long getEvents(@PathVariable Long id) {
        return cached.get(id, OrdersCachedMappingsType.EVENTS_WITH_SALES);
    }

    @GetMapping(value = "/sessions/{id}")
    public Long getSessions(@PathVariable Long id) {
        return cached.get(id, OrdersCachedMappingsType.SESSIONS_WITH_SALES);
    }
}

