package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.distribution.dto.ItemPack;
import es.onebox.common.datasources.distribution.dto.ItemWarning;
import es.onebox.common.datasources.distribution.dto.attendee.Barcode;
import es.onebox.common.datasources.distribution.dto.order.items.b2b.B2BPolicy;
import es.onebox.common.datasources.distribution.dto.order.items.promotion.ItemPromotion;
import es.onebox.common.datasources.distribution.dto.order.price.Price;
import es.onebox.common.datasources.distribution.dto.state.ItemState;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


public class OrderItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248696302212177674L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("barcode")
    private Barcode barcode;
    @JsonProperty("type")
    private ItemType type;
    @JsonProperty("state")
    private ItemState state;
    @JsonProperty("allocation")
    private ItemSeatAllocation allocation;
    @JsonProperty("previous_allocation")
    private ItemSeatAllocation previousAllocation;
    @JsonProperty("rate")
    private ItemRate rate;
    @JsonProperty("attendee")
    private List<ItemAttendee> itemAttendee;
    @JsonProperty("b2b")
    private B2BPolicy b2BPolicy;
    @JsonProperty("external_data")
    private ItemExternalData externalData;
    @JsonProperty("promotions")
    private List<ItemPromotion> promotions;
    @JsonProperty("price")
    private Price price;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("reallocations")
    private List<Reallocation> reallocations;
    @JsonProperty("related_reallocation_code")
    private String relatedReallocationCode;
    @JsonProperty("product_data")
    private ProductData productData;
    @JsonProperty("pack")
    private ItemPack pack;
    @JsonProperty("item_warnings")
    protected List<ItemWarning> itemWarnings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ItemSeatAllocation getAllocation() {
        return allocation;
    }

    public void setAllocation(ItemSeatAllocation allocation) {
        this.allocation = allocation;
    }

    public ItemRate getRate() {
        return rate;
    }

    public void setRate(ItemRate rate) {
        this.rate = rate;
    }

    public List<ItemAttendee> getItemAttendee() {
        return itemAttendee;
    }

    public void setItemAttendee(List<ItemAttendee> itemAttendee) {
        this.itemAttendee = itemAttendee;
    }

    public B2BPolicy getB2BPolicy() {
        return b2BPolicy;
    }

    public void setB2BPolicy(B2BPolicy b2BPolicy) {
        this.b2BPolicy = b2BPolicy;
    }

    public ItemExternalData getExternalData() {
        return externalData;
    }

    public void setExternalData(ItemExternalData externalData) {
        this.externalData = externalData;
    }

    public List<ItemPromotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<ItemPromotion> promotions) {
        this.promotions = promotions;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ItemState getState() {
        return state;
    }

    public void setState(ItemState state) {
        this.state = state;
    }

    public List<Reallocation> getReallocations() {
        return reallocations;
    }

    public void setReallocations(List<Reallocation> reallocations) {
        this.reallocations = reallocations;
    }

    public ItemSeatAllocation getPreviousAllocation() {
        return previousAllocation;
    }

    public void setPreviousAllocation(ItemSeatAllocation previousAllocation) {
        this.previousAllocation = previousAllocation;
    }

    public String getRelatedReallocationCode() {
        return relatedReallocationCode;
    }

    public void setRelatedReallocationCode(String relatedReallocationCode) {
        this.relatedReallocationCode = relatedReallocationCode;
    }

    public ProductData getProductData() {
        return productData;
    }

    public void setProductData(ProductData productData) {
        this.productData = productData;
    }

    public ItemPack getPack() {
        return pack;
    }

    public void setPack(ItemPack pack) {
        this.pack = pack;
    }

    public List<ItemWarning> getItemWarnings() {
        return itemWarnings;
    }

    public void setItemWarnings(List<ItemWarning> itemWarnings) {
        this.itemWarnings = itemWarnings;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
