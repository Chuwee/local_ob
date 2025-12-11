package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MsTaxonomiesSaleRequestDTO extends MsTaxonomySaleRequestDTO{

    private static final long serialVersionUID = 3388908792252556420L;

    private MsTaxonomySaleRequestDTO parentTaxonomy;
    private MsTaxonomySaleRequestDTO customTaxonomy;

    public MsTaxonomySaleRequestDTO getParentTaxonomy() {
        return parentTaxonomy;
    }

    public void setParentTaxonomy(MsTaxonomySaleRequestDTO parentTaxonomy) {
        this.parentTaxonomy = parentTaxonomy;
    }

    public MsTaxonomySaleRequestDTO getCustomTaxonomy() {
        return customTaxonomy;
    }

    public void setCustomTaxonomy(MsTaxonomySaleRequestDTO customTaxonomy) {
        this.customTaxonomy = customTaxonomy;
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
