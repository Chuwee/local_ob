package es.onebox.common.entities.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class IdValueCodeDTO extends IdValue {

    @Serial
    private static final long serialVersionUID = -1158465928886705786L;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
