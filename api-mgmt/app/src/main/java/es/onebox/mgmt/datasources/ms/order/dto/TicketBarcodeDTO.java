package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.order.enums.BarcodeValidationStatus;
import es.onebox.mgmt.datasources.ms.order.enums.ProductBarcodeTicketType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class TicketBarcodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String barcode;
    private ProductBarcodeAdmissionDTO admission;
    private ProductBarcodeSessionDTO session;
    private IdNameDTO event;
    private IdNameDTO venue;
    private BarcodeValidationStatus status;
    private ZonedDateTime updatedDate;
    private OrderCodeDTO order;
    private ProductBarcodeProductDTO product;
    private ProductBarcodeSeatDTO seat;
    private List<IdNameRestrictiveDTO> promotions;
    private List<ProductBarcodeUserValidationDTO> validations;
    private IdNameRestrictiveDTO rate;
    private IdNameRestrictiveDTO priceZone;
    private ProductBarcodeTicketType ticketType;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ProductBarcodeAdmissionDTO getAdmission() {
        return admission;
    }

    public void setAdmission(ProductBarcodeAdmissionDTO admission) {
        this.admission = admission;
    }

    public ProductBarcodeSessionDTO getSession() {
        return session;
    }

    public void setSession(ProductBarcodeSessionDTO session) {
        this.session = session;
    }

    public IdNameDTO getEvent() {
        return event;
    }

    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public IdNameDTO getVenue() {
        return venue;
    }

    public void setVenue(IdNameDTO venue) {
        this.venue = venue;
    }

    public BarcodeValidationStatus getStatus() {
        return status;
    }

    public void setStatus(BarcodeValidationStatus status) {
        this.status = status;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public OrderCodeDTO getOrder() {
        return order;
    }

    public void setOrder(OrderCodeDTO order) {
        this.order = order;
    }

    public ProductBarcodeProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductBarcodeProductDTO product) {
        this.product = product;
    }

    public ProductBarcodeSeatDTO getSeat() {
        return seat;
    }

    public void setSeat(ProductBarcodeSeatDTO seat) {
        this.seat = seat;
    }

    public List<IdNameRestrictiveDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<IdNameRestrictiveDTO> promotions) {
        this.promotions = promotions;
    }

    public List<ProductBarcodeUserValidationDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<ProductBarcodeUserValidationDTO> validations) {
        this.validations = validations;
    }

    public IdNameRestrictiveDTO getRate() {
        return rate;
    }

    public void setRate(IdNameRestrictiveDTO rate) {
        this.rate = rate;
    }

    public IdNameRestrictiveDTO getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(IdNameRestrictiveDTO priceZone) {
        this.priceZone = priceZone;
    }

    public ProductBarcodeTicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(ProductBarcodeTicketType ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
