import { type User, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { CustomManagementType, type EntityType } from '@admin-clients/shared/common/data-access';
import type { Section, Subsection } from '@admin-clients/shared/common/ui/components';

export interface CpanelSection extends Section {
    role?: UserRoles[];
    roleToHide?: UserRoles[];
    subsections?: CpanelSubsection[];
}

export interface CpanelSubsection extends Subsection {
    role?: UserRoles[];
    roleToHide?: UserRoles[];
    entityType?: EntityType[];
    customManagementType?: CustomManagementType[];
}

export class SectionList extends Array<CpanelSection> {
    constructor(user: User) {
        super(
            {
                icon: 'events',
                customIcon: true,
                label: 'TITLES.EVENTS',
                id: 'section_events',
                visible: true,
                role: [UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.EVN_MGR],
                subsections: [
                    {
                        label: 'TITLES.MY_EVENTS',
                        id: 'substn_events',
                        link: ['/events'],
                        visible: true
                    },
                    {
                        label: 'TITLES.SEASON_TICKETS',
                        id: 'substn_season-tickets',
                        link: ['/season-tickets'],
                        visible: true
                    },
                    {
                        label: 'TITLES.EVENT_CONFIGS',
                        id: 'substn_promoter-venue-templates',
                        link: ['/promoter-venue-templates'],
                        visible: true
                    },
                    {
                        label: 'TITLES.TICKET_TEMPLATES',
                        id: 'substn_ticket-templates',
                        link: ['/ticket-templates'],
                        visible: true
                    },
                    {
                        label: 'TITLES.TICKET_PASSBOOK_TEMPLATES',
                        id: 'substn_ticket-passbook',
                        link: ['/ticket-passbook'],
                        visible: true
                    },
                    {
                        label: 'TITLES.PROMOTION_TEMPLATES',
                        id: 'substn_event-promotion-templates',
                        link: ['/event-promotion-templates'],
                        visible: true
                    },
                    {
                        label: 'TITLES.GROUPS',
                        id: 'substn_tours',
                        link: ['/tours'],
                        role: [UserRoles.OPR_MGR, UserRoles.EVN_MGR, UserRoles.OPR_ANS, UserRoles.ENT_ANS],
                        visible: true
                    },
                    {
                        label: 'TITLES.PRODUCERS',
                        id: 'substn_producers',
                        link: ['/producers'],
                        role: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.EVN_MGR, UserRoles.ENT_ANS],
                        visible: true
                    },
                    {
                        label: 'TITLES.EXTERNAL_MANAGEMENT',
                        id: 'substn_external-management',
                        link: ['/external-management'],
                        role: [UserRoles.EVN_MGR, UserRoles.ENT_MGR],
                        customManagementType: [CustomManagementType.incompatibilityEngine],
                        visible: true
                    }
                ]
            },
            {
                icon: 'trending_up',
                label: 'TITLES.SALES',
                id: 'section_sales',
                visible: true,
                role: [
                    UserRoles.OPR_MGR, UserRoles.OPR_CALL,
                    UserRoles.ENT_MGR, UserRoles.ENT_ANS,
                    UserRoles.CNL_MGR, UserRoles.CNL_SAC,
                    UserRoles.EVN_MGR, UserRoles.REC_MGR
                ],
                subsections: [
                    {
                        label: 'TITLES.ORDERS',
                        id: 'substn_transactions',
                        visible: true,
                        link: ['/transactions'],
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_CALL,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS,
                            UserRoles.CNL_MGR, UserRoles.CNL_SAC
                        ],
                        entityType: ['OPERATOR', 'ENTITY_ADMIN', 'CHANNEL_ENTITY']
                    },
                    {
                        label: 'TITLES.TICKETS',
                        id: 'substn_tickets',
                        visible: true,
                        link: ['/tickets'],
                        role: [
                            UserRoles.OPR_MGR,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS,
                            UserRoles.EVN_MGR,
                            UserRoles.CNL_MGR, UserRoles.CNL_SAC,
                            UserRoles.REC_MGR
                        ]
                    },
                    {
                        label: 'TITLES.PRODUCTS_SALES',
                        id: 'substn_products',
                        visible: true,
                        link: ['/products-sales'],
                        role: [
                            UserRoles.OPR_MGR,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS,
                            UserRoles.EVN_MGR,
                            UserRoles.CNL_MGR, UserRoles.CNL_SAC,
                            UserRoles.REC_MGR
                        ]
                    },
                    {
                        label: 'TITLES.MEMBER_ORDERS',
                        id: 'substn_member-orders',
                        visible: true,
                        link: ['/member-orders'],
                        role: [
                            UserRoles.OPR_MGR,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS,
                            UserRoles.EVN_MGR,
                            UserRoles.CNL_MGR, UserRoles.CNL_SAC
                        ],
                        entityType: ['OPERATOR', 'ENTITY_ADMIN', 'CHANNEL_ENTITY'],
                        customManagementType: [CustomManagementType.avetintegration]
                    },
                    {
                        label: 'TITLES.GIFT_CARD',
                        id: 'substn_voucher-orders',
                        visible: true,
                        link: ['/voucher-orders'],
                        role: [
                            UserRoles.OPR_MGR,
                            UserRoles.ENT_MGR,
                            UserRoles.CNL_MGR, UserRoles.CNL_SAC
                        ],
                        entityType: ['OPERATOR', 'ENTITY_ADMIN', 'CHANNEL_ENTITY']
                    },
                    {
                        label: 'TITLES.PAYOUTS',
                        id: 'substn_payout',
                        visible: true,
                        link: ['/payouts'],
                        role: [
                            UserRoles.OPR_MGR,
                            UserRoles.ENT_MGR,
                            UserRoles.CNL_MGR, UserRoles.CNL_SAC
                        ],
                        entityType: ['OPERATOR', 'ENTITY_ADMIN', 'CHANNEL_ENTITY']
                    }
                ]
            },
            {
                icon: 'event_seat',
                label: 'TITLES.VIEWERS',
                id: 'section_viewers',
                visible: true,
                role: [
                    UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS,
                    UserRoles.ENT_MGR,
                    UserRoles.CNL_SAC,
                    UserRoles.CRM_MGR, UserRoles.CRM_DLIST
                ],
                subsections: [
                    {
                        label: 'TITLES.CUSTOMERS',
                        id: 'substn_customers',
                        link: ['/customers'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS,
                            UserRoles.CRM_MGR, UserRoles.ENT_MGR
                        ]
                    },
                    {
                        label: 'TITLES.SUBSCRIPTION_LISTS',
                        id: 'substn_subscription-lists',
                        link: ['/subscription-lists'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS,
                            UserRoles.ENT_MGR,
                            UserRoles.CRM_MGR, UserRoles.CRM_DLIST
                        ]
                    },
                    {
                        label: 'TITLES.BUYERS',
                        id: 'substn_buyers',
                        link: ['/buyers'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS,
                            UserRoles.ENT_MGR,
                            UserRoles.CNL_SAC,
                            UserRoles.CRM_MGR
                        ]
                    }
                ]
            },
            {
                icon: 'fastfood',
                label: 'TITLES.ADDITIONAL_PRODUCTS',
                id: 'section_additional-products',
                visible: true,
                role: [
                    UserRoles.OPR_MGR,
                    UserRoles.ENT_MGR,
                    UserRoles.EVN_MGR
                ],
                subsections: [
                    {
                        label: 'TITLES.MY_PRODUCTS',
                        id: 'substn_products',
                        link: ['/products'],
                        visible: true
                    },
                    {
                        label: 'TITLES.PRODUCTS_DELIVERY_POINTS',
                        id: 'substn_products_delivery_points',
                        link: ['/delivery-points'],
                        visible: true
                    }
                ]
            },
            {
                icon: 'inventory',
                label: 'TITLES.PACKS',
                id: 'section_packs',
                visible: true,
                badge: 'INFORMATION_BADGE.BETA_FEATURE',
                badgeClass: 'blue',
                role: [
                    UserRoles.OPR_MGR, UserRoles.EVN_MGR
                ],
                subsections: [
                    {
                        label: 'TITLES.MY_PACKS',
                        id: 'substn_packs',
                        link: ['/packs'],
                        visible: true
                    }
                ]
            },
            {
                icon: 'email',
                label: 'TITLES.NOTIFICATIONS.EMAIL',
                id: 'section_notifications-email',
                visible: false,
                badge: 'INFORMATION_BADGE.BETA_FEATURE',
                badgeClass: 'blue',
                role: [
                    UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS,
                    UserRoles.ENT_MGR, UserRoles.CRM_MGR
                ],
                subsections: [
                    {
                        label: 'TITLES.NOTIFICATIONS.MY_EMAIL_NOTIFICATIONS_EMAILS',
                        id: 'substn_notifications',
                        link: ['/notifications'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS,
                            UserRoles.CRM_MGR, UserRoles.ENT_MGR
                        ]
                    },
                    {
                        label: 'TITLES.NOTIFICATIONS.MY_EMAIL_NOTIFICATIONS_SETTINGS',
                        id: 'substn_notifications-settings',
                        link: ['/notifications-settings'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR
                        ]
                    }
                ]
            },
            {
                icon: 'spoke',
                label: 'TITLES.COLLECTIVES',
                id: 'section_collectives',
                visible: true,
                role: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.COL_MGR],
                subsections: [
                    {
                        label: 'TITLES.COLLECTIVES',
                        id: 'substn_collectives',
                        link: ['/collectives'],
                        visible: true,
                        role: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.COL_MGR]
                    }
                ]
            },
            {
                icon: 'festival',
                label: 'TITLES.VENUES',
                id: 'section_venues',
                visible: true,
                role: [UserRoles.REC_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS],
                subsections: [
                    {
                        label: 'TITLES.MY_VENUES',
                        id: 'substn_venues',
                        link: ['/venues'],
                        visible: true,
                        role: [UserRoles.REC_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                    },
                    {
                        label: 'TITLES.VENUE_CONFIGS',
                        id: 'substn_venue-templates',
                        link: ['/venue-templates'],
                        visible: true,
                        role: [UserRoles.REC_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                    }
                ]
            },
            {
                icon: 'settings',
                label: 'TITLES.CONFIGURATIONS',
                id: 'section_configurations',
                visible: true,
                role: [UserRoles.SYS_MGR],
                subsections: [
                    {
                        label: 'TITLES.OPERATORS',
                        id: 'substn_operators',
                        link: ['/operators'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.INVOICING',
                        id: 'substn_invoicing',
                        link: ['/invoicing'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.INSURERS',
                        id: 'substn_insurers',
                        link: ['/insurers'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.WEBHOOKS',
                        id: 'substn_webhooks',
                        link: ['/webhooks'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.VISIBILITY',
                        id: 'substn_visibility',
                        link: ['/visibility'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    }
                ]
            },
            {
                icon: 'route',
                label: 'TITLES.CHANNELS',
                id: 'section_channels',
                visible: true,
                role: [UserRoles.CNL_MGR, UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS, UserRoles.SYS_MGR],
                subsections: [
                    {
                        label: 'TITLES.MY_CHANNELS',
                        id: 'substn_channels',
                        link: ['/channels'],
                        visible: true,
                        role: [UserRoles.CNL_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.SALES_REQUESTS',
                        id: 'substn_sales-requests',
                        link: ['/sales-requests'],
                        visible: true,
                        role: [UserRoles.CNL_MGR, UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS]
                    },
                    {
                        label: 'TITLES.PRODUCTS_SALE_REQUESTS',
                        id: 'substn_products-sale-requests',
                        link: ['/products-sale-requests'],
                        visible: true,
                        role: [UserRoles.CNL_MGR, UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS]
                    },
                    {
                        label: 'TITLES.PACKS_SALE_REQUESTS',
                        id: 'substn_packs-sale-requests',
                        link: ['/packs-sale-requests'],
                        visible: true,
                        role: [UserRoles.CNL_MGR, UserRoles.OPR_MGR, UserRoles.OPR_CALL, UserRoles.OPR_ANS]
                    },
                    {
                        label: 'TITLES.VOUCHERS',
                        id: 'substn_vouchers',
                        link: ['/vouchers'],
                        visible: true,
                        role: [UserRoles.CNL_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS]
                    },
                    {
                        label: 'TITLES.LITERALS',
                        id: 'substn_literals',
                        link: ['/literals'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    }
                ]
            },
            {
                icon: 'work',
                label: 'TITLES.PROFESSIONALS',
                id: 'section_professionals',
                visible: user.entity?.settings?.enable_B2B
                    || !!user.roles?.find(role => role.code === UserRoles.OPR_MGR || role.code === UserRoles.OPR_ANS),
                role: [
                    UserRoles.OPR_MGR, UserRoles.OPR_ANS,
                    UserRoles.ENT_MGR, UserRoles.ENT_ANS
                ],
                subsections: [
                    {
                        label: 'TITLES.B2B_CLIENTS',
                        id: 'substn_b2b-clients',
                        link: ['/b2b-clients'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_ANS,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS
                        ]
                    },
                    {
                        label: 'TITLES.ENTITY_B2B_CONDITIONS',
                        id: 'substn_entity-b2b-conditions',
                        link: ['/entity-b2b-conditions'],
                        visible: true,
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_ANS,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS
                        ]
                    },
                    {
                        label: 'TITLES.B2B_PUBLISHINGS',
                        id: 'substn_b2b-publishings',
                        link: ['/b2b-publishings'],
                        visible: user.entity?.settings?.allow_B2B_publishing
                            || !!user.roles?.find(role => role.code === UserRoles.OPR_MGR || role.code === UserRoles.OPR_ANS)
                            || !!user.entity?.settings?.types?.find(type => type === 'ENTITY_ADMIN'),
                        role: [
                            UserRoles.OPR_MGR, UserRoles.OPR_ANS,
                            UserRoles.ENT_MGR, UserRoles.ENT_ANS
                        ]
                    }
                ]
            },
            {
                icon: 'account_box',
                label: 'TITLES.ORGANIZATIONS',
                id: 'section_organizations',
                visible: true,
                subsections: [
                    {
                        label: 'TITLES.MY_ENTITY',
                        id: 'substn_my-entity',
                        link: ['/my-entity'],
                        visible: user.entity?.id !== user.operator?.id,
                        role: [UserRoles.ENT_MGR]
                    },
                    {
                        label: 'TITLES.ENTITIES',
                        id: 'substn_entities',
                        link: ['/entities'],
                        visible: true,
                        entityType: ['ENTITY_ADMIN', 'OPERATOR', 'SUPER_OPERATOR'],
                        role: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.ENT_MGR, UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.MY_USER',
                        id: 'substn_my-user',
                        link: ['/my-user'],
                        visible: true
                    },
                    {
                        label: 'TITLES.USERS',
                        id: 'substn_users',
                        link: ['/users'],
                        visible: true,
                        role: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.ENT_MGR, UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.TERMINALS',
                        id: 'substn_terminals',
                        link: ['/terminals'],
                        visible: true,
                        role: [UserRoles.OPR_MGR]
                    },
                    {
                        label: 'TITLES.AUDIT',
                        id: 'substn_audit',
                        visible: false,
                        role: [UserRoles.OPR_MGR]
                    }
                ]
            },
            {
                icon: 'assessment',
                label: 'TITLES.BI_REPORTS',
                id: 'section_bi-reports',
                visible: true,
                role: [UserRoles.SYS_MGR],
                subsections: [
                    {
                        label: 'TITLES.ENTITIES_LICENSES',
                        id: 'substn_bi-entities',
                        link: ['/bi-entities'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.MOBILE_LICENSES',
                        id: 'substn_bi-mobile',
                        link: ['/bi-mobile'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    },
                    {
                        label: 'TITLES.IMPERSONATION',
                        id: 'substn_bi-impersonation',
                        link: ['/bi-impersonation'],
                        visible: true,
                        role: [UserRoles.SYS_MGR]
                    }
                ]
            },
            {
                icon: 'assessment',
                label: 'TITLES.MY_BI',
                id: 'section_my-bi',
                visible: true,
                role: [UserRoles.BI_USR, UserRoles.FV_REPORTING],
                subsections: [
                    {
                        id: 'substn_my-bi-reports',
                        label: 'TITLES.MY_BI_REPORTS',
                        link: ['/bi-reports'],
                        visible: true,
                        role: [UserRoles.BI_USR]
                    },
                    {
                        id: 'substn_superset-bi-reports',
                        label: 'TITLES.SUPERSET_BI_REPORTS',
                        link: ['/superset-bi-reports'],
                        visible: true,
                        role: [UserRoles.BI_USR]
                    },
                    {
                        id: 'substn_my-bi-subscriptions',
                        label: 'TITLES.MY_BI_SUBSCRIPTIONS',
                        link: ['/bi-subscriptions'],
                        visible: true,
                        role: [UserRoles.BI_USR]
                    },
                    {
                        id: 'fever_zone-reporting',
                        label: 'TITLES.FEVER_ZONE_REPORTING',
                        link: ['/fever-zone'],
                        icon: 'open_in_new',
                        visible: true,
                        role: [UserRoles.FV_REPORTING]
                    }
                ]
            }
        );
    }
}
