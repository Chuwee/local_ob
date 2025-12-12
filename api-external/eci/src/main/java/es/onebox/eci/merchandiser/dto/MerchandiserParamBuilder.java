package es.onebox.eci.merchandiser.dto;

public class MerchandiserParamBuilder {
    MerchandiserParam merchandiserParam;

    public MerchandiserParamBuilder() {
        merchandiserParam = new MerchandiserParam();
    }

    public MerchandiserParam build(){
        return merchandiserParam;
    }

    public MerchandiserParamBuilder id(Long value) {
        merchandiserParam.setId(value);
        return this;
    }

    public MerchandiserParamBuilder eventId(Long value) {
        merchandiserParam.setEventId(value);
        return this;
    }

    public MerchandiserParamBuilder venueId(Long value) {
        merchandiserParam.setVenueId(value);
        return this;
    }
}
