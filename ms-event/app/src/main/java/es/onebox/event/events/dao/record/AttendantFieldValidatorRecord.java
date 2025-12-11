package es.onebox.event.events.dao.record;

import java.io.Serial;
import java.io.Serializable;

public class AttendantFieldValidatorRecord implements Serializable {


    @Serial
    private static final long serialVersionUID = 3969870629981238859L;

    private Integer eventFieldId;
    private String validationType;
    private String regExp;
    private String javaClass;

    public AttendantFieldValidatorRecord(Integer eventFieldId, String validationType, String regExp, String javaClass) {
        this.eventFieldId = eventFieldId;
        this.validationType = validationType;
        this.regExp = regExp;
        this.javaClass = javaClass;
    }

    public Integer getEventFieldId() {
        return eventFieldId;
    }

    public void setEventFieldId(Integer eventFieldId) {
        this.eventFieldId = eventFieldId;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public String getRegExp() {
        return regExp;
    }

    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }
}
