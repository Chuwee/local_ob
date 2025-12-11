package es.onebox.mgmt.datasources.ms.client.repositories;

import es.onebox.mgmt.datasources.ms.client.MsClientDatasource;
import es.onebox.mgmt.datasources.ms.client.dto.ClientsConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.DeleteConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientEntity;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientSecret;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUser;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUsers;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SearchClientsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientsRepository {

    private final MsClientDatasource clientDatasource;

    @Autowired
    public ClientsRepository(MsClientDatasource clientDatasource) {
        this.clientDatasource = clientDatasource;
    }

    public Clients getClients(SearchClientsFilter searchClientsFilter) {
        return clientDatasource.getClients(searchClientsFilter);
    }

    public Client getClient(Long clientId, Long entityId) {
        return clientDatasource.getClient(clientId, entityId);
    }

    public Client upsertClient(Client client, Long entityId) {
        return clientDatasource.upsertClient(client, entityId);
    }

    public void deleteClient(Long clientId, Long entityId) {
        clientDatasource.deleteClient(clientId, entityId);
    }

    public ConditionsData getConditions(ConditionsFilter filter) {
        return clientDatasource.getConditions(filter);
    }

    public ClientsConditionsData getClientConditions(ConditionsFilter filter) {
        return clientDatasource.getClientConditions(filter);
    }

    public void createConditions(List<ConditionsData> conditions) {
        clientDatasource.createConditions(conditions);
    }

    public void deleteConditions(Long groupId) {
        clientDatasource.deleteConditions(groupId);
    }

    public void deleteClientsConditions(DeleteConditionsFilter filter) {
        clientDatasource.deleteClientsConditions(filter);
    }

    public ClientUsers getClientUsers(Long clientId, String keyword, Integer from, Integer amount,
                                      Boolean countAllElements) {
        return clientDatasource.getClientUsers(clientId, keyword, from, amount, countAllElements);
    }

    public ClientUser upsertClientUser(ClientUser clientUser, Long entityId, Boolean resetPassword) {
        return clientDatasource.upsertClientUser(clientUser, entityId, resetPassword);
    }

    public ClientUser deleteClientUser(Long clientUserId) {
        return clientDatasource.deleteClientUser(clientUserId);
    }

    public List<ClientUser> getClientUsersByUsername(String username) {
        return clientDatasource.getClientUsersByUsername(username);
    }

    public ClientUser getClientUser(Long clientUserId) {
        return clientDatasource.getClientUser(clientUserId);
    }

    public void createClientEntity(List<ClientEntity> clientEntites) {
        clientDatasource.createClientEntity(clientEntites);
    }

    public ClientSecret refreshApiKey(Long userId) {
        return clientDatasource.refreshApiKey(userId);
    }
}
