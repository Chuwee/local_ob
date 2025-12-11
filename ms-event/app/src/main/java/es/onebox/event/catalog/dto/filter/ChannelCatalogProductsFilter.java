package es.onebox.event.catalog.dto.filter;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.event.common.Pageable;
import es.onebox.event.events.enums.CatalogSortableField;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.event.products.enums.ProductPublicationType;
import es.onebox.event.products.enums.ProductType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class ChannelCatalogProductsFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 3529367316561955501L;

    private List<Long> id;
    private List<Long> sessionId;
    private List<Long> eventId;
    private ProductType productType;
    private ProductPublicationType productPublicationType;
    private Long currencyId;
    private List<ProductDeliveryType> deliveryType;

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public List<Long> getSessionId() {
        return sessionId;
    }

    public void setSessionId(List<Long> sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductPublicationType getProductPublicationType() {
        return productPublicationType;
    }

    public void setProductPublicationType(ProductPublicationType productPublicationType) {
        this.productPublicationType = productPublicationType;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public List<ProductDeliveryType> getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(List<ProductDeliveryType> deliveryType) {
        this.deliveryType = deliveryType;
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
