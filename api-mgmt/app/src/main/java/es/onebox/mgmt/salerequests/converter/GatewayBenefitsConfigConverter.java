package es.onebox.mgmt.salerequests.converter;

import es.onebox.mgmt.datasources.ms.payment.dto.benefits.Badge;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.Benefit;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.BenefitGroupConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.BinGroup;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.BrandGroup;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.CheckoutCommunicationElements;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.GatewayBenefitConfiguration;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.ValidityPeriod;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BadgeDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BenefitDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BenefitGroupConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BinGroupDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.BrandGroupDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.CheckoutCommunicationElementsDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigDTO;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.GatewayBenefitsConfigRequest;
import es.onebox.mgmt.salerequests.gateways.benefit.dto.ValidityPeriodDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GatewayBenefitsConfigConverter {

    public static GatewayBenefitsConfigDTO convert(GatewayBenefitConfiguration in) {
        if (in == null) {
            return null;
        }

        GatewayBenefitsConfigDTO out = new GatewayBenefitsConfigDTO();
        out.setChannelId(in.getChannelId());
        out.setGatewaySid(in.getGatewaySid());
        out.setConfSid(in.getConfSid());
        out.setEventId(in.getEventId());

        if (in.getBenefits() != null) {
            List<BenefitDTO> benefitDTOs = in.getBenefits().stream()
                    .map(GatewayBenefitsConfigConverter::convert)
                    .collect(Collectors.toList());
            out.setBenefits(benefitDTOs);
        }

        return out;
    }

    public static List<GatewayBenefitsConfigDTO> convert(List<GatewayBenefitConfiguration> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(GatewayBenefitsConfigConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static GatewayBenefitConfiguration convert(Long channelId, Long eventId, String gatewaySid, String confSid, GatewayBenefitsConfigRequest in) {
        if (in == null) {
            return null;
        }

        GatewayBenefitConfiguration entity = new GatewayBenefitConfiguration();
        entity.setChannelId(channelId);
        entity.setGatewaySid(gatewaySid);
        entity.setConfSid(confSid);
        entity.setEventId(eventId);

        if (in.getBenefits() != null) {
            List<Benefit> benefits = in.getBenefits().stream()
                    .map(GatewayBenefitsConfigConverter::convert)
                    .collect(Collectors.toList());
            entity.setBenefits(benefits);
        }

        return entity;
    }

    private static BenefitDTO convert(Benefit in) {
        if (in == null) {
            return null;
        }

        BenefitDTO out = new BenefitDTO();
        out.setType(in.getType());

        if (in.getBinGroups() != null) {
            List<BinGroupDTO> binGroupDTOs = in.getBinGroups().stream()
                    .map(GatewayBenefitsConfigConverter::convert)
                    .collect(Collectors.toList());
            out.setBinGroups(binGroupDTOs);
        }

        if (CollectionUtils.isNotEmpty(in.getBrandGroups())) {
            List<BrandGroupDTO> brandGroupDTOs = in.getBrandGroups().stream()
                    .map(GatewayBenefitsConfigConverter::convert)
                    .collect(Collectors.toList());
            out.setBrandGroups(brandGroupDTOs);
        }

        return out;
    }

    private static Benefit convert(BenefitDTO in) {
        if (in == null) {
            return null;
        }

        Benefit out = new Benefit();
        out.setType(in.getType());

        if (CollectionUtils.isNotEmpty(in.getBinGroups())) {
            List<BinGroup> binGroups = in.getBinGroups().stream()
                    .map(GatewayBenefitsConfigConverter::convert)
                    .collect(Collectors.toList());
            out.setBinGroups(binGroups);
        }

        if (CollectionUtils.isNotEmpty(in.getBrandGroups())) {
            List<BrandGroup> brandGroups = in.getBrandGroups().stream()
                    .map(GatewayBenefitsConfigConverter::convert)
                    .collect(Collectors.toList());
            out.setBrandGroups(brandGroups);
        }

        return out;
    }

    private static BinGroupDTO convert(BinGroup in) {
        if (in == null) {
            return null;
        }
        BinGroupDTO out = new BinGroupDTO();
        fillGenericFieldsOnDTO(out, in);
        out.setBins(in.getBins());
        return out;
    }

    private static BinGroup convert(BinGroupDTO in) {
        if (in == null) {
            return null;
        }

        BinGroup out = new BinGroup();
        out.setBins(in.getBins());
        fillGenericFieldsFromDTO(out, in);

        return out;
    }

    private static BrandGroupDTO convert(BrandGroup in) {
        if (in == null) {
            return null;
        }
        BrandGroupDTO out = new BrandGroupDTO();
        fillGenericFieldsOnDTO(out, in);
        out.setBrands(in.getBrands());
        return out;
    }

    private static BrandGroup convert(BrandGroupDTO in) {
        if (in == null) {
            return null;
        }

        BrandGroup out = new BrandGroup();
        out.setBrands(in.getBrands());
        fillGenericFieldsFromDTO(out, in);

        return out;
    }

    private static void fillGenericFieldsFromDTO(BenefitGroupConfig out, BenefitGroupConfigDTO dto) {
        out.setValidityPeriod(convert(dto.getValidityPeriod()));
        out.setInstallmentOptions(dto.getInstallmentOptions());
        out.setCheckoutCommunicationElements(convert(dto.getCheckoutCommunicationElements()));
    }

    private static void fillGenericFieldsOnDTO(BenefitGroupConfigDTO dto, BenefitGroupConfig in) {
        dto.setValidityPeriod(convert(in.getValidityPeriod()));
        dto.setInstallmentOptions(in.getInstallmentOptions());
        dto.setCheckoutCommunicationElements(convert(in.getCheckoutCommunicationElements()));
    }

    public static ValidityPeriodDTO convert(ValidityPeriod in) {
        if (in == null) {
            return null;
        }

        ValidityPeriodDTO out = new ValidityPeriodDTO();
        out.setStartDate(in.getStartDate());
        out.setEndDate(in.getEndDate());
        return out;
    }

    public static ValidityPeriod convert(ValidityPeriodDTO in) {
        if (in == null) {
            return null;
        }

        ValidityPeriod out = new ValidityPeriod();
        out.setStartDate(in.getStartDate());
        out.setEndDate(in.getEndDate());
        return out;
    }

    public static BadgeDTO convert(Badge in) {
        if (in == null) {
            return null;
        }

        BadgeDTO out = new BadgeDTO();
        out.setText(in.getText());
        out.setTextColor(in.getTextColor());
        out.setBackgroundColor(in.getBackgroundColor());
        return out;
    }

    public static Badge convert(BadgeDTO in) {
        if (in == null) {
            return null;
        }

        Badge out = new Badge();
        out.setText(in.getText());
        out.setTextColor(in.getTextColor());
        out.setBackgroundColor(in.getBackgroundColor());
        return out;
    }

    public static CheckoutCommunicationElementsDTO convert(CheckoutCommunicationElements in) {
        if (in == null) {
            return null;
        }

        CheckoutCommunicationElementsDTO out = new CheckoutCommunicationElementsDTO();
        out.setDescription(in.getDescription());
        out.setBadge(convert(in.getBadge()));
        return out;
    }

    public static CheckoutCommunicationElements convert(CheckoutCommunicationElementsDTO in) {
        if (in == null) {
            return null;
        }

        CheckoutCommunicationElements out = new CheckoutCommunicationElements();
        out.setDescription(in.getDescription());
        out.setBadge(convert(in.getBadge()));
        return out;
    }
}
