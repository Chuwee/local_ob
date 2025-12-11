package es.onebox.mgmt.sessions.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsChannelSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresaleLoyaltyProgram;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionPreSaleDTO;
import es.onebox.mgmt.sessions.dto.PresaleSettingsDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleChannelDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleCustomerTypeDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleLoyaltyProgramDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleValidatorDTO;
import es.onebox.mgmt.sessions.dto.SessionPresalePeriodDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionPreSaleDTO;
import es.onebox.mgmt.sessions.enums.PresaleValidationMethod;
import es.onebox.mgmt.sessions.enums.PresaleValidatorType;
import es.onebox.mgmt.sessions.presales.utils.PresalesUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class SessionPreSaleConverter {

    public static PreSaleConfigDTO toMsEvent(CreateSessionPreSaleDTO in) {
        PreSaleConfigDTO out = new PreSaleConfigDTO();

        out.setName(in.getName());
        out.setValidatorId(in.getValidatorId());
        out.setValidatorType(in.getValidatorType());
        if (in.getAdditionalConfig() != null) {
            if (in.getAdditionalConfig().getExternalPresaleId() != null) {
                out.setExternalId(in.getAdditionalConfig().getExternalPresaleId());
            }
            if (in.getAdditionalConfig().getEntityId() != null) {
                out.setEntityId(in.getAdditionalConfig().getEntityId());
            }
        }

        return out;
    }

    public static PreSaleConfigDTO toMsEvent(UpdateSessionPreSaleDTO in, MsSaleRequestsResponseDTO saleRequests,
                                             CustomerTypes customerTypes) {
        PreSaleConfigDTO out = new PreSaleConfigDTO();

        out.setActive(in.getActive());
        out.setName(in.getName());
        if (in.getPresalePeriod() != null) {
            out.setValidationRangeType(in.getPresalePeriod().getType());
            out.setStartDate(in.getPresalePeriod().getStartDate());
            out.setEndDate(in.getPresalePeriod().getEndDate());
        }

        out.setMemberTicketsLimitEnabled(in.getMemberTicketsLimitEnabled());
        out.setMemberTicketsLimit(in.getMemberTicketsLimit());
        out.setGeneralTicketsLimit(in.getGeneralTicketsLimit());
        if (in.getPresaleSettingsDTO() != null) {
            if (in.getPresaleSettingsDTO().getMultiplePurchase() != null) {
                out.setMultiplePurchase(in.getPresaleSettingsDTO().getMultiplePurchase());
            }
        }

        if (CollectionUtils.isEmpty(in.getChannels()) || isNull(saleRequests) || CollectionUtils.isEmpty(saleRequests.getData())) {
            if (!isNull(in.getChannels())) {
                out.setActiveChannels(new ArrayList<>());
            }
        } else {
            Set<Long> saleChannelIds = saleRequests.getData().stream()
                    .map(MsSaleRequestDTO::getChannel)
                    .map(MsChannelSaleRequestDTO::getId)
                    .collect(Collectors.toSet());

            out.setActiveChannels(in.getChannels().stream()
                    .filter(id -> BooleanUtils.isNotFalse(saleChannelIds.contains(id)))
                    .collect(Collectors.toList()));
        }

        if (CollectionUtils.isEmpty(in.getCustomerTypes()) || isNull(customerTypes) || CollectionUtils.isEmpty(customerTypes.getData())) {
            if (!isNull(in.getCustomerTypes())) {
                out.setActiveCustomerTypes(new ArrayList<>());
            }
        } else {
            Set<Long> customerTypeIds = customerTypes.getData().stream()
                    .map(CustomerType::getId)
                    .collect(Collectors.toSet());

            out.setActiveCustomerTypes(in.getCustomerTypes().stream()
                    .filter(id -> BooleanUtils.isNotFalse(customerTypeIds.contains(id)))
                    .collect(Collectors.toList()));
        }

        if (in.getLoyaltyProgram() != null) {
            PresaleLoyaltyProgram loyaltyProgram = new PresaleLoyaltyProgram();
            loyaltyProgram.setEnabled(in.getLoyaltyProgram().getEnabled());
            loyaltyProgram.setPoints(in.getLoyaltyProgram().getPoints());
            out.setLoyaltyProgram(loyaltyProgram);
        }
        return out;
    }

    public static SessionPreSaleDTO toDTO(PreSaleConfigDTO dto, Set<IdNameDTO> channels,
                                          MsCollectiveDetailDTO collective, Set<IdNameDTO> customerTypes,
                                          EntityDTO entity, Boolean isSmartBooking) {
        SessionPreSaleDTO out = new SessionPreSaleDTO();
        out.setId(dto.getId());
        out.setActive(dto.getActive());
        out.setStatus(PresalesUtils.getPresaleStatus(dto));
        out.setName(dto.getName());
        SessionPresalePeriodDTO presalePeriodDTO = new SessionPresalePeriodDTO();
        presalePeriodDTO.setType(dto.getValidationRangeType());
        presalePeriodDTO.setStartDate(dto.getStartDate());
        presalePeriodDTO.setEndDate(dto.getEndDate());
        out.setPresalePeriod(presalePeriodDTO);
        out.setValidatorType(dto.getValidatorType());
        SessionPreSaleValidatorDTO sessionPreSaleCollectiveDTO = new SessionPreSaleValidatorDTO();
        sessionPreSaleCollectiveDTO.setId(dto.getValidatorId());
        if (PresaleValidatorType.COLLECTIVE.equals(dto.getValidatorType()) && collective != null) {
            sessionPreSaleCollectiveDTO.setName(collective.getName());
            sessionPreSaleCollectiveDTO.setValidationMethod(PresaleValidationMethod.getByName(collective.getValidationMethod().name()));
        }
        out.setValidator(sessionPreSaleCollectiveDTO);
        out.setMemberTicketsLimitEnabled(dto.getMemberTicketsLimitEnabled());
        out.setMemberTicketsLimit(dto.getMemberTicketsLimit());
        out.setGeneralTicketsLimit(dto.getGeneralTicketsLimit());
        out.setChannels(toSessionPresaleChannels(dto, channels));
        out.setActive(dto.getActive());
        if (BooleanUtils.isTrue(isSmartBooking) && dto.getMultiplePurchase() != null) {
            if (out.getPresaleSettingsDTO() == null) {
                out.setPresaleSettingsDTO(new PresaleSettingsDTO());
            }
            out.getPresaleSettingsDTO().setMultiplePurchase(dto.getMultiplePurchase());
        }
        if (PresaleValidatorType.CUSTOMERS.equals(dto.getValidatorType())) {
            out.setCustomerTypes(toSessionPresaleCustomerTypes(dto, customerTypes));
            out.setLoyaltyProgram(toSessionPresaleLoyaltyProgram(dto, entity));
        }
        return out;
    }

    private static SessionPreSaleChannelDTO toSessionPresaleChannel(PreSaleConfigDTO dto, IdNameDTO d) {
        SessionPreSaleChannelDTO channel = new SessionPreSaleChannelDTO();
        channel.setId(d.getId());
        channel.setName(d.getName());
        channel.setSelected(dto != null && dto.getActiveChannels() != null && dto.getActiveChannels().contains(d.getId()));
        return channel;
    }

    private static List<SessionPreSaleChannelDTO> toSessionPresaleChannels(PreSaleConfigDTO dto, Collection<IdNameDTO> channels) {
        if (channels == null) {
            return Collections.emptyList();
        }
        return channels.stream()
                .map(channel -> toSessionPresaleChannel(dto, channel))
                .toList();

    }

    private static SessionPreSaleCustomerTypeDTO toSessionPresaleCustomerType(PreSaleConfigDTO dto, IdNameDTO d) {
        SessionPreSaleCustomerTypeDTO customerType = new SessionPreSaleCustomerTypeDTO();
        customerType.setId(d.getId());
        customerType.setName(d.getName());
        customerType.setSelected(dto != null && dto.getActiveCustomerTypes() != null && dto.getActiveCustomerTypes().contains(d.getId()));
        return customerType;
    }

    private static List<SessionPreSaleCustomerTypeDTO> toSessionPresaleCustomerTypes(PreSaleConfigDTO dto, Collection<IdNameDTO> customerTypes) {
        if (customerTypes == null) {
            return Collections.emptyList();
        }
        return customerTypes.stream()
                .map(customerType -> toSessionPresaleCustomerType(dto, customerType))
                .toList();

    }

    private static SessionPreSaleLoyaltyProgramDTO toSessionPresaleLoyaltyProgram(PreSaleConfigDTO dto, EntityDTO entity) {
        if (entity != null && entity.getSettings() != null && BooleanUtils.isTrue(entity.getSettings().getAllowLoyaltyPoints())) {
            SessionPreSaleLoyaltyProgramDTO loyaltyProgram = new SessionPreSaleLoyaltyProgramDTO();
            Optional.ofNullable(dto.getLoyaltyProgram()).ifPresentOrElse(it -> {
                loyaltyProgram.setPoints(it.getPoints());
                loyaltyProgram.setEnabled(BooleanUtils.isTrue(it.getEnabled()));
            }, () -> loyaltyProgram.setEnabled(Boolean.FALSE));
            return loyaltyProgram;
        }
        return null;
    }

}
