package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.ChannelPortalBuild;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateChannelRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4586272251932135954L;

    @Size(max = 200, message = "name should be 200 characters or less")
    private String name;
    @Size(max = 200, message = "domain should be 200 characters or less")
    private String domain;
    @JsonProperty("public")
    private Boolean channelPublic;
    private ChannelStatus status;
    private ChannelLanguagesDTO languages;
    @JsonProperty("currency_codes")
    private List<String> currencies;
    private ChannelPortalBuild build;
    @Valid
    private ChannelContactDTO contact;
    @Valid
    private ChannelSettingsUpdateDTO settings;
    @Valid
    private ChannelLimitsDTO limits;
    @JsonProperty("virtual_queue")
    private VirtualQueueConfigDTO virtualQueueConfig;
    @JsonProperty("whitelabel_path")
    private String whitelabelPath;
    @JsonProperty("force_square_pictures")
    private Boolean forceSquarePictures;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChannelPublic() {
        return channelPublic;
    }

    public void setChannelPublic(Boolean channelPublic) {
        this.channelPublic = channelPublic;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
    }

    public ChannelPortalBuild getBuild() {
        return build;
    }

    public void setBuild(ChannelPortalBuild build) {
        this.build = build;
    }

    public ChannelContactDTO getContact() {
        return contact;
    }

    public void setContact(ChannelContactDTO contact) {
        this.contact = contact;
    }

    public ChannelSettingsUpdateDTO getSettings() {
        return settings;
    }

    public void setSettings(ChannelSettingsUpdateDTO settings) {
        this.settings = settings;
    }

    public ChannelLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(ChannelLimitsDTO limits) {
        this.limits = limits;
    }

    public VirtualQueueConfigDTO getVirtualQueueConfig() {
        return virtualQueueConfig;
    }

    public void setVirtualQueueConfig(VirtualQueueConfigDTO virtualQueueConfig) {
        this.virtualQueueConfig = virtualQueueConfig;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    public String getWhitelabelPath() {
        return whitelabelPath;
    }

    public void setWhitelabelPath(String whitelabelPath) {
        this.whitelabelPath = whitelabelPath;
    }

    public Boolean getForceSquarePictures() {
        return forceSquarePictures;
    }

    public void setForceSquarePictures(Boolean forceSquarePictures) {
        this.forceSquarePictures = forceSquarePictures;
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
