package es.onebox.eci.organizations.converter;

import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventEntity;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.eci.organizations.dto.Brand;
import es.onebox.eci.organizations.dto.Address;
import es.onebox.eci.organizations.dto.Organization;
import es.onebox.eci.organizations.dto.OrganizationType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrganizationConverter {

    private OrganizationConverter() {
    }

    public static Set<Organization> getOrganizations(List<ChannelEventDetail> channelEventDetails, ChannelDTO channel,
                                                      ChannelConfigDTO channelConfig, List<ChannelDTO> channels) {
        Set<Organization> organizations = new HashSet<>();
        List<Brand> brands = channels.stream().map(OrganizationConverter::fillBrand).collect(Collectors.toList());
        boolean channelEntityAdded = false;
        if (CollectionUtils.isNotEmpty(channelEventDetails)) {
            for (ChannelEventDetail channelEventDetail : channelEventDetails) {
                // Adds the entity organization with sponsor type
                Organization organization = convert(channelEventDetail.getEntity(), OrganizationType.SPONSOR);

                if (organization.getTaxId().equals(channelConfig.getEntityNif())) {
                    // if the channel entity is the same, also add the provider type
                    organization.getOrganizationTypes().add(OrganizationType.PROVIDER);
                    organization.setBrands(brands);
                    channelEntityAdded = true;
                }
                organizations.add(organization);

                // Adds the producer organization with organizer type
                organizations.add(convert(channelEventDetail.getPromoter(), OrganizationType.ORGANIZER));
            }
        }

        // Only adds the channel entity if is not previously added
        if (!channelEntityAdded) {
            organizations.add(convert(fillEntity(channel, channelConfig), OrganizationType.PROVIDER, brands));
        }

        return organizations;
    }

    public static Organization convert(ChannelEventEntity entity, OrganizationType organizationType) {
        if (entity == null) {
            return null;
        }

        Organization organization = new Organization();
        organization.setIdentifier(buildOrganizationIdentifier(entity.getId(), organizationType));
        organization.setName(entity.getFiscalName());
        organization.setTaxId(entity.getFiscalIdentifier());
        organization.setOrganizationTypes(new HashSet<>(List.of(organizationType)));
        organization.setAddress(fillAddress(entity));
        return organization;
    }

    public static Organization convert(ChannelEventEntity entity, OrganizationType organizationType, List<Brand> brands) {
        if (entity == null) {
            return null;
        }

        Organization organization = new Organization();
        organization.setIdentifier(buildOrganizationIdentifier(entity.getId(), organizationType));
        organization.setName(entity.getName());
        organization.setTaxId(entity.getFiscalIdentifier());
        organization.setOrganizationTypes(new HashSet<>(List.of(organizationType)));
        organization.setBrands(brands);
        organization.setAddress(fillAddress(entity));
        return organization;
    }

    public static ChannelEventEntity fillEntity(ChannelDTO channel, ChannelConfigDTO channelConfig) {
        ChannelEventEntity entity = new ChannelEventEntity();
        entity.setId(channel.getEntityId());
        entity.setName(channel.getEntityName());
        entity.setFiscalIdentifier(channelConfig.getEntityNif());

        return entity;
    }

    public static Brand fillBrand(ChannelDTO channel) {
        Brand brand = new Brand();
        brand.setIdentifier(channel.getId().toString());
        brand.setName(channel.getName());
        return brand;
    }

    private static Address fillAddress(ChannelEventEntity entity) {
        Address address = new Address();
        address.setFullAddress(entity.getAddress());
        if (entity.getCountry() != null) {
            address.setAddressCountry(entity.getCountry().getCode());
        }
        return address;
    }

    private static String buildOrganizationIdentifier(Long id, OrganizationType organizationType) {
        return String.format("%s%s", organizationType.name().charAt(0), id);
    }
}
