package es.onebox.mgmt.channels.members.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.dto.TranslationsDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class MemberRoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7419674475604343892L;
    @JsonProperty("id")
    private Long roleId;

    @JsonProperty("name")
    private String role;

    private Map<String, TranslationsDTO> translations;

    public MemberRoleDTO() {
    }

    public MemberRoleDTO(Long roleId, String role) {
        this.roleId = roleId;
        this.role = role;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, TranslationsDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, TranslationsDTO> translations) {
        this.translations = translations;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
