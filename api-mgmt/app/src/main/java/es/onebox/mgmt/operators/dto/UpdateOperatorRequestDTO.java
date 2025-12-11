package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record UpdateOperatorRequestDTO(String name, @JsonProperty("currency_code") String currencyCode,
                                       @JsonProperty("olson_id") String olsonId,
                                       @LanguageIETF @JsonProperty("language_code") String languageCode,
                                       List<String> gateways, List<WalletConfigDTO> wallets,
                                       @JsonProperty("allow_fever_zone") Boolean allowFeverZone,
                                       @JsonProperty("allow_gateway_benefits") Boolean allowGatewayBenefits)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
