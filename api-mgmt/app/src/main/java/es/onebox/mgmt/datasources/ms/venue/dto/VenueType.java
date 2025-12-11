package es.onebox.mgmt.datasources.ms.venue.dto;

import java.util.Arrays;

public enum VenueType {
    AUDITORIUM(1),
    CONGRESS_HALL(2),
    SPEED_CIRCUIT(3),
    THEATRE(4),
    STADIUM(5),
    SPORTS_DOME(6),
    FAIRGROUNDS(7),
    OPEN_SPACE(8),
    SPORTS_CLUB(9),
    OTHER(10);

    private int venueTypeId;

    VenueType(int venueTypeId){
        this.venueTypeId = venueTypeId;
    }

    public Integer getVenueTypeId(){
        return venueTypeId;
    }

    public static VenueType getById(int id){
        return Arrays.stream(values())
                .filter(venueT->venueT.getVenueTypeId().equals(id))
                .findAny().orElseThrow();
    }
}
