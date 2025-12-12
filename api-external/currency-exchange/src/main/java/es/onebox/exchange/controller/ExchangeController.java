package es.onebox.exchange.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.exchange.dto.ExchangeRequestDTO;
import es.onebox.exchange.dto.ExchangeResponseDTO;
import es.onebox.exchange.service.ExchangeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ExchangeController.BASE_URI)
public class ExchangeController {

    public static final String BASE_URI = ApiConfig.CurrencyExchangeApiConfig.BASE_URL + "/currency-exchange";

    private final ExchangeService exchangeService;

    @Autowired
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping
    public ExchangeResponseDTO getExchange(@BindUsingJackson @Valid ExchangeRequestDTO filter){
        return exchangeService.getExchange(filter);
    }

}
