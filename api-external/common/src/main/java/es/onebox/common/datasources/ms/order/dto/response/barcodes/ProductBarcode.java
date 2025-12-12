package es.onebox.common.datasources.ms.order.dto.response.barcodes;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serializable;

public class ProductBarcode implements Serializable {

    private String barcode;
    private BarcodeValidationStatus status;
    private ProductBarcodeSeat seat;
    private IdDTO product;
    private IdNameDTO session;
    private BarcodeOrder order;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BarcodeValidationStatus getStatus() {
        return status;
    }

    public void setStatus(BarcodeValidationStatus status) {
        this.status = status;
    }

    public ProductBarcodeSeat getSeat() {
        return seat;
    }

    public IdDTO getProduct() {
        return product;
    }

    public void setProduct(IdDTO product) {
        this.product = product;
    }

    public void setSeat(ProductBarcodeSeat seat) {
        this.seat = seat;
    }

    public IdNameDTO getSession() {
        return session;
    }

    public void setSession(IdNameDTO session) {
        this.session = session;
    }

    public BarcodeOrder getOrder() {
        return order;
    }

    public void setOrder(BarcodeOrder order) {
        this.order = order;
    }
}
