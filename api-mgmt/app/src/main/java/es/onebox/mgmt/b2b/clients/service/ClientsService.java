package es.onebox.mgmt.b2b.clients.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.b2b.clients.converter.ClientsConverter;
import es.onebox.mgmt.b2b.clients.dto.ClientDTO;
import es.onebox.mgmt.b2b.clients.dto.ClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.CreateClientDTO;
import es.onebox.mgmt.b2b.clients.dto.CreateDirectoryClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.DirectoryClientsDTO;
import es.onebox.mgmt.b2b.clients.dto.SearchClientsFilterDTO;
import es.onebox.mgmt.b2b.clients.dto.UpdateClientDTO;
import es.onebox.mgmt.b2b.users.converter.ClientUserConverter;
import es.onebox.mgmt.b2b.utils.B2BUtilsService;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientEntity;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SearchClientsFilter;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtCustomersErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientsService {

    private final B2BUtilsService b2bUtilsService;
    private final ClientsRepository clientsRepository;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ClientsService(B2BUtilsService b2bUtilsService, ClientsRepository clientsRepository,
                          EntitiesRepository entitiesRepository,
                          MasterdataService masterdataService) {
        this.b2bUtilsService = b2bUtilsService;
        this.clientsRepository = clientsRepository;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }

    public ClientsDTO searchClients(SearchClientsFilterDTO filter) {
        b2bUtilsService.validateEntity(filter);
        Clients response = clientsRepository.getClients(ClientsConverter.toMs(filter.getEntityId(), filter));
        return ClientsConverter.toDTO(response, filter, entitiesRepository, masterdataService);
    }

    public ClientDTO getClient(Long clientId, Long entityId) {
        Client client = validateClient(clientId, entityId);
        return ClientsConverter.toDTO(client, entitiesRepository, masterdataService);
    }

    public IdDTO createClient(CreateClientDTO createClient) {
        Long entityId = b2bUtilsService.validateEntity(createClient.getEntityId());
        validateCreation(createClient, entityId);
        Client newClient = ClientsConverter.toMsCreate(createClient);

        // Search directory client with same taxID
        SearchClientsFilter directoryFilter = ClientsConverter.getClientByTaxIdFilter(entityId, createClient.getTaxId());
        Clients directoryClients = clientsRepository.getClients(directoryFilter);

        // If TaxID is already used in the directory then create clientEntity
        if (CollectionUtils.isNotEmpty(directoryClients.getClientList())) {
            Client directoryClient = directoryClients.getClientList().get(0);
            ClientEntity clientEntity = ClientsConverter.toMsClientEntity(entityId, directoryClient.getId().longValue());
            clientsRepository.createClientEntity(List.of(clientEntity));
            newClient.setId(directoryClient.getId());

            // create a new user if the provided one does not exist in the directory
            if (clientsRepository.getClientUsersByUsername(createClient.getUser().getUsername()).isEmpty()) {
                clientsRepository.upsertClientUser(ClientUserConverter.toMs(createClient.getUser(),
                        directoryClients.getClientList().get(0).getId()), entityId, null);
            }
        }

        return new IdDTO(clientsRepository.upsertClient(newClient, entityId).getId().longValue());
    }

    public void updateClient(Long clientId, UpdateClientDTO updateClient) {
        Long entityId = b2bUtilsService.validateEntity(updateClient.getEntityId());
        validateClient(clientId, entityId);
        //Validate taxId is unique in the directory
        checkUniqueTaxId(updateClient.getTaxId(), entityId, clientId);
        Client updatedClient = ClientsConverter.toMsUpdate(updateClient, clientId);
        clientsRepository.upsertClient(updatedClient, entityId);
    }

    public void deleteClient(Long clientId, Long entityId) {
        clientsRepository.deleteClient(clientId, b2bUtilsService.validateEntity(entityId));
    }

    public DirectoryClientsDTO searchClientsDirectory(SearchClientsFilterDTO filter) {
        b2bUtilsService.validateEntity(filter);
        Clients response = clientsRepository.getClients(ClientsConverter.toMsDirectory(filter.getEntityId(), filter));
        return ClientsConverter.toDTODirectory(response, filter, masterdataService);
    }

    public void createDirectoryClients(CreateDirectoryClientsDTO createDirectoryClients) {
        Long entityId = b2bUtilsService.validateEntity(createDirectoryClients.getEntityId());
        List<ClientEntity> clientEntities = new ArrayList<>();
        List<Client> directoryClients = new ArrayList<>();

        createDirectoryClients.getClients().forEach(client -> {
            clientEntities.add(ClientsConverter.toMsClientEntity(entityId, client.getId()));
            directoryClients.add(clientsRepository.getClient(client.getId(), null));
        });
        clientsRepository.createClientEntity(clientEntities);
        directoryClients.forEach(directoryClient ->
                clientsRepository.upsertClient(directoryClient, entityId)
        );
    }

    public Client validateClient(Long clientId, Long entityId) {
        Client client = clientsRepository.getClient(clientId, b2bUtilsService.validateEntity(entityId));
        if (client.getClientB2B() == null || client.getClientB2B().getClientEntity() == null) {
            throw new OneboxRestException(ApiMgmtCustomersErrorCode.CLIENT_NOT_FOUND);
        }
        return client;
    }

    private void validateCreation(CreateClientDTO createClient, Long entityId) {
        if (createClient.getCategoryType() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "category_type can not be null", null);
        }
        if (createClient.getName() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "name can not be null", null);
        }
        if (createClient.getTaxId() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "tax_id can not be null", null);
        }
        if (createClient.getBusinessName() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "business_name can not be null", null);
        }
        if (createClient.getCountry() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "country can not be null", null);
        }
        if (createClient.getCountrySubdivision() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "country_subdivision can not be null", null);
        }
        if (createClient.getContactData() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "contact_data can not be null", null);
        }
        // Validates the taxId in the entity
        checkUniqueTaxId(createClient.getTaxId(), entityId, null);
    }

    /**
     * Validates if taxId is already in use in any B2B client of the entity
     *
     * @param entityId
     * @param currentClientId Only required in update validation
     */
    private void checkUniqueTaxId(String taxId, Long entityId, Long currentClientId) {
        SearchClientsFilter filter = ClientsConverter.getClientByTaxIdFilter(entityId, taxId);
        Clients response = clientsRepository.getClients(filter);

        boolean taxIdInUse = response.getClientList().stream()
                .anyMatch(client -> taxId != null && taxId.equals(client.getClientB2B().getTaxId()) &&
                        (currentClientId == null || !currentClientId.equals(client.getId().longValue())));

        if (taxIdInUse) {
            throw new OneboxRestException(ApiMgmtErrorCode.CLIENT_TAX_ID_ALREADY_IN_USE);
        }
    }
}
