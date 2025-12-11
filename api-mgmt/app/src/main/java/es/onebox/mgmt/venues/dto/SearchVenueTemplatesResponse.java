package es.onebox.mgmt.venues.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SearchVenueTemplatesResponse extends BaseResponseCollection<VenueTemplateDTO, Metadata> implements DateConvertible {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public void convertDates() {
        if (CollectionUtils.isNotEmpty(getData())) {
            for (VenueTemplateDTO venueTemplateDTO : getData()) {
                venueTemplateDTO.convertDates();
            }
        }
    }

}
