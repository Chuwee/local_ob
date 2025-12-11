package es.onebox.mgmt.events.postbookingquestions.dto;

import es.onebox.mgmt.events.dto.TranlationDTO;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3857291050042545660L;

    private Integer id;
    private String name;
    private TranlationDTO label;

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

    public TranlationDTO getLabel() {
        return label;
    }

    public void setLabel(TranlationDTO label) {
        this.label = label;
    }
}
