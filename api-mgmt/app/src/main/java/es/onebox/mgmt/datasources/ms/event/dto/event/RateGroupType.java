package es.onebox.mgmt.datasources.ms.event.dto.event;

public enum RateGroupType {

    PRODUCT(1),
    RATE(2);

    private final Integer id;

    RateGroupType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

}
