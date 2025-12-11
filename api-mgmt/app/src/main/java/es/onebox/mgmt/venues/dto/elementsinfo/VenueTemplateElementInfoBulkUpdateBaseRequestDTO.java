package es.onebox.mgmt.venues.dto.elementsinfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VenueTemplateElementInfoBulkUpdateBaseRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7164597868176457200L;


    @JsonProperty("update_all_elements_info")
    private Boolean updateAllElementsInfo;
    @JsonProperty("elements_type_related_id_map")
    private Map<String, List<Long>> elementsTypeRelatedIdMap;

    public Boolean getUpdateAllElementsInfo() {
        return updateAllElementsInfo;
    }

    public void setUpdateAllElementsInfo(Boolean updateAllElementsInfo) {
        this.updateAllElementsInfo = updateAllElementsInfo;
    }

    public Map<String, List<Long>> getElementsTypeRelatedIdMap() {
        return elementsTypeRelatedIdMap;
    }

    public void setElementsTypeRelatedIdMap(Map<String, List<Long>> elementsTypeRelatedIdMap) {
        this.elementsTypeRelatedIdMap = elementsTypeRelatedIdMap;
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
