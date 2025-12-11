package es.onebox.event.query.events.dto;

import es.onebox.event.promotions.enums.PromotionType;

import java.io.Serializable;

/**
 * @author ignasi
 */
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long templateId;
    private Long eventTemplateId;
    private String name;
    private PromotionType type;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getEventTemplateId() {
        return eventTemplateId;
    }

    public void setEventTemplateId(Long eventTemplateId) {
        this.eventTemplateId = eventTemplateId;
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
