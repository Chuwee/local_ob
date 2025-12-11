package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class DynamicCustomTagGroupsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("tag_1")
    private DynamicTagGroupDTO tag1;
    @JsonProperty("tag_2")
    private DynamicTagGroupDTO tag2;

    public DynamicTagGroupDTO getTag1() {
        return tag1;
    }

    public void setTag1(DynamicTagGroupDTO tag1) {
        this.tag1 = tag1;
    }

    public DynamicTagGroupDTO getTag2() {
        return tag2;
    }

    public void setTag2(DynamicTagGroupDTO tag2) {
        this.tag2 = tag2;
    }
}
