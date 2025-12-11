package es.onebox.event.seasontickets.dto.renewals;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UniversalSeatIdentifier {

    private String sectorCode;

    private String rowName;

    private String seatName;

    public UniversalSeatIdentifier(String sectorCode, String rowName, String seatName) {
        this.sectorCode = sectorCode;
        this.rowName = rowName;
        this.seatName = seatName;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String toString() {
        return sectorCode + "-" + rowName + "-" + seatName;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
