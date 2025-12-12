package es.onebox.common.datasources.ms.order.dto;

import es.onebox.common.datasources.ms.order.enums.ProductState;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AttendantHistoryDTO extends BaseAttendantDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7055401537248512197L;

    private List<AttendantHistoryRecordDTO> attendantHistory;

    public List<AttendantHistoryRecordDTO> getAttendantHistory() {
        return attendantHistory;
    }

    public void setAttendantHistory(List<AttendantHistoryRecordDTO> attendantHistory) {
        this.attendantHistory = attendantHistory;
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
