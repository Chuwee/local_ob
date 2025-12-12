package es.onebox.flc.orders.dto.groups;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VisitorGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3696279462639128613L;

    private Long idGroup;
    private String name;
    private Integer numAttendants;
    private Integer numAccompanists;
    private List<AttributeValuesDTO> attributeValues;

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNumAttendants(Integer numAttendants) {
        this.numAttendants = numAttendants;
    }

    public Integer getNumAttendants() {
        return numAttendants;
    }

    public void setNumAccompanists(Integer numAccompanists) {
        this.numAccompanists = numAccompanists;
    }

    public Integer getNumAccompanists() {
        return numAccompanists;
    }

    public void setAttributeValues(List<AttributeValuesDTO> attributeValues) {

        this.attributeValues = attributeValues;
    }

    public List<AttributeValuesDTO> getAttributeValues() {
        return attributeValues;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
