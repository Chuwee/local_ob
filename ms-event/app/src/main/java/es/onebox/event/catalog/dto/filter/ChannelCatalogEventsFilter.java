package es.onebox.event.catalog.dto.filter;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.events.enums.CatalogSortableField;
import es.onebox.event.events.enums.EventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class ChannelCatalogEventsFilter extends ChannelCatalogFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<FilterWithOperator<Double>> price;
    private List<FilterWithOperator<Double>> promotedPrice;
    private List<String> country;
    private List<String> countrySubdivision;
    private List<String> city;
    private List<Long> venueId;
    private List<EventType> eventType;
    private List<FilterWithOperator<ZonedDateTime>> sessionsStartDate;
    private List<Long> attributesValueId;
    private String q;
    private SortOperator<CatalogSortableField> sort;
    private String categoryCode;
    private String customCategoryCode;
    private List<Long> eventId;

    public List<FilterWithOperator<Double>> getPrice() {
        return price;
    }

    public void setPrice(List<FilterWithOperator<Double>> price) {
        this.price = price;
    }

    public List<FilterWithOperator<Double>> getPromotedPrice() {
        return promotedPrice;
    }

    public void setPromotedPrice(List<FilterWithOperator<Double>> promotedPrice) {
        this.promotedPrice = promotedPrice;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public List<String> getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(List<String> countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(List<String> city) {
        this.city = city;
    }

    public List<Long> getVenueId() {
        return venueId;
    }

    public void setVenueId(List<Long> venueId) {
        this.venueId = venueId;
    }

    public List<Long> getAttributesValueId() {
        return attributesValueId;
    }

    public void setAttributesValueId(List<Long> attributesValueId) {
        this.attributesValueId = attributesValueId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<CatalogSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<CatalogSortableField> sort) {
        this.sort = sort;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCustomCategoryCode() {
        return customCategoryCode;
    }

    public void setCustomCategoryCode(String customCategoryCode) {
        this.customCategoryCode = customCategoryCode;
    }

    public List<FilterWithOperator<ZonedDateTime>> getSessionsStartDate() {
        return sessionsStartDate;
    }

    public void setSessionsStartDate(List<FilterWithOperator<ZonedDateTime>> sessionsStartDate) {
        this.sessionsStartDate = sessionsStartDate;
    }

    public List<EventType> getEventType() {
        return eventType;
    }

    public void setEventType(List<EventType> eventType) {
        this.eventType = eventType;
    }

    public List<Long> getEventId() { return eventId; }

    public void setEventId(List<Long> eventId) { this.eventId = eventId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
