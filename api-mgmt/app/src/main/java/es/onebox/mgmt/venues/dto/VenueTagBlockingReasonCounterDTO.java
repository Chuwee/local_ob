package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class VenueTagBlockingReasonCounterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String source;
    @JsonProperty("blocking_reason")
    private Long blockingReason;
    private Integer count;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getBlockingReason() {
        return blockingReason;
    }

    public void setBlockingReason(Long blockingReason) {
        this.blockingReason = blockingReason;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
