package es.onebox.mgmt.b2b.clients.converter;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.mgmt.b2b.clients.dto.BaseClientDTO;
import es.onebox.mgmt.b2b.clients.dto.BaseClientRequestDTO;
import es.onebox.mgmt.b2b.clients.dto.ClientDTO;
import es.onebox.mgmt.b2b.clients.dto.ClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.ContactDataDTO;
import es.onebox.mgmt.b2b.clients.dto.CreateClientDTO;
import es.onebox.mgmt.b2b.clients.dto.CreateClientUserDTO;
import es.onebox.mgmt.b2b.clients.dto.DirectoryClientDTO;
import es.onebox.mgmt.b2b.clients.dto.DirectoryClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.SearchClientsFilterDTO;
import es.onebox.mgmt.b2b.clients.dto.UpdateClientDTO;
import es.onebox.mgmt.b2b.clients.enums.ClientCategoryType;
import es.onebox.mgmt.b2b.clients.enums.ClientStatus;
import es.onebox.mgmt.b2b.clients.enums.ClientType;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientB2B;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientB2BBranch;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientEntity;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUser;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SearchClientsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SortableElement;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClientsConverter {

    private ClientsConverter() {}

    public static ClientsDTO toDTO(Clients source, SearchClientsFilterDTO filter,
                                   EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        ClientsDTO target = new ClientsDTO();
        target.setMetadata(new Metadata());
        target.getMetadata().setTotal(NumberUtils.zeroIfNull(source.getTotalElements()));
        target.getMetadata().setLimit(filter.getLimit());
        target.getMetadata().setOffset(filter.getOffset());
        target.setData(source.getClientList().stream()
                .map(elem -> (BaseClientDTO) toDTOBase(elem, entitiesRepository, masterdataService)).toList());
        return target;
    }

    public static ClientDTO toDTO(Client client, EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        ClientDTO target = toDTOBase(client, entitiesRepository, masterdataService);
        target.setContactData(getContactData(client));

        ClientB2B clientB2B = client.getClientB2B();
        if (clientB2B != null) {
            target.setIataCode(clientB2B.getIataCode());
            target.setDescription(clientB2B.getDescription());
            target.setKeywords(Arrays.stream(Optional.ofNullable(clientB2B.getTags()).orElse("")
                                                     .split(","))
                                .filter(StringUtils::isNotBlank)
                                .map(String::trim)
                                .toList());
        }
        return target;
    }

    public static DirectoryClientsDTO toDTODirectory(Clients source, SearchClientsFilterDTO filter, MasterdataService masterdataService) {
        DirectoryClientsDTO target = new DirectoryClientsDTO();
        target.setMetadata(new Metadata());
        target.getMetadata().setTotal(NumberUtils.zeroIfNull(source.getTotalElements()));
        target.getMetadata().setLimit(filter.getLimit());
        target.getMetadata().setOffset(filter.getOffset());
        target.setData(source.getClientList().stream().map(elem -> toDTODirectory(elem, masterdataService)).toList());
        return target;
    }

    private static DirectoryClientDTO toDTODirectory(Client client, MasterdataService masterdataService) {
        DirectoryClientDTO target = new DirectoryClientDTO();
        target.setId(client.getId().longValue());
        target.setName(client.getName());
        target.setIataCode(client.getClientB2B().getIataCode());
        target.setCountry(fillCodeName(masterdataService.getCountryByCode(client.getCountry())));
        target.setCategoryType(ClientCategoryType.fromId(client.getClientB2B().getClientCategoryId()));
        target.setTaxId(client.getClientB2B().getTaxId());
        return target;
    }

    private static ContactDataDTO getContactData(Client client) {
        ContactDataDTO contactData = new ContactDataDTO();
        contactData.setAddress(client.getAddress());
        contactData.setPhone(client.getContactPhone());
        contactData.setContactPerson(client.getContactName());
        contactData.setEmail(client.getContactEmail());
       return contactData;
    }

    private static CodeNameDTO fillCodeName(MasterdataValue source) {
        CodeNameDTO target = new CodeNameDTO();
        target.setCode(source.getCode());
        target.setName(source.getName());
        return target;
    }

    public static ClientDTO toDTOBase(Client client, EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        ClientDTO target = new ClientDTO();
        target.setId(client.getId().longValue());
        target.setName(client.getName());
        target.setCountry(fillCodeName(masterdataService.getCountryByCode(client.getCountry())));
        target.setCountrySubdivision(fillCodeName(masterdataService.getCountrySubdivisionByCode(client.getCountrySubdivision())));
        ClientB2B clientB2B = client.getClientB2B();
        if (clientB2B != null) {
            if (clientB2B.getClientEntity() != null) {
                target.setClientId(clientB2B.getClientEntity().getId().longValue());
                target.setCreationDate(clientB2B.getClientEntity().getCreatedAt());
                target.setStatus(ClientStatus.fromBoolean(clientB2B.getClientEntity().getActive()));
                if (clientB2B.getClientEntity().getEntityId() != null) {
                    Entity entity = entitiesRepository.getCachedEntity(clientB2B.getClientEntity().getEntityId().longValue());
                    target.setEntity(new IdNameDTO());
                    target.getEntity().setId(entity.getId());
                    target.getEntity().setName(entity.getName());
                }
            }
            target.setCategoryType(ClientCategoryType.fromId(clientB2B.getClientCategoryId()));
            target.setTaxId(clientB2B.getTaxId());
            target.setBusinessName(clientB2B.getBusinessName());
        }
        return target;
    }

    public static SearchClientsFilter toMs(Long entityId, SearchClientsFilterDTO source) {
        SearchClientsFilter target = new SearchClientsFilter();
        target.setEntityId(entityId.intValue());
        target.setKeyword(source.getQ());
        target.setFrom(source.getOffset().intValue());
        target.setAmount(source.getLimit().intValue());
        target.setClientTypeId(ClientType.CLIENT_B2B.getId());
        target.setCountAllElements(Boolean.TRUE);
        target.setActive(Boolean.TRUE);
        if (source.getSort() != null && !source.getSort().getSortDirections().isEmpty()) {
            SortDirection<String> sort = source.getSort().getSortDirections().get(0);
            target.setSortAsc(sort.getDirection().equals(Direction.ASC));
            target.setSortBy(SortableElement.fromValue(sort.getValue()));
        }
        return target;
    }

    public static SearchClientsFilter toMsDirectory(Long entityId, SearchClientsFilterDTO source) {
        SearchClientsFilter target = toMs(entityId, source);
        target.setInvertEntityIdFilter(Boolean.TRUE);
        return target;
    }

    public static SearchClientsFilter getClientByTaxIdFilter(Long entityId, String taxId) {
        SearchClientsFilter target = new SearchClientsFilter();
        target.setEntityId(entityId != null ? entityId.intValue(): null);
        target.setTaxId(taxId);
        target.setClientTypeId(ClientType.CLIENT_B2B.getId());
        target.setCountAllElements(Boolean.TRUE);
        target.setActive(Boolean.TRUE);
        return target;
    }

    public static Client toMsUpdate(UpdateClientDTO source, Long clientId) {
        Client target = toMs(source);
        target.setId(clientId.intValue());
        if (source.getContactData() != null) {
            target.setContactEmail(source.getContactData().getEmail());
            target.setContactName(source.getContactData().getContactPerson());
            target.setContactPhone(source.getContactData().getPhone());
        }
        if (source.getKeywords() != null) {
            target.getClientB2B().setTags(String.join(", ", source.getKeywords()));
        }
        target.getClientB2B().setDescription(source.getDescription());
        return target;
    }

    public static Client toMsCreate(CreateClientDTO source) {
        Client target = toMs(source);
        if (source.getContactData() != null) {
            target.setContactEmail(source.getContactData().getEmail());
            target.setContactName(source.getContactData().getContactPerson());
            target.setContactPhone(source.getContactData().getPhone());
        }
        if (source.getKeywords() != null) {
            target.getClientB2B().setTags(String.join(", ", source.getKeywords()));
        }
        target.getClientB2B().setDescription(source.getDescription());
        target.getClientB2B().setIataCode(source.getIataCode());
        target.setUsers(List.of(toMsClientUser(source.getUser())));
        return target;
    }

    public static Client toMs(BaseClientRequestDTO source) {
        Client target = new Client();
        target.setTypeId(ClientType.CLIENT_B2B.getId());
        target.setName(source.getName());
        if (source.getContactData() != null) {
            target.setAddress(source.getContactData().getAddress());
        }
        if (source.getCountry() != null) {
            target.setCountry(source.getCountry().getCode());
        }
        if (source.getCountrySubdivision() != null) {
            target.setCountrySubdivision(source.getCountrySubdivision().getCode());
        }
        target.setClientB2B(toMsClientB2B(source));
        return target;
    }

    private static ClientB2B toMsClientB2B(BaseClientRequestDTO source) {
        ClientB2B target = new ClientB2B();
        target.setBusinessName(source.getBusinessName());
        if (source.getCategoryType() != null) {
            target.setClientCategoryId(source.getCategoryType().getId());
        }
        target.setTaxId(source.getTaxId());
        target.setClientB2BBranches(List.of(toMsB2BBranch(source)));
        return target;
    }

    private static ClientB2BBranch toMsB2BBranch(BaseClientRequestDTO source) {
        ClientB2BBranch target = new ClientB2BBranch();
        if (source.getCountry() != null) {
            target.setCountry(source.getCountry().getCode());
        }
        if (source.getCountrySubdivision() != null) {
            target.setCountrySubdivision(source.getCountrySubdivision().getCode());
        }
        if (source.getContactData() != null) {
            target.setAddress(source.getContactData().getAddress());
            target.setContactEmail(source.getContactData().getEmail());
            target.setContactName(source.getContactData().getContactPerson());
            target.setContactPhone(source.getContactData().getPhone());
        }
        target.setMain(Boolean.TRUE);
        return target;
    }

    private static ClientUser toMsClientUser(CreateClientUserDTO source) {
        ClientUser target = new ClientUser();
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setUsername(source.getUsername());
        return target;
    }

    public static ClientEntity toMsClientEntity(Long entityId, Long clientId){
        ClientEntity clientEntity = new ClientEntity();
            clientEntity.setClientId(clientId.intValue());
            clientEntity.setEntityId(entityId.intValue());
        return clientEntity;
    }
}
