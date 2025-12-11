package es.onebox.mgmt.b2b.users.converter;

import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.b2b.clients.dto.CreateClientUserDTO;
import es.onebox.mgmt.b2b.users.dto.ClientUserResponseDTO;
import es.onebox.mgmt.b2b.users.dto.ClientUsersDTO;
import es.onebox.mgmt.b2b.users.dto.CreateClientUserRequestDTO;
import es.onebox.mgmt.b2b.users.dto.UpdateClientUserRequestDTO;
import es.onebox.mgmt.b2b.users.enums.ClientUserType;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUser;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUsers;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;

import static es.onebox.core.security.Roles.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.ROLE_SYS_MGR;

public class ClientUserConverter {

    private ClientUserConverter() {
    }

    public static ClientUserResponseDTO toDTO(ClientUser in) {
        if (in == null) {
            return null;
        }

        ClientUserResponseDTO out = new ClientUserResponseDTO();
        out.setId(in.getId());
        out.setUsername(in.getUsername());
        out.setName(in.getName());
        out.setEmail(in.getEmail());
        out.setType(ClientUserType.valueOf(in.getType().name()));
        out.setCreationDate(in.getCreatedAt());
        out.setClientId(in.getClientId());
        out.setApiKey(maskApiKey(in.getApiKey()));
        out.setExternalReference(in.getExternalReference());

        return out;
    }

    public static ClientUsersDTO toDTO(ClientUsers clientUsers) {
        if (clientUsers == null) {
            return null;
        }

        ClientUsersDTO clientUsersDTO = new ClientUsersDTO();

        Metadata metadata = new Metadata();
        metadata.setLimit(clientUsers.getAmount() == null ? null : clientUsers.getAmount().longValue());
        metadata.setOffset(clientUsers.getFrom() == null ? null : clientUsers.getFrom().longValue());
        clientUsersDTO.setMetadata(metadata);
        if (CollectionUtils.isEmpty(clientUsers.getClientUserList())) {
            clientUsersDTO.setData(Collections.emptyList());
            metadata.setTotal(0L);
        } else {
            clientUsersDTO.setData(clientUsers.getClientUserList().stream().map(ClientUserConverter::toDTO).toList());
            metadata.setTotal(clientUsers.getTotalElements() == null ? null : clientUsers.getTotalElements().longValue());
        }
        return clientUsersDTO;
    }

    public static ClientUser toMs(CreateClientUserRequestDTO clientUserRequestDTO, Long clientId) {
        if (clientUserRequestDTO == null) {
            return null;
        }

        ClientUser clientUser = new ClientUser();
        clientUser.setUsername(clientUserRequestDTO.getUsername());
        clientUser.setEmail(clientUserRequestDTO.getEmail());
        clientUser.setName(clientUserRequestDTO.getName());
        clientUser.setPassword(clientUserRequestDTO.getPassword());
        clientUser.setExternalReference(clientUserRequestDTO.getExternalReference());
        clientUser.setType(clientUserRequestDTO.getType() == null ?
                null : es.onebox.mgmt.datasources.ms.client.enums.ClientUserType.valueOf(clientUserRequestDTO.getType().name()));
        clientUser.setClientId(clientId.intValue());
        return clientUser;
    }

    public static ClientUser toMs(UpdateClientUserRequestDTO clientUserRequestDTO, Long clientId, String username) {
        if (clientUserRequestDTO == null) {
            return null;
        }

        ClientUser clientUser = new ClientUser();
        clientUser.setUsername(username);
        clientUser.setEmail(clientUserRequestDTO.getEmail());
        clientUser.setName(clientUserRequestDTO.getName());
        clientUser.setExternalReference(clientUserRequestDTO.getExternalReference());
        clientUser.setType(clientUserRequestDTO.getType() == null ?
                null : es.onebox.mgmt.datasources.ms.client.enums.ClientUserType.valueOf(clientUserRequestDTO.getType().name()));
        clientUser.setClientId(clientId.intValue());
        return clientUser;
    }

    public static ClientUser toMs(CreateClientUserDTO clientUserDTO, Integer clientId) {
        if (clientUserDTO == null) {
            return null;
        }

        ClientUser clientUser = new ClientUser();
        clientUser.setUsername(clientUserDTO.getUsername());
        clientUser.setEmail(clientUserDTO.getEmail());
        clientUser.setName(clientUserDTO.getName());
        clientUser.setClientId(clientId);
        return clientUser;
    }

    private static String maskApiKey(String apiKey) {
        if (SecurityUtils.hasAnyRole(ROLE_OPR_MGR, ROLE_SYS_MGR, ROLE_OPR_ANS, ROLE_SYS_ANS)) {
            return apiKey;
        }
        return apiKey.replaceAll(".(?=.{5})", "*");
    }
}
