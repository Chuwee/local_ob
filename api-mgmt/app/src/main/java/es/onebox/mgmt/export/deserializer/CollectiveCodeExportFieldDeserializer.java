package es.onebox.mgmt.export.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.mgmt.collectives.collectivecodes.enums.CollectiveCodeField;

import java.io.IOException;
import java.util.Optional;

public class CollectiveCodeExportFieldDeserializer extends StdDeserializer<CollectiveCodeField> {

    private static final long serialVersionUID = 1L;

    public CollectiveCodeExportFieldDeserializer() {
        super(CollectiveCodeField.class);
    }

    @Override
    public CollectiveCodeField deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (node != null) {
            code = node.asText();
        }
        return Optional.ofNullable(CollectiveCodeField.getByCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized field name " + node.asText()));
    }
}
