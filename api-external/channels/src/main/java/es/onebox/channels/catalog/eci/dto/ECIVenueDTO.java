package es.onebox.channels.catalog.eci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.onebox.channels.catalog.eci.ZonedDateTimeSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ECIVenueDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String phone;
    private String fax;
    private String url;
    @JsonProperty("min_date")
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime minDate;
    @JsonProperty("max_date")
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime maxDate;
    @JsonProperty("start_sale_date")
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime startSaleDate;
    @JsonProperty("end_sale_date")
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime endSaleDate;
    @JsonProperty("start_price")
    private BigDecimal startPrice;
    @JsonProperty("start_price_base")
    private BigDecimal startPriceBase;
    @JsonProperty("default_start_price")
    private BigDecimal defaultStartPrice;
    @JsonProperty("default_start_price_base")
    private BigDecimal defaultStartPriceBase;
    private ECIAddressDTO location;
    private Map<String, String> image;
    @JsonProperty("venue_image")
    private String venueImage;
    private List<ECISessionYearDTO> sessions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ZonedDateTime getMinDate() {
        return minDate;
    }

    public void setMinDate(ZonedDateTime minDate) {
        this.minDate = minDate;
    }

    public ZonedDateTime getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(ZonedDateTime maxDate) {
        this.maxDate = maxDate;
    }

    public ZonedDateTime getStartSaleDate() {
        return startSaleDate;
    }

    public void setStartSaleDate(ZonedDateTime startSaleDate) {
        this.startSaleDate = startSaleDate;
    }

    public ZonedDateTime getEndSaleDate() {
        return endSaleDate;
    }

    public void setEndSaleDate(ZonedDateTime endSaleDate) {
        this.endSaleDate = endSaleDate;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public BigDecimal getStartPriceBase() {
        return startPriceBase;
    }

    public void setStartPriceBase(BigDecimal startPriceBase) {
        this.startPriceBase = startPriceBase;
    }

    public BigDecimal getDefaultStartPrice() {
        return defaultStartPrice;
    }

    public void setDefaultStartPrice(BigDecimal defaultStartPrice) {
        this.defaultStartPrice = defaultStartPrice;
    }

    public BigDecimal getDefaultStartPriceBase() {
        return defaultStartPriceBase;
    }

    public void setDefaultStartPriceBase(BigDecimal defaultStartPriceBase) {
        this.defaultStartPriceBase = defaultStartPriceBase;
    }

    public ECIAddressDTO getLocation() {
        return location;
    }

    public void setLocation(ECIAddressDTO location) {
        this.location = location;
    }

    public Map<String, String> getImage() {
        return image;
    }

    public void setImage(Map<String, String> image) {
        this.image = image;
    }

    public String getVenueImage() {
        return venueImage;
    }

    public void setVenueImage(String venueImage) {
        this.venueImage = venueImage;
    }

    public List<ECISessionYearDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<ECISessionYearDTO> sessions) {
        this.sessions = sessions;
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
