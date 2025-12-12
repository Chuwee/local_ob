package es.onebox.channels.catalog.eci;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by csobrino on 19/04/2018.
 */
public class ZonedDateTimeSerialize extends JsonSerializer<ZonedDateTime> {

    @Override
    public void serialize(ZonedDateTime zdt, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(zdt != null ? DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zdt) : null);
    }
}
