package es.onebox.common.datasources.webhook.dto.fever;


import java.util.stream.Stream;

public enum SaleRequestStatus {

    REJECTED(0),
    PENDING(1),
    ACCEPTED(2);

    private final int id;

    SaleRequestStatus(int id){
        this.id = id;
    }

    public static SaleRequestStatus getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(status -> status.id == id)
                .findFirst()
                .orElse(null);
    }

    public int getId() {
        return id;
    }
}

