package es.onebox.event.priceengine.taxes.utils;

import es.onebox.core.order.utils.tax.TaxUtils;
import es.onebox.event.priceengine.taxes.domain.TaxAmount;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaxUtilsTest {

    @Test
    void calculateTaxDivisor() {
        List<Double> taxes = List.of(21D);

        Double taxDivisor = TaxUtils.calculateTaxDivisor(taxes);

        assertEquals(1.21, taxDivisor);

    }

    @Test
    void calculateNetPrice() {
        List<Double> taxes = List.of(21D);
        Double priceWithTaxes = 100d;

        Double taxDivisor = TaxUtils.calculateNetPrice(priceWithTaxes, taxes);

        assertEquals(82.64, taxDivisor);
    }

    @Test
    void createTaxInfo_whenNotNull() {
        CpanelImpuestoRecord cpanelImpuestoRecord = new CpanelImpuestoRecord();
        cpanelImpuestoRecord.setIdimpuesto(1);
        cpanelImpuestoRecord.setNombre("Impuesto 1");
        cpanelImpuestoRecord.setValor(21d);

        TaxInfo productTaxInfo = TaxSimulationUtils.createTaxInfo(
                cpanelImpuestoRecord.getIdimpuesto().longValue(), cpanelImpuestoRecord.getValor(), cpanelImpuestoRecord.getNombre(), () -> new TaxInfo() {}
        );

        Assertions.assertEquals(cpanelImpuestoRecord.getIdimpuesto().longValue(), productTaxInfo.getId());
        Assertions.assertEquals(cpanelImpuestoRecord.getNombre(), productTaxInfo.getName());
        Assertions.assertEquals(cpanelImpuestoRecord.getValor(), productTaxInfo.getValue());
        assertNull(productTaxInfo.getDescription());

    }

    @Test
    void createTaxInfo_whenNull() {

        TaxInfo productTaxInfo = TaxSimulationUtils.createTaxInfo(
                null, null, null, ()  -> new TaxInfo() {}
        );

        assertNull(productTaxInfo.getId());
        assertNull(productTaxInfo.getName());
        Assertions.assertEquals(0D, productTaxInfo.getValue());
        assertNull(productTaxInfo.getDescription());

    }

    @Test
    void calculateTaxAmountGivenNetPrice() {
        Double specificTaxAmount = 21d;
        Double priceWithTaxes = 82.64;

        Double taxAmount = TaxUtils.calculateTaxAmountGivenNetPrice(priceWithTaxes, specificTaxAmount);

        assertEquals(17.35, taxAmount);
    }

    @Test
    void createTaxAmount() {

        Double netPrice = 76.34;
        Double totalTaxes = 23.66;

        TaxInfo tax1 = new TaxInfo() {{
            setId(1L);
            setValue(21.0);
        }};

        TaxInfo tax2 = new TaxInfo() {{
            setId(2L);
            setValue(10.0);
        }};

        List<TaxInfo> taxes = List.of(tax1, tax2);

        List<TaxAmount> result = TaxSimulationUtils.createTaxAmount(
                totalTaxes,
                netPrice,
                taxes,
                () -> new TaxAmount() {}
        );

        assertNotNull(result);
        assertEquals(2, result.size());

        Assertions.assertEquals(tax1.getId(), result.get(0).getId());
        Assertions.assertEquals(16.03, result.get(0).getAmount());

        Assertions.assertEquals(tax2.getId(), result.get(1).getId());
        Assertions.assertEquals(7.63, result.get(1).getAmount());

    }
}
