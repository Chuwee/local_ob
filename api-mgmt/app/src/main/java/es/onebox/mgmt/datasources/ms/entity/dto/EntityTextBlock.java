package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class EntityTextBlock extends IdDTO {

    private static final long serialVersionUID = 1L;

    private Long blockId;
    private Integer order;
    private EntityBlockCategory category;
    private EntityBlockType type;
    private String language;
    private String subject;
    private String value;
    private Boolean audited;
    private Boolean useFreeText;
    private List<EntityProfiledTextBlock> profiledTextsBlocks;
    private List<EntityTextBlockLabel> labels;

    public EntityBlockCategory getCategory() {
        return category;
    }

    public void setCategory(EntityBlockCategory category) {
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

    public EntityBlockType getType() {
        return type;
    }

    public void setType(EntityBlockType type) {
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

    public List<EntityProfiledTextBlock> getProfiledTextsBlocks() {
        return profiledTextsBlocks;
    }

    public void setProfiledTextsBlocks(List<EntityProfiledTextBlock> profiledTextsBlocks) {
        this.profiledTextsBlocks = profiledTextsBlocks;
    }

    public List<EntityTextBlockLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<EntityTextBlockLabel> labels) {
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
