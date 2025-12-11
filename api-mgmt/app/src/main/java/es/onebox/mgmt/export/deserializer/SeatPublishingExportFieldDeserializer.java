package es.onebox.mgmt.export.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.mgmt.b2b.publishing.enums.SeatPublishingFileField;

import java.io.IOException;
import java.util.Optional;

public class SeatPublishingExportFieldDeserializer extends StdDeserializer<SeatPublishingFileField> {

    public SeatPublishingExportFieldDeserializer() {
        super(SeatPublishingFileField.class);
    }

    @Override
    public SeatPublishingFileField deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (node != null) {
            code = node.asText();
        }
        return Optional.ofNullable(code)
                .map(SeatPublishingFileField::getByCode)
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized field name " + Optional.ofNullable(node).map(JsonNode::asText).orElse("null")));
    }
}
