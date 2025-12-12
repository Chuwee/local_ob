package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.dto.SessionDateDTO;

import java.io.Serial;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductSessionFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8901234567890123456L;

    private Long id;
    private String name;
    private SessionDateDTO dates;

    public ProductSessionFeverDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionDateDTO getDates() {
        return dates;
    }

    public void setDates(SessionDateDTO dates) {
        this.dates = dates;
    }
}
