package es.onebox.event.events.dto;

import es.onebox.event.events.enums.EventFieldType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AttendantFieldDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1563437199688487467L;

    private Integer id;
    private String key;
    private Integer minLength;
    private Integer maxLength;
    private Boolean mandatory;
    private Integer eventId;
    private Integer fieldId;
    private EventFieldType type;
    private Byte order;
    private List<AttendantFieldValidatorDTO> validators;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public void setOrder(Byte order) {
        this.order = order;
    }

    public Byte getOrder() {
        return order;
    }

    public EventFieldType getType() {
        return type;
    }

    public void setType(EventFieldType type) {
        this.type = type;
    }

    public List<AttendantFieldValidatorDTO> getValidators() {
        return validators;
    }

    public void setValidators(List<AttendantFieldValidatorDTO> validators) {
        this.validators = validators;
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
