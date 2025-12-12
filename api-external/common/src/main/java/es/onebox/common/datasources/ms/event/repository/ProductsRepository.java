package es.onebox.common.datasources.ms.event.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.event.MsEventDatasource;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public ProductsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    @Cached(key = "ProductsRepository_getProductVariant")
    public ProductVariant getProductVariant(@CachedArg Long productId, @CachedArg Long variantId) {
        return msEventDatasource.getProductVariant(productId, variantId);
    }

}
