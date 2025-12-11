package es.onebox.mgmt.salerequests.contents.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestAgreement;
import es.onebox.mgmt.salerequests.contents.dto.CreateSaleRequestAgreementDTO;
import es.onebox.mgmt.salerequests.contents.dto.SaleRequestAgreementDTO;
import es.onebox.mgmt.salerequests.contents.dto.UpdateSaleRequestAgreementDTO;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaleRequestAgreementConverter {

    private SaleRequestAgreementConverter() {
    }

    public static SaleRequestAgreement toDTO(CreateSaleRequestAgreementDTO in) {
        SaleRequestAgreement out = new SaleRequestAgreement();
        out.setName(in.getName());
        out.setTexts(in.getTexts().entrySet().stream()
                .collect(Collectors.toMap(entry -> ConverterUtils.toLocale(entry.getKey()), Map.Entry::getValue)));
        return out;
    }

    public static SaleRequestAgreement toDTO(UpdateSaleRequestAgreementDTO in) {
        SaleRequestAgreement out = new SaleRequestAgreement();
        out.setName(in.getName());
        out.setMandatory(in.getMandatory());
        out.setEnabled(in.getEnabled());
        if (MapUtils.isNotEmpty(in.getTexts())) {
            out.setTexts(in.getTexts().entrySet().stream()
                    .collect(Collectors.toMap(entry -> ConverterUtils.toLocale(entry.getKey()), Map.Entry::getValue)));
        }
        return out;
    }

    public static List<SaleRequestAgreementDTO> toResponse(List<SaleRequestAgreement> in) {
        return in.stream().map(SaleRequestAgreementConverter::toDTO).collect(Collectors.toList());
    }

    private static SaleRequestAgreementDTO toDTO(SaleRequestAgreement in) {
        SaleRequestAgreementDTO out = new SaleRequestAgreementDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setMandatory(in.getMandatory());
        out.setEnabled(in.getEnabled());
        out.setTexts(in.getTexts().entrySet().stream()
                .collect(Collectors.toMap(entry -> ConverterUtils.toLanguageTag(entry.getKey()), Map.Entry::getValue)));
        return out;
    }

}
