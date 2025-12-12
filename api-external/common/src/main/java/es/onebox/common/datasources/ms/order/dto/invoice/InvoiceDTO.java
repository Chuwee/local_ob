package es.onebox.common.datasources.ms.order.dto.invoice;

import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.order.dto.OrderDataDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InvoiceDTO implements Serializable {
    private static final long serialVersionUID = 2283245864242479613L;

    private OrderDataDTO order;
    private List<OrderProductDTO> products;
    private EntityDTO entity;
    private OrderInvoiceDTO orderInvoice;
    private List<InvoiceEventDataDTO> invoiceEventsData = new LinkedList();
    private List<InvoiceTaxDataDTO> invoiceTaxesData = new LinkedList();

    private Double totalPrice = null;

    public OrderDataDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDataDTO order) {
        this.order = order;
    }

    public List<OrderProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProductDTO> products) {
        this.products = products;
    }

    public EntityDTO getEntity() {
        return entity;
    }

    public void setEntity(EntityDTO entity) {
        this.entity = entity;
    }

    public OrderInvoiceDTO getOrderInvoice() {
        return orderInvoice;
    }

    public void setOrderInvoice(OrderInvoiceDTO orderInvoice) {
        this.orderInvoice = orderInvoice;
    }

    public List<InvoiceEventDataDTO> getInvoiceEventsData() {
        return invoiceEventsData;
    }

    public void setInvoiceEventsData(List<InvoiceEventDataDTO> invoiceEventsData) {
        this.invoiceEventsData = invoiceEventsData;
    }

    public List<InvoiceTaxDataDTO> getInvoiceTaxesData() {
        return invoiceTaxesData;
    }

    public void setInvoiceTaxesData(List<InvoiceTaxDataDTO> invoiceTaxesData) {
        this.invoiceTaxesData = invoiceTaxesData;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
