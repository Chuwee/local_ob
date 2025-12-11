package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsChannelSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresaleLoyaltyProgram;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.seasontickets.dto.CreateSeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPresaleDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleChannelDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleCustomerTypeDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleLoyaltyProgramDTO;
import es.onebox.mgmt.sessions.dto.SessionPresalePeriodDTO;
import es.onebox.mgmt.sessions.enums.PresaleStatus;
import es.onebox.mgmt.sessions.enums.PresaleValidationRangeType;
import es.onebox.mgmt.sessions.enums.PresaleValidatorType;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class SeasonTicketPresalesConverter {

    private SeasonTicketPresalesConverter() {
    }

    public static PreSaleConfigDTO toMsEvent(CreateSeasonTicketPresaleDTO in) {
        PreSaleConfigDTO out = new PreSaleConfigDTO();

        out.setName(in.getName());
        out.setValidatorType(PresaleValidatorType.CUSTOMERS);
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


    public static PreSaleConfigDTO toMsEvent(UpdateSeasonTicketPresaleDTO in, MsSaleRequestsResponseDTO saleRequests, CustomerTypes customerTypes) {
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

    public static SeasonTicketPresaleDTO toDTO(PreSaleConfigDTO dto, Set<IdNameDTO> channels, Set<IdNameDTO> customerTypes, EntityDTO entity) {
        SeasonTicketPresaleDTO out = new SeasonTicketPresaleDTO();
        out.setId(dto.getId());
        out.setActive(dto.getActive());
        out.setStatus(toSeasonTicketPresaleStatus(dto));
        out.setName(dto.getName());
        SessionPresalePeriodDTO presalePeriodDTO = new SessionPresalePeriodDTO();
        presalePeriodDTO.setType(dto.getValidationRangeType());
        presalePeriodDTO.setStartDate(dto.getStartDate());
        presalePeriodDTO.setEndDate(dto.getEndDate());
        out.setPresalePeriod(presalePeriodDTO);
        out.setMemberTicketsLimitEnabled(dto.getMemberTicketsLimitEnabled());
        out.setMemberTicketsLimit(dto.getMemberTicketsLimit());
        out.setGeneralTicketsLimit(dto.getGeneralTicketsLimit());
        out.setChannels(toSeasonTicketPresaleChannels(dto, channels));
        out.setCustomerTypes(toSeasonTicketPresaleCustomerTypes(dto, customerTypes));
        out.setLoyaltyProgram(toSeasonTicketPresaleLoyaltyProgram(dto, entity));
        return out;
    }

    private static PresaleStatus toSeasonTicketPresaleStatus(PreSaleConfigDTO dto) {
        if (BooleanUtils.isNotTrue(dto.getActive())) {
            return PresaleStatus.INACTIVE;
        }
        if (PresaleValidationRangeType.ALL.equals(dto.getValidationRangeType())) {
            return PresaleStatus.ACTIVE;
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isBefore(dto.getEndDate()) && now.isAfter(dto.getStartDate())) {
            return PresaleStatus.ACTIVE;
        }
        return PresaleStatus.PLANNED;
    }

    private static SessionPreSaleChannelDTO toSeasonTicketPresaleChannels(PreSaleConfigDTO dto, IdNameDTO d) {
        SessionPreSaleChannelDTO channel = new SessionPreSaleChannelDTO();
        channel.setId(d.getId());
        channel.setName(d.getName());
        channel.setSelected(dto != null && dto.getActiveChannels() != null && dto.getActiveChannels().contains(d.getId()));
        return channel;
    }

    private static List<SessionPreSaleChannelDTO> toSeasonTicketPresaleChannels(PreSaleConfigDTO dto, Collection<IdNameDTO> channels) {
        if (channels == null) {
            return Collections.emptyList();
        }
        return channels.stream()
                .map(channel -> toSeasonTicketPresaleChannels(dto, channel))
                .toList();

    }

    private static SessionPreSaleCustomerTypeDTO toSeasonTicketPresaleCustomerType(PreSaleConfigDTO dto, IdNameDTO d) {
        SessionPreSaleCustomerTypeDTO customerType = new SessionPreSaleCustomerTypeDTO();
        customerType.setId(d.getId());
        customerType.setName(d.getName());
        customerType.setSelected(dto != null && dto.getActiveCustomerTypes() != null && dto.getActiveCustomerTypes().contains(d.getId()));
        return customerType;
    }

    private static List<SessionPreSaleCustomerTypeDTO> toSeasonTicketPresaleCustomerTypes(PreSaleConfigDTO dto, Collection<IdNameDTO> customerTypes) {
        if (customerTypes == null) {
            return Collections.emptyList();
        }
        return customerTypes.stream()
                .map(customerType -> toSeasonTicketPresaleCustomerType(dto, customerType))
                .toList();

    }

    private static SessionPreSaleLoyaltyProgramDTO toSeasonTicketPresaleLoyaltyProgram(PreSaleConfigDTO dto, EntityDTO entity) {
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
