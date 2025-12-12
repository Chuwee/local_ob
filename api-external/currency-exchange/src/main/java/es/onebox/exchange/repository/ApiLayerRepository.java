package es.onebox.exchange.repository;

import es.onebox.cache.annotation.CachedArg;
import es.onebox.exchange.dto.ExchangeResponse;
import es.onebox.exchange.datasource.ApiLayerDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ApiLayerRepository {

    private final ApiLayerDatasource apiLayerDatasource;

    @Autowired
    public ApiLayerRepository(ApiLayerDatasource apiLayerDatasource) {
        this.apiLayerDatasource = apiLayerDatasource;
    }

    public ExchangeResponse getCurrencyExchange(@CachedArg String currencyCode) {
        return apiLayerDatasource.getCurrenciesExchange(currencyCode);
    }

}
