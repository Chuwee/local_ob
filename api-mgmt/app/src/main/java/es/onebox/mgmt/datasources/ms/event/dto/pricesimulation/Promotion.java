package es.onebox.mgmt.datasources.ms.event.dto.pricesimulation;



import java.io.Serializable;

public class Promotion implements Serializable {

    private static final long serialVersionUID = 4061094974431377457L;

    private Long id;
    private String name;
    private PromotionType type;

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

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }
}
