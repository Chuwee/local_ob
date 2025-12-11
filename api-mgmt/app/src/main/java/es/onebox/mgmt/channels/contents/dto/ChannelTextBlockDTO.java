package es.onebox.mgmt.channels.contents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.contents.enums.ChannelBlockType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelTextBlockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ChannelBlockType type;
    private String subject;
    private String value;
    private String language;
    private Boolean audited;
    @JsonProperty("use_free_text")
    private Boolean useFreeText;
    @JsonProperty("profiled_content")
    private List<ChannelProfiledTextBlockDTO> profiledTextBlocks;
    private List<ChannelTextBlockLabelDTO> labels;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setType(ChannelBlockType type) {
        this.type = type;
    }

    public ChannelBlockType getType() {
        return type;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getAudited() {
        return audited;
    }

    public void setAudited(Boolean audited) {
        this.audited = audited;
    }

    public Boolean getUseFreeText() {
        return useFreeText;
    }

    public void setUseFreeText(Boolean useFreeText) {
        this.useFreeText = useFreeText;
    }

    public void setProfiledTextBlocks(List<ChannelProfiledTextBlockDTO> profiledTextBlocks) {
        this.profiledTextBlocks = profiledTextBlocks;
    }

    public List<ChannelProfiledTextBlockDTO> getProfiledTextBlocks() {
        return profiledTextBlocks;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public List<ChannelTextBlockLabelDTO> getLabels() {
        return labels;
    }

    public void setLabels(List<ChannelTextBlockLabelDTO> labels) {
        this.labels = labels;
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
