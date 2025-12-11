package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SubscriptionMode implements Serializable  {
    private String sid;
    private String name;
    private List<Long> capacities;
    private List<Long> allowedPeriodicities;
    private List<Long> allowedRoles;
    private Long defaultBuyPeriodicity;
    private Long defaultBuyRoleId;
    private Map<String, Translations> translations;
    private Boolean active;

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

    public List<Long> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<Long> capacities) {
        this.capacities = capacities;
    }

    public List<Long> getAllowedPeriodicities() {
        return allowedPeriodicities;
    }

    public void setAllowedPeriodicities(List<Long> allowedPeriodicities) {
        this.allowedPeriodicities = allowedPeriodicities;
    }

    public Long getDefaultBuyPeriodicity() {
        return defaultBuyPeriodicity;
    }

    public void setDefaultBuyPeriodicity(Long defaultBuyPeriodicity) {
        this.defaultBuyPeriodicity = defaultBuyPeriodicity;
    }

    public List<Long> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<Long> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public Long getDefaultBuyRoleId() {
        return defaultBuyRoleId;
    }

    public void setDefaultBuyRoleId(Long defaultBuyRoleId) {
        this.defaultBuyRoleId = defaultBuyRoleId;
    }

    public Map<String, Translations> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, Translations> translations) {
        this.translations = translations;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
