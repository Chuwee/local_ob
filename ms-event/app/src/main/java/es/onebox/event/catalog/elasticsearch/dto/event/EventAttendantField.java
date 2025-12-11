package es.onebox.event.catalog.elasticsearch.dto.event;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventAttendantField implements Serializable {

    @Serial
    private static final long serialVersionUID = 5882846136738691378L;

    private Integer eventFieldId;
    private Integer fieldId;
    private String key;
    private String type;
    private Integer order;
    private Boolean mandatory;
    private Integer maxLength;
    private Integer minLength;
    private List<EventAttendantFieldValidator> validators;

    public Integer getEventFieldId() {
        return eventFieldId;
    }

    public void setEventFieldId(Integer eventFieldId) {
        this.eventFieldId = eventFieldId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public List<EventAttendantFieldValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<EventAttendantFieldValidator> validators) {
        this.validators = validators;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
