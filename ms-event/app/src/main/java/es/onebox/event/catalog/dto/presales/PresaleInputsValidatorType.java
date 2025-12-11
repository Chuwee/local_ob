package es.onebox.event.catalog.dto.presales;

import java.util.Arrays;

public enum PresaleInputsValidatorType { //cpanel_subitipo_colectivo
    PROMOTIONAL_CODE_INTERNAL(1,1),
    USER_INTERNAL(2,1),
    USERNAME_PASSWORD_INTERNAL(3, 2),
    PROMOTIONAL_CODE_EXTERNAL(4, 1),
    USER_EXTERN(5, 1),
    USERNAME_PASSWORD_EXTERNAL(6,2),
    USER_CODE_PASSWORD_EXTERNAL(13, 3);



    private final Integer id;
    private final Integer numInputs;

    PresaleInputsValidatorType(Integer id, Integer numInputs) {
        this.id = id;
        this.numInputs = numInputs;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumInputs() {
        return numInputs;
    }

    public static PresaleInputsValidatorType getById(Integer id) {
        return Arrays.stream(values())
                .filter( item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
