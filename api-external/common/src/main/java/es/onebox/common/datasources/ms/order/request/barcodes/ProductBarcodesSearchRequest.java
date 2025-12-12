package es.onebox.common.datasources.ms.order.request.barcodes;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductBarcodesSearchRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -6564127390910043520L;

    private List<Integer> sessionId;
    private List<Long> productId;
    private List<String> orderCode;
    private String barcodeOrderProvider;
    private Long limit;
    private Long offset;


    public List<Integer> getSessionId() {
        return sessionId;
    }

    public void setSessionId(List<Integer> sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getProductId() {
        return productId;
    }

    public void setProductId(List<Long> productId) {
        this.productId = productId;
    }

    public List<String> getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(List<String> orderCode) {
        this.orderCode = orderCode;
    }

    public String getBarcodeOrderProvider() {
        return barcodeOrderProvider;
    }

    public void setBarcodeOrderProvider(String barcodeOrderProvider) {
        this.barcodeOrderProvider = barcodeOrderProvider;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
