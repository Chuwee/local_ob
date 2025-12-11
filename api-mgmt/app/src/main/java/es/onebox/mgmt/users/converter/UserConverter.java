package es.onebox.mgmt.users.converter;

import es.onebox.core.security.Roles;
import es.onebox.mgmt.datasources.ms.entity.dto.ForgotPasswordPropertiesResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Notification;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.dto.UserAuthUrls;
import es.onebox.mgmt.datasources.ms.entity.dto.UserFilter;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.operators.dto.OperatorDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.users.dto.BaseUserDTO;
import es.onebox.mgmt.users.dto.CreateUserRequestDTO;
import es.onebox.mgmt.users.dto.ForgotPasswordPropertiesDTO;
import es.onebox.mgmt.users.dto.NotificationDTO;
import es.onebox.mgmt.users.dto.UpdateAuthUserRequestDTO;
import es.onebox.mgmt.users.dto.UpdateUserRequestDTO;
import es.onebox.mgmt.users.dto.UserAuthUrlsDTO;
import es.onebox.mgmt.users.dto.UserContactDTO;
import es.onebox.mgmt.users.dto.UserDTO;
import es.onebox.mgmt.users.dto.UserEntityDTO;
import es.onebox.mgmt.users.dto.UserLocationDTO;
import es.onebox.mgmt.users.dto.UserResponseDTO;
import es.onebox.mgmt.users.dto.UserSearchFilter;
import es.onebox.mgmt.users.dto.UserSelfDTO;
import es.onebox.mgmt.users.enums.UserStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    private UserConverter() {
    }

    public static void fromMsEntityDetails(User source, UserSelfDTO target, EntityDTO entityDTO) {
        if (source != null) {
            fromMsEntity(source, target);
            target.setEntity(entityDTO);
        }
    }

    public static void fromMsEntityReduced(User source, UserResponseDTO target, EntityDTO entityDTO) {
        if (source != null) {
            fromMsEntity(source, target);
            UserEntityDTO userEntityDTO = new UserEntityDTO();
            if (entityDTO != null) {
                userEntityDTO.setId(entityDTO.getId());
                userEntityDTO.setName(entityDTO.getName());
                userEntityDTO.setShortName(entityDTO.getShortName());
                target.setEntity(userEntityDTO);
            }
        }
    }
    private static void fromMsEntity(User source, UserDTO target) {

        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        target.setName(source.getName());
        target.setLastname(source.getLastName());

        target.setOperator(new OperatorDTO(source.getOperatorId()));
        if (source.getStatus() != null) {
            target.setStatus(UserStatus.byId(source.getStatus()));
        }
        target.setJobTitle(source.getJobTitle());
        target.setNotes(source.getNotes());

        target.setContact(new UserContactDTO());
        target.getContact().setPrimaryPhone(source.getMainPhone());
        target.getContact().setSecondaryPhone(source.getCellPhone());
        target.getContact().setFax(source.getFax());

        target.setLocation(new UserLocationDTO());
        target.getLocation().setAddress(source.getAddress());
        target.getLocation().setCity(source.getCity());
        target.getLocation().setPostalCode(source.getPostalCode());

    }


    public static User toMsEntity(CreateUserRequestDTO source) {
        User userDTO = toMsEntity((UserDTO) source);
        userDTO.setEntityId(source.getEntityId());
        userDTO.setEmail(source.getUsername());
        return userDTO;
    }

    public static User toMsEntity(UpdateUserRequestDTO source) {
        User userDTO = toMsEntity((UserDTO) source);
        userDTO.setEntityId(source.getEntityId());
        userDTO.setEmail(null);
        userDTO.setUsername(null);
        return userDTO;
    }

    public static User toMsEntity(Long userId, UpdateAuthUserRequestDTO source) {
        User userDTO = toMsEntity(source);
        userDTO.setId(userId);
        return userDTO;
    }

    public static User toMsEntity(UserDTO source) {
        User target = toMsEntity((BaseUserDTO) source);
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus().getId());
        }

        return target;
    }

    public static User toMsEntity(BaseUserDTO source){
        User target = new User();
        target.setName(source.getName());
        target.setLastName(source.getLastname());
        target.setJobTitle(source.getJobTitle());
        target.setNotes(source.getNotes());
        if (source.getContact() != null) {
            target.setMainPhone(source.getContact().getPrimaryPhone());
            target.setCellPhone(source.getContact().getSecondaryPhone());
            target.setFax(source.getContact().getFax());
        }
        if (source.getLocation() != null) {
            target.setAddress(source.getLocation().getAddress());
            target.setCity(source.getLocation().getCity());
            target.setPostalCode(source.getLocation().getPostalCode());
        }
        return target;
    }

    public static List<NotificationDTO> fromMsEntity(List<Notification> userNotifications) {
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification userNotification : userNotifications) {
            notificationDTOS.add(new NotificationDTO(userNotification.getType(), userNotification.getEnable()));
        }

        return notificationDTOS;
    }

    public static List<Notification> toMsEntity(List<NotificationDTO> notificationDTOs) {
        List<Notification> notifications = new ArrayList<>();

        for (NotificationDTO notificationDTO : notificationDTOs) {
            Notification notification = new Notification();
            notification.setEnable(notificationDTO.getEnable());
            notification.setType(notificationDTO.getType());
            notifications.add(notification);
        }

        return notifications;
    }

    public static UserAuthUrlsDTO fromMsEntity(UserAuthUrls in) {
        if (in == null) {
            return null;
        }
        UserAuthUrlsDTO out = new UserAuthUrlsDTO();
        out.setLogin(in.getLogin());
        out.setLogout(in.getLogout());
        out.setLoad(in.getLoad());
        return out;
    }

    public static UserFilter toMsEntity(UserSearchFilter filter){

        UserFilter.Builder msFilter = UserFilter
                .builder()
                .freeSearch(filter.getFreeSearch())
                .sort(filter.getSort())
                .limit(filter.getLimit())
                .offset(filter.getOffset())
                .roles(filter.getRoles())
                .permissions(filter.getPermissions())
                .entityId(filter.getEntityId());

        if (!SecurityUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS)) {
            msFilter.operatorId(SecurityUtils.getUserOperatorId());
        }

        if(SecurityUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS)) {
            msFilter.operatorId(filter.getOperatorId());
        }

        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            msFilter.status(filter.getStatus().stream().map(UserStatus::getId).collect(Collectors.toList()));
        }

        if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            msFilter.entityAdminId(SecurityUtils.getUserEntityId());
        }

        return msFilter.build();
    }

    public static ForgotPasswordPropertiesDTO toDTO(ForgotPasswordPropertiesResponse response){
        if(response == null || response.getMaxPasswordStorage() == null){
            return null;
        }
        ForgotPasswordPropertiesDTO dto = new ForgotPasswordPropertiesDTO();
        dto.setMaxPasswordStorage(response.getMaxPasswordStorage());

        return dto;
    }


}
