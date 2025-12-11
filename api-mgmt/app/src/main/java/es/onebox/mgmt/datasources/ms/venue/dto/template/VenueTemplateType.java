/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 *
 * @author ignasi
 */
public enum VenueTemplateType implements Serializable {

    DEFAULT(1),
    AVET(2),
    ACTIVITY(3),
    THEME_PARK(4);

    private final Integer id;

    private VenueTemplateType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static VenueTemplateType byId(Integer id) {
        return Stream.of(VenueTemplateType.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
