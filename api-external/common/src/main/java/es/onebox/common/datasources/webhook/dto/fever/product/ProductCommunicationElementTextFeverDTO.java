package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.ms.event.enums.ProductCommunicationElementTextsType;

import java.io.Serial;
import java.io.Serializable;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductCommunicationElementTextFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2345678901234567890L;

    private ProductCommunicationElementTextsType type;
    private Long languageId;
    private String language;
    private String value;

    public ProductCommunicationElementTextFeverDTO() {
    }

    public ProductCommunicationElementTextsType getType() {
        return type;
    }

    public void setType(ProductCommunicationElementTextsType type) {
        this.type = type;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
