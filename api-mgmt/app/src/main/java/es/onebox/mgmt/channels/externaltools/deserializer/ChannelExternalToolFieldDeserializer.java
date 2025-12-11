package es.onebox.mgmt.channels.externaltools.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolIdentifierDTO;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ChannelExternalToolFieldDeserializer extends StdDeserializer<ChannelExternalToolIdentifierDTO> {

    private static final long serialVersionUID = 1L;

    public ChannelExternalToolFieldDeserializer() {
        super(ChannelExternalToolIdentifierDTO.class);
    }

    @Override
    public ChannelExternalToolIdentifierDTO deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (Objects.nonNull(node)) {
            code = node.asText();
        }
        return Optional.ofNullable(ChannelExternalToolIdentifierDTO.getByCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized field name " + node.asText()));
    }
}
