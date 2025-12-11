package es.onebox.mgmt.sessions.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class SessionPackNotNumberedZoneLinkDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id is mandatory")
    private Long id;

    @NotNull(message = "source is mandatory")
    private String source;
    @NotNull(message = "target is mandatory")
    private String target;
    @NotNull(message = "count is mandatory")
    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
