/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.mgmt.datasources.ms.venue.dto;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * @author ignasi
 */
public enum VenueStatus implements Serializable {

    DELETED(0),
    ACTIVE(1),
    PENDING(2),
    BLOCKED(3);

    private final Integer id;

    private VenueStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static VenueStatus byId(Integer id) {
        return Stream.of(VenueStatus.values())
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
