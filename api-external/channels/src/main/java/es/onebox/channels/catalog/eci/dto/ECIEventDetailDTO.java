package es.onebox.channels.catalog.eci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.onebox.channels.catalog.eci.ZonedDateTimeSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ECIEventDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<ECIThemeDTO> themes;
    private List<String> participants;
    private ECIOrganizerDTO organizer;
    private List<ECIVenueDTO> venues;
    private Map<String, String> description;
    private Map<String, String> image;
    @JsonProperty("multilingual_name")
    private Map<String, String> multilingualName;
    private List<String> deliveryMethods;
    private String deliveryInfo;
    private String duration;
    private String ages;
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime schedule;
    private String lastMinuteRate;

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

    public List<ECIThemeDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<ECIThemeDTO> themes) {
        this.themes = themes;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public ECIOrganizerDTO getOrganizer() {
        return organizer;
    }

    public void setOrganizer(ECIOrganizerDTO organizer) {
        this.organizer = organizer;
    }

    public List<ECIVenueDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<ECIVenueDTO> venues) {
        this.venues = venues;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getImage() {
        return image;
    }

    public void setImage(Map<String, String> image) {
        this.image = image;
    }

    public Map<String, String> getMultilingualName() {
        return multilingualName;
    }

    public void setMultilingualName(Map<String, String> multilingualName) {
        this.multilingualName = multilingualName;
    }

    public String getAges() {
        return ages;
    }

    public void setAges(String ages) {
        this.ages = ages;
    }

    public List<String> getDeliveryMethods() {
        return deliveryMethods;
    }

    public void setDeliveryMethods(List<String> deliveryMethods) {
        this.deliveryMethods = deliveryMethods;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public ZonedDateTime getSchedule() {
        return schedule;
    }

    public void setSchedule(ZonedDateTime schedule) {
        this.schedule = schedule;
    }

    public String getLastMinuteRate() {
        return lastMinuteRate;
    }

    public void setLastMinuteRate(String lastMinuteRate) {
        this.lastMinuteRate = lastMinuteRate;
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
