package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class AttendantFieldRecord extends CpanelEventFieldRecord {

    private String sid;
    private String fieldType;
    private List<AttendantFieldValidatorRecord> validators;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public List<AttendantFieldValidatorRecord> getValidators() {
        return validators;
    }

    public void setValidators(List<AttendantFieldValidatorRecord> validators) {
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
