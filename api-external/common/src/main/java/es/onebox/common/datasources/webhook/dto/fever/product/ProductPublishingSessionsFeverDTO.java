package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.SelectionType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductPublishingSessionsFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9012345678901234567L;

    private SelectionType type;
    private Set<ProductSessionFeverDTO> sessions;

    public ProductPublishingSessionsFeverDTO() {
    }

    public ProductPublishingSessionsFeverDTO(SelectionType type, Set<ProductSessionFeverDTO> sessions) {
        this.type = type;
        this.sessions = sessions;
    }

    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) {
        this.type = type;
    }

    public Set<ProductSessionFeverDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<ProductSessionFeverDTO> sessions) {
        this.sessions = sessions;
    }
}
