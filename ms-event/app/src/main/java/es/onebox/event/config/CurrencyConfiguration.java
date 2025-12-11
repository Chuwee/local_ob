package es.onebox.event.config;

import es.onebox.core.utils.common.CurrencyUtils;
import es.onebox.core.utils.dto.CurrencyConfig;
import es.onebox.event.datasources.ms.entity.dto.CurrencyDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrencyConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConfiguration.class);

    @Autowired
    private EntitiesRepository entitiesRepository;

    @PostConstruct
    public void init() {
        try {
            List<CurrencyDTO> currencies = entitiesRepository.getCurrencies();
            for (CurrencyDTO currency : currencies) {
                CurrencyUtils.initCurrency(currency.getCode(), new CurrencyConfig(currency.getId(), currency.getCode(),
                        currency.getLocale(), currency.getIso(), currency.getHexValue()));
            }
        } catch (Exception e) {
            LOGGER.error("Error reloading currency values. Start application with default values", e);
        }
    }
}
