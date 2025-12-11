package es.onebox.mgmt.channels.taxes.converter;

import es.onebox.mgmt.channels.taxes.dto.ChannelSurchargesTaxesDTO;
import es.onebox.mgmt.channels.taxes.dto.ChannelSurchargesTaxesUpdateDTO;
import es.onebox.mgmt.channels.taxes.dto.TaxDTO;
import es.onebox.mgmt.channels.taxes.dto.TaxInfoDTO;
import es.onebox.mgmt.channels.taxes.enums.ChannelSurchargesTaxesOriginDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.ChannelSurchargesTaxes;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.ChannelSurchargesTaxesUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.Tax;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.TaxInfo;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSurchargesTaxesOrigin;

import java.util.Collections;
import java.util.List;

public class ChannelTaxesConverter {

    private ChannelTaxesConverter(){}

    public static ChannelSurchargesTaxesDTO toDTO(ChannelSurchargesTaxes in) {
        if (in == null) {
            return null;
        }
        ChannelSurchargesTaxesDTO out = new ChannelSurchargesTaxesDTO();
        out.setOrigin(ChannelSurchargesTaxesOriginDTO.fromMs(in.getOrigin()));
        out.setTaxes(toDTO(in.getTaxes()));
        return out;
    }

    private static List<TaxInfoDTO> toDTO(List<TaxInfo> in) {
        if (in == null) {
            return Collections.emptyList();
        }
        return in.stream()
                .map(ChannelTaxesConverter::toDTO)
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

    public static ChannelSurchargesTaxesUpdate toMS(ChannelSurchargesTaxesUpdateDTO in) {
        ChannelSurchargesTaxesUpdate out = new ChannelSurchargesTaxesUpdate();
        out.setOrigin(ChannelSurchargesTaxesOrigin.fromDTO(in.getOrigin()));
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
