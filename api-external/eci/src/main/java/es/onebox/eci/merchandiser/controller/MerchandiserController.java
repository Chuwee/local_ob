package es.onebox.eci.merchandiser.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.eci.merchandiser.converter.CSVConverter;
import es.onebox.eci.merchandiser.converter.MerchandiserConverter;
import es.onebox.eci.merchandiser.service.MerchandiserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@Valid
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL + "/merchandiser")
public class MerchandiserController {

    private final MerchandiserService merchandiserService;

    @Autowired
    public MerchandiserController (MerchandiserService merchandiserService) {
        this.merchandiserService = merchandiserService;
    }

    @GetMapping(value = "/info")
    public String getInfo () { return "Información de la API para ECI"; }


    @GetMapping()
    public void getResults (HttpServletResponse response,
            @Valid @RequestParam("purchase_date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @Valid @RequestParam("purchase_date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to)
            throws IOException {

        List<OrderItem> orderItems =  merchandiserService.getOrderItems(from, to);
        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().print(CSVConverter.mapToCSV(MerchandiserConverter.getAggregatedEvents(orderItems)));
    }
}
