package es.onebox.event.sessions.dto;

import java.io.Serializable;

public class SessionTaxDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    public enum SessionTaxType {
        TICKET_TAX,
        CHARGES_TAX
    }

    private long id;
    private String type;
    private String name;
    private Double value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
