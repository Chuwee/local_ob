package es.onebox.event.events.dao.record;

public class PriceRecord {

    private Integer venueConfigId;
    private String venueConfigName;
    private Integer priceZoneId;
    private String priceZoneCode;
    private String priceZoneDescription;
    private Integer rateId;
    private String rateName;
    private Double price;

    public Integer getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Integer venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public String getVenueConfigName() {
        return venueConfigName;
    }

    public void setVenueConfigName(String venueConfigName) {
        this.venueConfigName = venueConfigName;
    }

    public Integer getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Integer priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public String getPriceZoneCode() {
        return priceZoneCode;
    }

    public void setPriceZoneCode(String priceZoneCode) {
        this.priceZoneCode = priceZoneCode;
    }

    public String getPriceZoneDescription() {
        return priceZoneDescription;
    }

    public void setPriceZoneDescription(String priceZoneDescription) {
        this.priceZoneDescription = priceZoneDescription;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
