package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.channels.enums.RestrictionType;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MemberRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 7169908241167898054L;
    private String sid;
    private String name;
    private RestrictionType restrictionType;
    private Map<String, String> translations;
    private Long venueTemplateId;
    private Boolean activated;
    private List<Long> venueTemplateSectors;
    private List<MemberPeriodType> memberPeriods;
    private MemberRestrictionFields fields;

    public MemberRestriction() {
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Long getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Long venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
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

    public MemberRestrictionFields getFields() {
        return fields;
    }

    public void setFields(MemberRestrictionFields fields) {
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
