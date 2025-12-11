package es.onebox.event.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.MsEntityDatasource;
import es.onebox.event.datasources.ms.entity.dto.Attribute;
import es.onebox.event.datasources.ms.entity.dto.AttributeScope;
import es.onebox.event.datasources.ms.entity.dto.CountryDTO;
import es.onebox.event.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.event.datasources.ms.entity.dto.CurrencyDTO;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypeSearchFilter;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.datasources.ms.entity.dto.EntityConfigDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.ExternalBarcodeEntityConfigDTO;
import es.onebox.event.datasources.ms.entity.dto.ExternalEntityConfig;
import es.onebox.event.datasources.ms.entity.dto.ExternalLoginConfig;
import es.onebox.event.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.event.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.event.datasources.ms.entity.dto.OperatorCurrenciesDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerInvoiceProvider;
import es.onebox.event.datasources.ms.entity.dto.request.ProducersRequest;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.exception.MsEventSessionErrorCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class EntitiesRepository {

    private final MsEntityDatasource msEntityDatasource;
    private final CacheRepository localCacheRepository;

    @Autowired
    public EntitiesRepository(MsEntityDatasource msEntityDatasource, CacheRepository localCacheRepository) {
        this.msEntityDatasource = msEntityDatasource;
        this.localCacheRepository = localCacheRepository;
    }

    @Cached(key = "entitiesRepository.entity")
    public EntityDTO getEntity(@CachedArg Integer entityId) {
        return msEntityDatasource.getEntity(entityId);
    }

    public EntityConfigDTO getEntityConfig(Integer entityId) {
        return msEntityDatasource.getEntityConfig(entityId);
    }

    @Cached(key = "entitiesRepository.producer")
    public ProducerDTO getProducer(@CachedArg Integer producerId) {
        return msEntityDatasource.getProducer(producerId);
    }

    public ProducerDTO getProducerRaw(@CachedArg Integer producerId) {
        ProducersRequest request = new ProducersRequest();
        request.setId(producerId);
        request.setIncludeDeleted(Boolean.TRUE);
        request.setLimit(1L);
        var producers = msEntityDatasource.getProducers(request);

        if (CollectionUtils.isNotEmpty(producers.getData())) {
            return producers.getData().get(0);
        }
        throw new OneboxRestException(MsEventSessionErrorCode.PRODUCER_NOT_FOUND);
    }

    public ProducerInvoiceProvider getProducerInvoiceProvider(Integer producerId) {
        return msEntityDatasource.getProducerInvoiceProvider(producerId);
    }

    public List<Attribute> getAttributes(Long entityId, AttributeScope attributeScope) {
        return msEntityDatasource.getAttributes(entityId, attributeScope);
    }

    @Cached(key = "ms-entitiesRepository.country", expires = 10 * 60)
    public CountryDTO getCountry(@CachedArg Integer countryId) {
        return msEntityDatasource.getCountry(countryId);
    }

    @Cached(key = "ms-entitiesRepository.countrySubdivision", expires = 10 * 60)
    public CountrySubdivisionDTO getCountrySubdivision(@CachedArg Integer countrySubdivisionId) {
        return msEntityDatasource.getCountrySubdivision(countrySubdivisionId);
    }

    @Cached(key = "ms-entitiesRepository.allLanguages", expires = 10 * 60)
    public Map<Long, String> getAllIdAndCodeLanguages() {
        return msEntityDatasource.getAllLanguages()
                .stream()
                .collect(Collectors.toMap(MasterdataValue::getId, MasterdataValue::getCode));
    }

    public InvoicePrefix getInvoicePrefix(Integer producerId, Integer invoicePrefixId) {
        return msEntityDatasource.getInvoicePrefix(producerId, invoicePrefixId);
    }

    @Cached(key = "ms-entitiesRepository.invoiceprefix", expires = 3 * 60)
    public InvoicePrefix getInvoicePrefix(@CachedArg Integer invoicePrefixId) {
        return msEntityDatasource.getInvoicePrefix(invoicePrefixId);
    }

    @Cached(key = "ms-entitiesRepository.externalBarcodeConfig", expires = 3 * 60)
    public ExternalBarcodeEntityConfigDTO getExternalEntityBarcodeConfig(@CachedArg Integer entityId) {
        return msEntityDatasource.getEntityExternalBarcodeConfig(entityId);
    }

    public ExternalEntityConfig getExternalEntityConfig(Integer entityId) {
        return msEntityDatasource.getExternalEntityConfig(entityId);
    }

    public OperatorCurrenciesDTO getOperatorCurrencies(Integer operatorId) {
        return msEntityDatasource.getOperatorCurrencies(operatorId);
    }

    @Cached(key = "ms-entitiesRepository.customerTypes", expires = 3 * 60)
    public CustomerTypes getCustomerTypes(@CachedArg Integer entityId, @CachedArg CustomerTypeSearchFilter filter) {
        return msEntityDatasource.getCustomerTypes(entityId, filter);
    }

    public List<CurrencyDTO> getCurrencies() {
        return localCacheRepository.cached("ms-entitiesRepository.currencies", 30, TimeUnit.MINUTES,
                                           msEntityDatasource::getCurrencies, new Object[]{});
    }

    @Cached(key = "ms-entitiesRepository.externalLogin", timeUnit =  TimeUnit.HOURS, expires = 1)
    public ExternalLoginConfig getExternalLoginConfig(@CachedArg Provider provider) {

        return msEntityDatasource.getExternalLoginConfig(provider);
    }

}
