package es.onebox.mgmt.b2b.users.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.b2b.clients.service.ClientsService;
import es.onebox.mgmt.b2b.users.converter.ClientUserConverter;
import es.onebox.mgmt.b2b.users.dto.ClientSecretDTO;
import es.onebox.mgmt.b2b.users.dto.ClientUserResponseDTO;
import es.onebox.mgmt.b2b.users.dto.ClientUsersDTO;
import es.onebox.mgmt.b2b.users.dto.CreateClientUserRequestDTO;
import es.onebox.mgmt.b2b.users.dto.SearchClientUsersFilterDTO;
import es.onebox.mgmt.b2b.users.dto.UpdateClientUserRequestDTO;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientSecret;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUser;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClientUsersService {

    private final ClientsRepository clientsRepository;
    private final ClientsService clientsService;

    @Autowired
    public ClientUsersService(ClientsRepository clientsRepository, ClientsService clientsService) {
        this.clientsRepository = clientsRepository;
        this.clientsService = clientsService;
    }

    public ClientUsersDTO getClientUsers(Long clientId, SearchClientUsersFilterDTO filter) {
        clientsService.validateClient(clientId, filter.getEntityId());
        return ClientUserConverter.toDTO(clientsRepository.getClientUsers(clientId,
                filter.getQ(),
                filter.getOffset() == null ? null : filter.getOffset().intValue(),
                filter.getLimit() == null ? null : filter.getLimit().intValue(),
                true));
    }

    public ClientUserResponseDTO getClientUser(Long clientId, Long clientUserId, Long entityId) {
        clientsService.validateClient(clientId, entityId);
        return ClientUserConverter.toDTO(clientsRepository.getClientUser(clientUserId));
    }

    public IdDTO createClientUser(CreateClientUserRequestDTO clientUser, Long clientId) {
        if (clientUser == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "request body can't be empty", null);
        }
        clientsService.validateClient(clientId, clientUser.getEntityId() == null ? null : clientUser.getEntityId().longValue());

        if (CollectionUtils.isNotEmpty(clientsRepository.getClientUsersByUsername(clientUser.getUsername()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.USERNAME_CONFLICT, "client user can't be created with an already existing username", null);
        }

        Long entityId = clientUser.getEntityId() != null ? (long) clientUser.getEntityId() : SecurityUtils.getUserEntityId();
        ClientUser response = clientsRepository.upsertClientUser(ClientUserConverter.toMs(clientUser, clientId), entityId, null);
        return new ClientSecretDTO(response.getApiKey(), response.getId().longValue());
    }

    public void updateClientUser(UpdateClientUserRequestDTO clientUser, Long clientId, Long clientUserId) {
        if (clientUser == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "request body can't be empty", null);
        }
        Long clientEntity =  clientUser.getEntityId();
        clientsService.validateClient(clientId, clientEntity);
        ClientUser existingClientUser = clientsRepository.getClientUser(clientUserId);
        if (existingClientUser == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_FOUND, "no client user was found with the given client user id", null);
        }
        ClientUser msClientUser = ClientUserConverter.toMs(clientUser, clientId, existingClientUser.getUsername());
        msClientUser.setId(clientUserId.intValue());
        clientsRepository.upsertClientUser(msClientUser, clientEntity, Boolean.FALSE);
    }

    public void resetPassword(Long clientId, Long clientUserId, Long entityId) {
        Client client = clientsService.validateClient(clientId, entityId);
        ClientUser existingClientUser = clientsRepository.getClientUser(clientUserId);
        if (existingClientUser == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_FOUND, "no client user was found with the given client user id", null);
        }
        existingClientUser.setClientId(clientId.intValue());
        existingClientUser.setId(clientUserId.intValue());
        if (entityId == null) {
            entityId = client.getClientB2B().getClientEntity().getEntityId().longValue();
        }
        clientsRepository.upsertClientUser(existingClientUser, entityId, Boolean.TRUE);
    }

    public ClientSecretDTO refreshApiKey(Long clientId, Long clientUserId, Long entityId) {
        clientsService.validateClient(clientId, entityId);
        ClientSecret clientSecret = clientsRepository.refreshApiKey(clientUserId);
        return new ClientSecretDTO(clientSecret.getApiKey());
    }

    public void deleteClientUser(Long clientId, Long clientUserId, Long entityId) {
        clientsService.validateClient(clientId, entityId);
        clientsRepository.deleteClientUser(clientUserId);
    }
}
