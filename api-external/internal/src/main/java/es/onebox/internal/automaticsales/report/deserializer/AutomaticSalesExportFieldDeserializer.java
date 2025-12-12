package es.onebox.internal.automaticsales.report.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import es.onebox.internal.automaticsales.report.enums.AutomaticSalesFields;

import java.io.IOException;
import java.io.Serial;
import java.util.Optional;

public class AutomaticSalesExportFieldDeserializer extends StdDeserializer<AutomaticSalesFields> {

    @Serial
    private static final long serialVersionUID = -6259773459906333360L;

    public AutomaticSalesExportFieldDeserializer() {
        super(AutomaticSalesFields.class);
    }

    @Override
    public AutomaticSalesFields deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String code = null;
        JsonNode node = p.getCodec().readTree(p);
        if (node != null) {
            code = node.asText();
        }
        return Optional.ofNullable(AutomaticSalesFields.getByCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Unrecognized field name " + node.asText()));
    }
}
