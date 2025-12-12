package es.onebox.common.datasources.webhook.dto.fever.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.SurchargeTypeDTO;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class SurchargesFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SurchargeTypeDTO type;
    private SurchargeLimitFeverDTO limit;
    private List<RangeFeverDTO> ranges;
    private Boolean allowChannelUseAlternativeCharges;

    public SurchargesFeverDTO() {
    }

    public SurchargesFeverDTO(SurchargeTypeDTO type, SurchargeLimitFeverDTO limit, List<RangeFeverDTO> ranges) {
        this.type = type;
        this.limit = limit;
        this.ranges = ranges;
    }

    public List<RangeFeverDTO> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeFeverDTO> ranges) {
        this.ranges = ranges;
    }

    public SurchargeTypeDTO getType() {
        return type;
    }

    public void setType(SurchargeTypeDTO type) {
        this.type = type;
    }

    public SurchargeLimitFeverDTO getLimit() {
        return limit;
    }

    public void setLimit(SurchargeLimitFeverDTO limit) {
        this.limit = limit;
    }

    public Boolean getAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }

        if(!(obj instanceof SurchargesFeverDTO)) {
            return false;
        }

        return ((SurchargesFeverDTO) obj).getType().equals(this.getType());
    }
}
