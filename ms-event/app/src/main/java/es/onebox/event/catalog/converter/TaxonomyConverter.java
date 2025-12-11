package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.events.dto.TaxonomyDTO;

public class TaxonomyConverter {

    private TaxonomyConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static TaxonomyDTO convertTaxonomy(Event event) {
        TaxonomyDTO taxonomy = null;
        if (event.getTaxonomyId() != null || event.getTaxonomyCode() != null || event.getTaxonomyDescription() != null) {
            taxonomy = new TaxonomyDTO();
            taxonomy.setCode(event.getTaxonomyCode());
            taxonomy.setId(event.getTaxonomyId());
            taxonomy.setDescription(event.getTaxonomyDescription());
        }
        return taxonomy;
    }

    public static TaxonomyDTO convertCustomTaxonomy(Event event) {
        TaxonomyDTO taxonomy = null;
        if (event.getCustomTaxonomyId() != null || event.getCustomTaxonomyCode() != null || event.getCustomTaxonomyDescription() != null) {
            taxonomy = new TaxonomyDTO();
            taxonomy.setCode(event.getCustomTaxonomyCode());
            taxonomy.setId(event.getCustomTaxonomyId());
            taxonomy.setDescription(event.getCustomTaxonomyDescription());
        }
        return taxonomy;
    }

    public static TaxonomyDTO convertCustomTaxonomy(ChannelEvent channelEvent) {
        TaxonomyDTO taxonomy = null;
        if (channelEvent.getCustomCategoryId() != null || channelEvent.getCustomCategoryCode() != null
                || channelEvent.getCustomCategoryName() != null) {
            taxonomy = new TaxonomyDTO();
            taxonomy.setCode(channelEvent.getCustomCategoryCode());
            taxonomy.setId(channelEvent.getCustomCategoryId());
            taxonomy.setDescription(channelEvent.getCustomCategoryName());
        }
        return taxonomy;
    }

    public static TaxonomyDTO convertCustomParentTaxonomy(ChannelEvent channelEvent) {
        TaxonomyDTO taxonomy = null;
        if (channelEvent.getCustomParentCategoryId() != null || channelEvent.getCustomParentCategoryCode() != null
                || channelEvent.getCustomParentCategoryName() != null) {
            taxonomy = new TaxonomyDTO();
            taxonomy.setCode(channelEvent.getCustomParentCategoryCode());
            taxonomy.setId(channelEvent.getCustomParentCategoryId());
            taxonomy.setDescription(channelEvent.getCustomParentCategoryName());
        }
        return taxonomy;
    }

    public static TaxonomyDTO convertParentTaxonomy(Event event) {
        TaxonomyDTO taxonomy = null;
        if (event.getTaxonomyParentId() != null || event.getTaxonomyParentCode() != null || event.getTaxonomyParentDescription() != null) {
            taxonomy = new TaxonomyDTO();
            taxonomy.setId(event.getTaxonomyParentId());
            taxonomy.setCode(event.getTaxonomyParentCode());
            taxonomy.setDescription(event.getTaxonomyParentDescription());
        }
        return taxonomy;
    }
}
