package es.onebox.mgmt.currencies;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.common.dto.Range;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.operators.dto.OperatorCurrencies;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class CurrenciesUtils {
    private CurrenciesUtils() {
    }

    public static String getCurrencyCode(List<Currency> currencies, Long currencyId) {
        Currency currency = currencies.stream().filter(c -> c.getId().equals(currencyId)).findFirst().orElse(null);
        if(currency != null) {
            return currency.getCode();
        }
        throw  new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
    }

    public static Long getCurrencyId(List<Currency> currencies, String currencyCode) {
        Currency currency = currencies.stream().filter(c -> c.getCode().equals(currencyCode)).findFirst().orElse(null);
        if(currency != null) {
            return currency.getId();
        }
        throw  new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
    }

    public static List<Long> getCurrencyIds(String currencyCode, OperatorCurrencies currencies) {
        if (currencyCode != null) {
            return Collections.singletonList(currencies.getSelected().stream()
                    .filter(currency -> currency.getCode().equals(currencyCode))
                    .map(Currency::getId)
                    .findFirst()
                    .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED)));
        }
        return null;
    }

    public static Currency getCurrencyByCurrencyId(Long currencyId, List<Currency> currencies) {
        return currencies.stream()
                .filter(cc-> cc.getId().equals(currencyId))
                .findFirst().orElse(null);
    }

    public static Currency getDefaultCurrency(Operator operator) {
        Currency defaultCurrency = new Currency();
        if(Objects.nonNull(operator.getCurrencies())) {
            Currency defaultOperatorCurrency = operator.getCurrencies().getSelected().stream()
                    .filter(doc -> operator.getCurrencies().getDefaultCurrency().equals(doc.getCode())).findFirst().orElse(null);
            if(defaultOperatorCurrency != null) {
                defaultCurrency.setId(defaultOperatorCurrency.getId());
                defaultCurrency.setCode(defaultOperatorCurrency.getCode());
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
            }
        } else {
            defaultCurrency.setCode(operator.getCurrency().getValue());
            defaultCurrency.setId(operator.getCurrency().getId().longValue());
        }
        return defaultCurrency;
    }

    public static boolean hasDefaultCurrencyRange (Range range, Long defaultCurrencyId, List<Range> ranges) {
        return range.getCurrencyId() == null
                && ranges.stream().anyMatch(r -> defaultCurrencyId.equals(r.getCurrencyId()));
    }
}
