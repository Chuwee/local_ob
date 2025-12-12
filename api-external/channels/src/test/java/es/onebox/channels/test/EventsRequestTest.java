package es.onebox.channels.test;

import es.onebox.common.datasources.catalog.dto.session.request.EventsRequestDTO;
import es.onebox.common.datasources.catalog.dto.session.request.EventsRequestDTOBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class EventsRequestTest {

    @Test
    public void  generateKeyTest() {
        Assertions.assertEquals("null-null-null", this.generateParams(null, null, null).generateKey());
        Assertions.assertEquals("0-100-null", this.generateParams(0L, 100L, null).generateKey());
        Assertions.assertEquals("null-100-null", this.generateParams(null, 100L, null).generateKey());
        Assertions.assertEquals("null-null-1", this.generateParams(null, null, Arrays.asList(1L)).generateKey());
        Assertions.assertEquals("null-null-1-2", this.generateParams(null, null, Arrays.asList(1L, 2L)).generateKey());
    }

    private EventsRequestDTO generateParams(Long limit, Long offset, List<Long> eventIds) {
        return EventsRequestDTOBuilder.builder()
                .eventIds(eventIds)
                .limit(limit)
                .offset(offset)
                .build();
    }
}
