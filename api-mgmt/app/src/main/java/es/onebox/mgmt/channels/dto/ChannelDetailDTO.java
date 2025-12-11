package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.enums.ChannelPortalBuild;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelDetailDTO extends ChannelDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("public")
    private Boolean channelPublic;
    @JsonProperty("whitelabel_type")
    private WhitelabelType whitelabelType;
    @JsonProperty("force_square_pictures")
    private Boolean forceSquarePictures;
    @JsonProperty("whitelabel_path")
    private String whitelabelPath;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("build")
    private ChannelPortalBuild build;
    @JsonProperty("languages")
    private ChannelLanguagesDTO languages;
    @JsonProperty("contact")
    private ChannelContactDTO contact;
    @JsonProperty("settings")
    private ChannelSettingsDTO settings;
    @JsonProperty("limits")
    private ChannelLimitsDTO limits;
    @JsonProperty("virtual_queue")
    private VirtualQueueConfigDTO virtualQueueConfig;
    @JsonProperty("currencies")
    private List<CodeNameDTO> currencies;

    public Boolean getChannelPublic() {
        return channelPublic;
    }

    public void setChannelPublic(Boolean aPublic) {
        channelPublic = aPublic;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
    }

    public Boolean getForceSquarePictures() {return forceSquarePictures;}

    public void setForceSquarePictures(Boolean forceSquarePictures) {this.forceSquarePictures = forceSquarePictures;}

    public String getWhitelabelPath() {
        return whitelabelPath;
    }

    public void setWhitelabelPath(String whitelabelPath) {
        this.whitelabelPath = whitelabelPath;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ChannelContactDTO getContact() {
        return contact;
    }

    public void setContact(ChannelContactDTO contact) {
        this.contact = contact;
    }

    public ChannelPortalBuild getBuild() {
        return build;
    }

    public void setBuild(ChannelPortalBuild build) {
        this.build = build;
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
    }

    public ChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(ChannelSettingsDTO settings) {
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

    public List<CodeNameDTO> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CodeNameDTO> currencies) {
        this.currencies = currencies;
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
