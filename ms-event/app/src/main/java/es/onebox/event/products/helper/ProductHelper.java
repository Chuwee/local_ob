package es.onebox.event.products.helper;

import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductHelper {

    private final OrdersRepository ordersRepository;

    @Autowired
    public ProductHelper(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public boolean checkProductOrVariantSales(List<Long> variantIds) {
        if (CollectionUtils.isEmpty(variantIds)) {
            return false;
        }
        ProductSearchResponse productSearchResponse = ordersRepository.getPurchasedProducts(variantIds);
        return (productSearchResponse != null && productSearchResponse.getData() != null && !productSearchResponse.getData().isEmpty());
    }
}
