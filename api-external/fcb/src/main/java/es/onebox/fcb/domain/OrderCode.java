package es.onebox.fcb.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;

@CouchDocument
public class OrderCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Id
    private String code;
    private String externalCode;

    public OrderCode() {
    }

    public OrderCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
