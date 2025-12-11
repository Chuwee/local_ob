package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.event.dto.session.PointGain;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSessionsLoyaltyPointsConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1677075448169485883L;

    private PointGain pointGain;

    public PointGain getPointGain() { return pointGain; }

    public void setPointGain(PointGain pointGain) { this.pointGain = pointGain; }
}
