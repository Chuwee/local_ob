/* Texts to show on permission description */
export enum DefaultPermissionsDescriptions {
    exportData = 'EXPORT_DATA',
    createNewMapping = 'CREATE_MAPPING',
    editMapping = 'EDIT_MAPPING',
    deleteMapping = 'DELETE_MAPPING',
    createUser = 'CREATE_USER',
    editUser = 'EDIT_USER',
    editPermissions = 'EDIT_PERMISSIONS',
    editUserStatus = 'EDIT_USER_STATUS',
    deleteUser = 'DELETE_USER',
    relaunchSale = 'RELAUNCH_SALE',
    editExchangeRate = 'MODIFY_EXCHANGE_RATE',
    editGeneralAdmission = 'MODIFY_GENERAL_ADMISSION',
    editExcludedSections = 'MODIFY_EXCLUDED_SECTIONS',
    editListigsBlacklist = 'EDIT_LISTINGS_BLACKLIST',
    editConfigurations = 'EDIT_SALES_SETTINGS'
}

/* eslint-disable @typescript-eslint/naming-convention */
export const PermissionsDescriptions = {
    SALES_READ: [
        DefaultPermissionsDescriptions.exportData
    ],
    SALES_WRITE: [
        DefaultPermissionsDescriptions.relaunchSale
    ],
    LISTING_READ: [
        DefaultPermissionsDescriptions.exportData
    ],
    LISTING_WRITE: [
        DefaultPermissionsDescriptions.editListigsBlacklist
    ],
    MAPPING_READ: [
        DefaultPermissionsDescriptions.exportData
    ],
    MAPPING_WRITE: [
        DefaultPermissionsDescriptions.createNewMapping,
        DefaultPermissionsDescriptions.editMapping,
        DefaultPermissionsDescriptions.deleteMapping
    ],
    MATCHING_READ: [
        DefaultPermissionsDescriptions.exportData
    ],
    MATCHING_WRITE: [],
    INGESTOR_READ: [],
    INGESTOR_WRITE: [
        DefaultPermissionsDescriptions.editGeneralAdmission,
        DefaultPermissionsDescriptions.editExcludedSections
    ],
    CONFIGURATION_READ: [],
    CONFIGURATION_WRITE: [
        DefaultPermissionsDescriptions.editConfigurations
    ],
    EXCHANGE_RATE_READ: [],
    EXCHANGE_RATE_WRITE: [
        DefaultPermissionsDescriptions.editExchangeRate
    ],
    USER_READ: [],
    USER_WRITE: [
        DefaultPermissionsDescriptions.createUser,
        DefaultPermissionsDescriptions.editUser,
        DefaultPermissionsDescriptions.editPermissions,
        DefaultPermissionsDescriptions.editUserStatus,
        DefaultPermissionsDescriptions.deleteUser
    ]
};

export enum PermissionExtraInformations {
    roleOwner = 'ROLE_OWNER',
    roleAdmin = 'ROLE_ADMIN',
    roleUser = 'ROLE_USER'
}

/* eslint-disable @typescript-eslint/naming-convention */
export const PermissionsExtraInformations = {
    USER_WRITE: [
        PermissionExtraInformations.roleOwner,
        PermissionExtraInformations.roleAdmin,
        PermissionExtraInformations.roleUser
    ]
};
