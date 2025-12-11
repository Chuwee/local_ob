package es.onebox.mgmt.datasources.ms.entity.dto;



public class IdValueCodeDTO extends IdValue {

    private static final long serialVersionUID = 1L;

    private String code;

    public IdValueCodeDTO() {
    }

    public IdValueCodeDTO(Long id, String value, String code) {
        this.setId(id);
        this.setValue(value);
        this.setCode(code);
    }

    public IdValueCodeDTO(Long id) {
        super(id);
    }

    public IdValueCodeDTO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
