package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class SearchSessionsResponse extends BaseResponseCollection<SearchSessionsDTO, Metadata> implements DateConvertible {


    @Serial
    private static final long serialVersionUID = -8807160678178683243L;

    @Override
    public void convertDates() {
        if (!CommonUtils.isEmpty(getData())) {
            for (SearchSessionsDTO session : getData()) {
                session.convertDates();
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
