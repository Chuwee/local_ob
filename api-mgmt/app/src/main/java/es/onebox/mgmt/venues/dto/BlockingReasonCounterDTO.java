package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BlockingReasonCounterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("blocking_reason")
    private Long blockingReason;
    private Integer count;

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
