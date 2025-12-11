package es.onebox.mgmt.salerequests.taxes.converter;

import es.onebox.mgmt.datasources.ms.channel.dto.taxes.SaleRequestSurchargesTaxes;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.SaleRequestSurchargesTaxesUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.Tax;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.TaxInfo;
import es.onebox.mgmt.datasources.ms.channel.enums.SaleRequestSurchargesTaxesOrigin;
import es.onebox.mgmt.salerequests.taxes.dto.SaleRequestSurchargesTaxesDTO;
import es.onebox.mgmt.salerequests.taxes.dto.SaleRequestsSurchargesTaxesUpdateDTO;
import es.onebox.mgmt.salerequests.taxes.dto.TaxDTO;
import es.onebox.mgmt.salerequests.taxes.dto.TaxInfoDTO;
import es.onebox.mgmt.salerequests.taxes.enums.SaleRequestSurchargesTaxesOriginDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class SaleRequestTaxesConverter {

    private SaleRequestTaxesConverter(){}

    public static SaleRequestSurchargesTaxesDTO toDTO(SaleRequestSurchargesTaxes in) {
        if (in == null) {
            return null;
        }
        SaleRequestSurchargesTaxesDTO out = new SaleRequestSurchargesTaxesDTO();
        out.setOrigin(SaleRequestSurchargesTaxesOriginDTO.fromMs(in.getOrigin()));
        out.setTaxes(toDTO(in.getTaxes()));
        return out;
    }

    private static List<TaxInfoDTO> toDTO(List<TaxInfo> channelSurchargesTaxes) {
        if (CollectionUtils.isEmpty(channelSurchargesTaxes)) {
            return null;
        }
        return channelSurchargesTaxes.stream()
                .map(SaleRequestTaxesConverter::toDTO)
                .toList();
    }

    private static TaxInfoDTO toDTO(TaxInfo in) {
        if (in == null) {
            return null;
        }
        TaxInfoDTO out = new TaxInfoDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        return out;
    }

    public static SaleRequestSurchargesTaxesUpdate toMS(SaleRequestsSurchargesTaxesUpdateDTO in) {
        SaleRequestSurchargesTaxesUpdate out = new SaleRequestSurchargesTaxesUpdate();
        out.setOrigin(SaleRequestSurchargesTaxesOrigin.fromDTO(in.getOrigin()));
        out.setTaxes(toMS(in.getTaxes()));
        return out;
    }

    private static List<Tax> toMS(List<TaxDTO> in) {
        if (in == null) {
            return null;
        }
        return in.stream()
                .map(t -> {
                    Tax tax = new Tax();
                    tax.setId(t.getId());
                    return tax;
                })
                .toList();
    }

}
