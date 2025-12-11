package es.onebox.mgmt.seasontickets.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.LinkedMetadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SearchSeasonTicketsResponse extends BaseResponseCollection<SeasonTicketSearchResultDTO, LinkedMetadata> implements DateConvertible {

    private static final long serialVersionUID = 1L;

    @Override
    public void convertDates() {
        if (!CommonUtils.isEmpty(getData())) {
            for (SeasonTicketSearchResultDTO seasonTicket : getData()) {
                seasonTicket.convertDates();
            }
        }
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
