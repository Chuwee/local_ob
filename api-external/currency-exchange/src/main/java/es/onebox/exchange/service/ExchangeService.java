package es.onebox.exchange.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.exchange.config.ApiLayerProperties;
import es.onebox.exchange.converter.ExchangeConverter;
import es.onebox.exchange.dto.ExchangeRequestDTO;
import es.onebox.exchange.dto.ExchangeResponse;
import es.onebox.exchange.dto.ExchangeResponseDTO;
import es.onebox.exchange.repository.ApiLayerRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ExchangeService {

    private static final String CACHE_EXCHANGE = "exchange";

    private final ApiLayerRepository apiLayerRepository;
    private final CacheRepository cacheRepository;
    private final ApiLayerProperties apiLayerProperties;

    @Autowired
    public ExchangeService(ApiLayerRepository apiLayerRepository, CacheRepository cacheRepository,
                           ApiLayerProperties apiLayerProperties)
    {
        this.apiLayerRepository = apiLayerRepository;
        this.cacheRepository = cacheRepository;
        this.apiLayerProperties = apiLayerProperties;
    }

    public ExchangeResponseDTO getExchange (ExchangeRequestDTO filter) {
        if(filter.getCurrencyCode() == null){
            throw new OneboxRestException(ApiExternalErrorCode.CURRENCY_CODE_MANDATORY) ;
        }
        String key =CACHE_EXCHANGE+'_'+ filter.getCurrencyCode();
        ExchangeResponse response = cacheRepository.get(key,ExchangeResponse.class);
        if(response == null){
            response = getCachedExchange(filter);
            cacheRepository.set(CACHE_EXCHANGE,response,
                    apiLayerProperties.getVersionCacheTime(),TimeUnit.HOURS, new Object[]{filter.getCurrencyCode()});

        }
        return ExchangeConverter.toDTO(response);
    }

    public ExchangeResponse getCachedExchange (ExchangeRequestDTO filter) {
        String responseErrorInfo =  "Error getting exchange " + filter.getCurrencyCode()+": ";
        try{
            ExchangeResponse response = apiLayerRepository.getCurrencyExchange(filter.getCurrencyCode());
            if (BooleanUtils.isTrue(response.getSuccess())){
                return response;
            }
            responseErrorInfo = responseErrorInfo + response.getError().getInfo();
        }catch (Exception e){
            throw new OneboxRestException(ApiExternalErrorCode.PERSISTENCE_ERROR,e.getMessage(), e.getCause()) ;
        }
        throw new OneboxRestException(ApiExternalErrorCode.NOT_FOUND, responseErrorInfo, null) ;
    }
}
