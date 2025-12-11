package es.onebox.event.query.events.dto;

import java.io.Serializable;

/**
 * @author ignasi
 */
public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Entity entity;

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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
