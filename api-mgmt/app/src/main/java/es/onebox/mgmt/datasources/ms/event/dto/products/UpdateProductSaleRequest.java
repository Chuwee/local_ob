package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;

import java.io.Serial;
import java.io.Serializable;

public class UpdateProductSaleRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8628372548319297105L;

    private ProductSaleRequestsStatus status;
    private Integer userId;

    public ProductSaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(ProductSaleRequestsStatus status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

