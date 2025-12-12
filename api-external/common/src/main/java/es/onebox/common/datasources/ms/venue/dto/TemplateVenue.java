package es.onebox.common.datasources.ms.venue.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.util.List;

public class TemplateVenue extends Venue {

    @Serial
    private static final long serialVersionUID = 4513502402563351775L;
    private List<IdNameDTO> spaces;
    private Integer maxCapacity;

    public List<IdNameDTO> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<IdNameDTO> spaces) {
        this.spaces = spaces;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

}
