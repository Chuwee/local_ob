package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.EventStatus;
import es.onebox.common.datasources.ms.event.enums.EventType;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EventDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6802533904232302851L;

    private Long id;
    private String name;
    private String promoterReference;
    private IdNameDTO producer;
    private EventStatus status;
    private EventType type;
    private Long entityId;
    private String entityName;
    private DatesDTO date;
    private Long externalId;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private Integer salesGoalTickets;
    private Double salesGoalRevenue;
    private CategoryDTO category;
    private CategoryDTO customCategory;
    private Map<Integer, List<Integer>> eventAttributes;
    private String customSelectTemplate;
    private EventTicketTemplates ticketTemplates;
    private List<EventLanguageDTO> languages;
    private List<VenueDTO> venues;
    private Boolean supraEvent;
    private String externalReference;

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

    public String getPromoterReference() {
        return promoterReference;
    }

    public void setPromoterReference(String promoterReference) {
        this.promoterReference = promoterReference;
    }

    public IdNameDTO getProducer() {
        return producer;
    }

    public void setProducer(IdNameDTO producer) {
        this.producer = producer;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public DatesDTO getDate() {
        return date;
    }

    public void setDate(DatesDTO date) {
        this.date = date;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonSurname() {
        return contactPersonSurname;
    }

    public void setContactPersonSurname(String contactPersonSurname) {
        this.contactPersonSurname = contactPersonSurname;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Integer getSalesGoalTickets() {
        return salesGoalTickets;
    }

    public void setSalesGoalTickets(Integer salesGoalTickets) {
        this.salesGoalTickets = salesGoalTickets;
    }

    public Double getSalesGoalRevenue() {
        return salesGoalRevenue;
    }

    public void setSalesGoalRevenue(Double salesGoalRevenue) {
        this.salesGoalRevenue = salesGoalRevenue;
    }

    public Map<Integer, List<Integer>> getEventAttributes() {
        return eventAttributes;
    }

    public void setEventAttributes(Map<Integer, List<Integer>> eventAttributes) {
        this.eventAttributes = eventAttributes;
    }

    public String getCustomSelectTemplate() {
        return customSelectTemplate;
    }

    public void setCustomSelectTemplate(String customSelectTemplate) {
        this.customSelectTemplate = customSelectTemplate;
    }

    public EventTicketTemplates getTicketTemplates() {
        return ticketTemplates;
    }

    public void setTicketTemplates(EventTicketTemplates ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }

    public List<EventLanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguageDTO> languages) {
        this.languages = languages;
    }

    public List<VenueDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueDTO> venues) {
        this.venues = venues;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public CategoryDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(CategoryDTO customCategory) {
        this.customCategory = customCategory;
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
