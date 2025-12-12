package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.EventType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class MsEventSaleRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5868869545767371747L;
    private Long id;
    private String name;
    private EventType eventType;
    private ZonedDateTime startDate;
    private String personContactName;
    private String personContactSurname;
    private String personContactEmail;
    private String personContactPhone;
    private MsEntitySaleRequestDTO entity;
    private List<MsVenueSaleRequestDTO> venues;
    private Long currencyId;

    public MsEventSaleRequestDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getPersonContactName() {
        return personContactName;
    }

    public void setPersonContactName(String personContactName) {
        this.personContactName = personContactName;
    }

    public String getPersonContactSurname() {
        return personContactSurname;
    }

    public void setPersonContactSurname(String personContactSurname) {
        this.personContactSurname = personContactSurname;
    }

    public String getPersonContactEmail() {
        return personContactEmail;
    }

    public void setPersonContactEmail(String personContactEmail) {
        this.personContactEmail = personContactEmail;
    }

    public String getPersonContactPhone() {
        return personContactPhone;
    }

    public void setPersonContactPhone(String personContactPhone) {
        this.personContactPhone = personContactPhone;
    }

    public MsEntitySaleRequestDTO getEntity() {
        return entity;
    }

    public void setEntity(MsEntitySaleRequestDTO entity) {
        this.entity = entity;
    }

    public List<MsVenueSaleRequestDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<MsVenueSaleRequestDTO> venues) {
        this.venues = venues;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
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
