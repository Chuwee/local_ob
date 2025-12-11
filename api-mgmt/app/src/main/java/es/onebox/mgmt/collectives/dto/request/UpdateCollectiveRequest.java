package es.onebox.mgmt.collectives.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.collectives.dto.CipherPolicy;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class UpdateCollectiveRequest implements Serializable {
    private static final long serialVersionUID = 9136166899896374425L;

    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Size(max = 250)
    private String description;

    @JsonProperty("user_max_length")
    private Long userMaxLength;

    @JsonProperty("cipher_policy")
    private CipherPolicy  cipherPolicy;

    @JsonProperty("show_usages")
    private Boolean showUsages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserMaxLength() {
        return userMaxLength;
    }

    public void setUserMaxLength(Long userMaxLength) {
        this.userMaxLength = userMaxLength;
    }

    public CipherPolicy getCipherPolicy() {
        return cipherPolicy;
    }

    public void setCipherPolicy(CipherPolicy cipherPolicy) {
        this.cipherPolicy = cipherPolicy;
    }

    public Boolean getShowUsages() {
        return showUsages;
    }

    public void setShowUsages(Boolean showUsages) {
        this.showUsages = showUsages;
    }
}