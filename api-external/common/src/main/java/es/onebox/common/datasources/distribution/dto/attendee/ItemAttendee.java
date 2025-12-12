package es.onebox.common.datasources.distribution.dto.attendee;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class ItemAttendee implements Serializable {

    @Serial
    private static final long serialVersionUID = 8955427720598108272L;
    private Long item_id;
    private HashMap<String, Object> field;

    public Long getItem_id() {
        return item_id;
    }

    public void setItem_id(Long item_id) {
        this.item_id = item_id;
    }

    public HashMap<String, Object> getField() {
        return field;
    }

    public void setField(HashMap<String, Object> field) {
        this.field = field;
    }
}
