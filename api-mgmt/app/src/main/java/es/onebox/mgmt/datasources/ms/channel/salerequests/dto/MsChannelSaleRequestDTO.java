package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class MsChannelSaleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private ChannelType type;
    private ChannelSubtype subtype;
    private MsEntitySaleRequestDTO entity;
    private MsTaxonomiesSaleRequestDTO taxonomy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public ChannelSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelSubtype subtype) {
        this.subtype = subtype;
    }

    public MsEntitySaleRequestDTO getEntity() {
        return entity;
    }

    public void setEntity(MsEntitySaleRequestDTO entity) {
        this.entity = entity;
    }

    public MsTaxonomiesSaleRequestDTO getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(MsTaxonomiesSaleRequestDTO taxonomy) {
        this.taxonomy = taxonomy;
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
