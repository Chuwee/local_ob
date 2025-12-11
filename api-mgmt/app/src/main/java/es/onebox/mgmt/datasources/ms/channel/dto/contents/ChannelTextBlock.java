package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.contents.enums.ChannelBlockCategory;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBlockType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ChannelTextBlock extends IdDTO {

    private static final long serialVersionUID = 1L;

    private Long blockId;
    private Integer order;
    private ChannelBlockCategory category;
    private ChannelBlockType type;
    private String language;
    private String subject;
    private String value;
    private Boolean audited;
    private Boolean useFreeText;
    private List<ChannelProfiledTextBlock> profiledTextsBlocks;
    private List<ChannelTextBlockLabel> labels;

    public ChannelBlockCategory getCategory() {
        return category;
    }

    public void setCategory(ChannelBlockCategory category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ChannelBlockType getType() {
        return type;
    }

    public void setType(ChannelBlockType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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

    public List<ChannelProfiledTextBlock> getProfiledTextsBlocks() {
        return profiledTextsBlocks;
    }

    public void setProfiledTextsBlocks(List<ChannelProfiledTextBlock> profiledTextsBlocks) {
        this.profiledTextsBlocks = profiledTextsBlocks;
    }

    public List<ChannelTextBlockLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<ChannelTextBlockLabel> labels) {
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
