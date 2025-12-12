package es.onebox.common.datasources.accesscontrol.dto;

import java.io.Serializable;

public class TicketSaleDTO implements Serializable {

    private static final long serialVersionUID = 351632110375798981L;

    private Long id;
    private String name;
    private TicketPromotionType type;
    private Boolean restrictive;

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

    public TicketPromotionType getType() {
        return type;
    }

    public void setType(TicketPromotionType type) {
        this.type = type;
    }

    public Boolean getRestrictive() {
        return restrictive;
    }

    public void setRestrictive(Boolean restrictive) {
        this.restrictive = restrictive;
    }
}
