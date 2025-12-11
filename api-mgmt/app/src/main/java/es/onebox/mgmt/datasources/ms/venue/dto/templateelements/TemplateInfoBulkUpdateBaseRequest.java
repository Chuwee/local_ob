package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TemplateInfoBulkUpdateBaseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7164597868176457200L;


    private Boolean updateAllTemplateInfo;
    private Map<String, List<Long>> templateInfoTypeWithIdsMap;

    public Boolean getUpdateAllTemplateInfo() {
        return updateAllTemplateInfo;
    }

    public void setUpdateAllTemplateInfo(Boolean updateAllTemplateInfo) {
        this.updateAllTemplateInfo = updateAllTemplateInfo;
    }

    public Map<String, List<Long>> getTemplateInfoTypeWithIdsMap() {
        return templateInfoTypeWithIdsMap;
    }

    public void setTemplateInfoTypeWithIdsMap(Map<String, List<Long>> templateInfoTypeWithIdsMap) {
        this.templateInfoTypeWithIdsMap = templateInfoTypeWithIdsMap;
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
