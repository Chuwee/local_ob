package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SubscriptionModeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sid;
    private String name;
    private List<Long> capacities;
    private List<Long> periodicities;
    private List<Long> roles;
    @JsonProperty("default_buy_periodicity")
    private Long defaultBuyPeriodicity;
    @JsonProperty("default_buy_role_id")
    private Long defaultBuyRoleId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, TranslationsDTO> translations;
    private Boolean active;

    public SubscriptionModeDTO() {
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

    public List<Long> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<Long> capacities) {
        this.capacities = capacities;
    }

    public List<Long> getPeriodicities() {
        return periodicities;
    }

    public void setPeriodicities(List<Long> periodicities) {
        this.periodicities = periodicities;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }

    public Long getDefaultBuyPeriodicity() {
        return defaultBuyPeriodicity;
    }

    public void setDefaultBuyPeriodicity(Long defaultBuyPeriodicity) {
        this.defaultBuyPeriodicity = defaultBuyPeriodicity;
    }

    public Long getDefaultBuyRoleId() {
        return defaultBuyRoleId;
    }

    public void setDefaultBuyRoleId(Long defaultBuyRoleId) {
        this.defaultBuyRoleId = defaultBuyRoleId;
    }

    public Map<String, TranslationsDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, TranslationsDTO> translations) {
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
