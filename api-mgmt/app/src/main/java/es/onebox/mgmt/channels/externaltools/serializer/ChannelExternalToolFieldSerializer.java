package es.onebox.mgmt.channels.externaltools.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolIdentifierDTO;

import java.io.IOException;

public class ChannelExternalToolFieldSerializer extends StdSerializer<ChannelExternalToolIdentifierDTO> {

    private static final long serialVersionUID = 1L;

    public ChannelExternalToolFieldSerializer() {
        super(ChannelExternalToolIdentifierDTO.class);
    }

    @Override
    public void serialize(ChannelExternalToolIdentifierDTO channelExternalToolIdentifierDTO, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(channelExternalToolIdentifierDTO.getCode());
    }
}
