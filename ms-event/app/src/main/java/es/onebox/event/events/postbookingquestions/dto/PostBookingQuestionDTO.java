package es.onebox.event.events.postbookingquestions.dto;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3858951050042545660L;

    private Integer id;
    private String name;
    private TranslationDTO label;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TranslationDTO getLabel() {
        return label;
    }

    public void setLabel(TranslationDTO label) {
        this.label = label;
    }
}
