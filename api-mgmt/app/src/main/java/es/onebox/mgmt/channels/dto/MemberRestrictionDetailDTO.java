package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.RestrictionType;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MemberRestrictionDetailDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = -8285367626953251458L;

    private String sid;
    @JsonProperty(value = "restriction_name")
    private String restrictionName;
    @JsonProperty("restriction_type")
    private RestrictionType restrictionType;
    private Boolean activated;
    private Map<String, String> translations;
    @JsonProperty(value = "venue_template_sectors")
    private List<Long> venueTemplateSectors;
    @JsonProperty(value = "member_periods")
    private List<MemberPeriodType> memberPeriods;
    private Map<String, Object> fields;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getRestrictionName() {
        return restrictionName;
    }

    public void setRestrictionName(String restrictionName) {
        this.restrictionName = restrictionName;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public List<Long> getVenueTemplateSectors() {
        return venueTemplateSectors;
    }

    public void setVenueTemplateSectors(List<Long> venueTemplateSectors) {
        this.venueTemplateSectors = venueTemplateSectors;
    }

    public List<MemberPeriodType> getMemberPeriods() {
        return memberPeriods;
    }

    public void setMemberPeriods(List<MemberPeriodType> memberPeriods) {
        this.memberPeriods = memberPeriods;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
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
