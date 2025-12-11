package es.onebox.mgmt.operators.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorCurrenciesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.operators.enums.OperatorShards;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record CreateOperatorRequestDTO (@NotNull(message = "name must not be null") String name,
                                        @Size(max = 30) @JsonProperty("short_name") @NotNull(message = "shortName must not be null") String shortName,
                                        @JsonProperty("currency_code") @NotNull(message = "currencyCode must not be null") String currencyCode,
                                        @JsonProperty("olson_id") @NotNull(message = "olson_id must not be null") String olsonId,
                                        @LanguageIETF  @JsonProperty("language_code") @NotNull(message = "language_code must not be null") String languageCode,
                                        @NotNull(message = "shard must not be null") OperatorShards shard,
                                        @NotEmpty(message = "gateways must not be empty") List<String> gateways,
                                        CreateOperatorCurrenciesRequest currencies, List<WalletConfigDTO> wallets) implements Serializable {

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
