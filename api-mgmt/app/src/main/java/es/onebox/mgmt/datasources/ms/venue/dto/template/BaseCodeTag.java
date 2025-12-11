package es.onebox.mgmt.datasources.ms.venue.dto.template;

public class BaseCodeTag extends BaseTag {

    private static final long serialVersionUID = 1L;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BaseCodeTag() {
    }

    public BaseCodeTag(Long id) {
        super(id);
    }
}
