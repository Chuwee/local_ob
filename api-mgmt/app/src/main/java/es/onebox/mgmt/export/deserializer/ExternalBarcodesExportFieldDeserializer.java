package es.onebox.mgmt.export.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.mgmt.sessions.enums.ExternalBarcodesField;
import es.onebox.mgmt.sessions.enums.WhiteListField;

import java.io.IOException;
import java.util.Optional;

public class ExternalBarcodesExportFieldDeserializer extends StdDeserializer<ExternalBarcodesField> {

    private static final long serialVersionUID = 1L;

    public ExternalBarcodesExportFieldDeserializer() {
        super(WhiteListField.class);
    }

    @Override
    public ExternalBarcodesField deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (node != null) {
            code = node.asText();
        }
        return Optional.ofNullable(ExternalBarcodesField.getByCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized field name " + node.asText()));
    }
}
