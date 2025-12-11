package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPromotionCollective implements Serializable {

    @Serial
    private static final long serialVersionUID = -5670809878402240732L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
