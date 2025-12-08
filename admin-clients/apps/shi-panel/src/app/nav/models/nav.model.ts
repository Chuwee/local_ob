import { Section, Subsection } from '@admin-clients/shared/common/ui/components';
import { UserPermissions, UserRoles } from '@admin-clients/shi-panel/utility-models';

export interface ShipanelSection extends Section {
    subsections?: ShiPanelSubsection[];
    permissions?: UserPermissions[];
}

export interface ShiPanelSubsection extends Subsection {
    role?: UserRoles[];
    permissions?: UserPermissions[];
}

export class SectionList extends Array<ShipanelSection> {
    constructor() {
        super(
            {
                id: 'sales-menu',
                icon: 'trending_up',
                label: 'TITLES.SALES_TITLE',
                visible: true,
                permissions: [UserPermissions.salesRead],
                subsections: [
                    {
                        id: 'sales',
                        label: 'TITLES.SALES_TITLE',
                        link: ['/sales'],
                        visible: true
                    },
                    {
                        id: 'sales-settings',
                        label: 'TITLES.SALES_SETTINGS',
                        link: ['/sales-settings'],
                        permissions: [UserPermissions.configurationRead],
                        visible: true
                    },
                    {
                        id: 'error-dashboard',
                        label: 'TITLES.ERROR_DASHBOARD',
                        link: ['/error-dashboard'],
                        permissions: [UserPermissions.salesRead],
                        visible: true
                    }
                ]
            },
            {
                id: 'listings-menu',
                icon: 'list',
                label: 'TITLES.LISTINGS_TITLE',
                visible: true,
                permissions: [UserPermissions.listingRead],
                subsections: [
                    {
                        id: 'listings',
                        label: 'TITLES.LISTINGS_TITLE',
                        link: ['/listings'],
                        visible: true
                    },
                    {
                        id: 'ingestor-settings',
                        label: 'TITLES.INGESTOR_SETTINGS',
                        link: ['/ingestor-settings'],
                        permissions: [UserPermissions.ingestorRead],
                        visible: true
                    }
                ]
            },
            {
                id: 'catalog-menu',
                icon: 'event',
                label: 'TITLES.CATALOG_TITLE',
                visible: true,
                permissions: [UserPermissions.mappingRead, UserPermissions.matchingRead],
                subsections: [
                    {
                        id: 'mappings',
                        label: 'TITLES.MAPPINGS_TITLE',
                        link: ['/mappings'],
                        permissions: [UserPermissions.mappingRead],
                        visible: true
                    },
                    {
                        id: 'matchings',
                        label: 'TITLES.MATCHINGS_TITLE',
                        link: ['/matchings'],
                        permissions: [UserPermissions.matchingRead],
                        visible: true
                    },
                    {
                        id: 'matcher-settings',
                        label: 'TITLES.MATCHER_SETTINGS',
                        link: ['/matcher-settings'],
                        permissions: [UserPermissions.matchingRead],
                        visible: true
                    },
                    {
                        id: 'candidates-blacklist',
                        label: 'TITLES.CANDIDATES_BLACKLIST',
                        link: ['/candidates-blacklist'],
                        permissions: [UserPermissions.matchingRead],
                        visible: true
                    }
                ]
            },
            {
                id: 'currencies-menu',
                icon: 'monetization_on',
                label: 'TITLES.CURRENCIES_TITLE',
                visible: true,
                permissions: [UserPermissions.exchangeRateRead],
                subsections: [
                    {
                        id: 'currencies',
                        label: 'TITLES.CURRENCIES_TITLE',
                        link: ['/currencies'],
                        permissions: [UserPermissions.exchangeRateRead],
                        visible: true
                    }
                ]
            },
            {
                id: 'profile-menu',
                icon: 'account_box',
                label: 'TITLES.PROFILE',
                visible: true,
                subsections: [
                    {
                        id: 'my-user',
                        label: 'TITLES.MY_USER',
                        link: ['/my-user'],
                        visible: true
                    },
                    {
                        id: 'users',
                        label: 'TITLES.USERS_TITLE',
                        link: ['/users'],
                        visible: true,
                        role: [UserRoles.owner, UserRoles.admin],
                        permissions: [UserPermissions.userRead]
                    }
                ]
            }
        );
    }
}
