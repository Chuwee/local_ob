import { inject } from '@angular/core';
import { Router, Routes } from '@angular/router';
import { authCanActivateGuard, multipleEntityGuard, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
// eslint-disable-next-line @nx/enforce-module-boundaries
import { provideLoginBackgroundurl } from '@admin-clients/shared/feature/login';
import { entityRoleGuard } from './core/auth/entity-role.guard';
import { getLoginBackgroundUrlByEnvironment } from './core/loginBackgroundFactory';
import { NavComponent } from './nav/nav.component';

const protectedRoutes = [
    {
        path: 'users',
        loadChildren: () => import('@admin-clients/cpanel/organizations/entity-users/feature').then(m => m.usersRoutes),
        data: {
            breadcrumb: 'TITLES.USERS'
        }
    },
    {
        path: 'my-user',
        loadChildren: () => import('@admin-clients/cpanel/organizations/entity-users/feature').then(m => m.myselfRoutes),
        data: {
            breadcrumb: 'TITLES.MY_USER'
        }
    },
    {
        path: 'entities',
        loadChildren: () => import('@admin-clients/cpanel/organizations/entities/feature').then(m => m.entitiesRoutes),
        canActivate: [multipleEntityGuard],
        data: {
            breadcrumb: 'TITLES.ENTITIES'
        }
    },
    {
        path: 'my-entity',
        loadChildren: () => import('@admin-clients/cpanel/organizations/entities/feature').then(m => m.entityRoutes),
        data: {
            breadcrumb: 'TITLES.MY_ENTITY'
        }
    },
    {
        path: 'terminals',
        loadChildren: () => import('@admin-clients/cpanel-channels-terminals-feature').then(m => m.routes),
        canActivate: [multipleEntityGuard],
        data: {
            breadcrumb: 'TITLES.TERMINALS'
        }
    },
    {
        path: 'admin-channels',
        loadChildren: () => import('@admin-clients/cpanel/migration/channels/feature').then(m => m.routes),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.ADMIN_CHANNELS',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'collectives',
        loadChildren: () => import('@admin-clients/cpanel/collectives/feature').then(m => m.routes),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.COLLECTIVES',
            roles: [UserRoles.OPR_ANS, UserRoles.OPR_MGR, UserRoles.COL_MGR]
        }
    },
    {
        path: 'events',
        loadChildren: () => import('@admin-clients/cpanel-promoters-my-events-feature').then(m => m.EventsModule),
        data: {
            breadcrumb: 'TITLES.MY_EVENTS'
        }
    },
    {
        path: 'season-tickets',
        loadChildren: () => import('@admin-clients/cpanel-promoters-season-tickets-list-feature').then(m => m.SeasonTicketsModule),
        data: {
            breadcrumb: 'TITLES.SEASON_TICKETS'
        }
    },
    {
        path: 'promoter-venue-templates',
        loadChildren: () => import('@admin-clients/cpanel-promoters-venue-templates-feature').then(m => m.PromoterVenueTemplatesModule),
        data: {
            breadcrumb: 'TITLES.EVENT_CONFIGS'
        }
    },
    {
        path: 'event-promotion-templates',
        loadChildren: () => import('@admin-clients/cpanel-promoters-promotion-templates-feature').then(r => r.PROMOTION_TEMPLATES_ROUTES),
        data: {
            breadcrumb: 'TITLES.EVENT_PROMOTION_TEMPLATES'
        }
    },
    {
        path: 'ticket-templates',
        loadChildren: () => import('@admin-clients/cpanel-promoters-ticket-templates-feature').then(m => m.routes),
        data: {
            breadcrumb: 'TITLES.TICKET_TEMPLATES'
        }
    },
    {
        path: 'producers',
        loadChildren: () => import('@admin-clients/cpanel-promoters-producers-feature').then(m => m.routes),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.PRODUCERS',
            roles: [
                UserRoles.OPR_ANS,
                UserRoles.OPR_MGR,
                UserRoles.ENT_MGR,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'external-management',
        loadChildren: () => import('@admin-clients/cpanel-promoters-external-management-feature').then(m => m.EXTERNAL_MGMT_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.EXTERNAL_MANAGEMENT',
            roles: [
                UserRoles.OPR_ANS,
                UserRoles.OPR_MGR,
                UserRoles.ENT_MGR,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'transactions',
        loadChildren: () => import('@admin-clients/cpanel/sales/feature').then(m => m.OrdersModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.ORDERS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_CALL,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.CNL_MGR,
                UserRoles.CNL_SAC
            ]
        }
    },
    {
        path: 'tickets',
        loadChildren: () => import('@admin-clients/cpanel/sales/feature').then(m => m.TicketsModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.TICKETS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR,
                UserRoles.CNL_MGR,
                UserRoles.CNL_SAC,
                UserRoles.REC_MGR
            ]
        }
    },
    {
        path: 'products-sales',
        loadChildren: () => import('@admin-clients/cpanel/sales/feature').then(m => m.PRODUCTS_SALES_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.PRODUCTS_SALES',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR,
                UserRoles.CNL_MGR,
                UserRoles.CNL_SAC,
                UserRoles.REC_MGR
            ]
        }
    },
    {
        path: 'voucher-orders',
        loadChildren: () => import('@admin-clients/cpanel/sales/feature').then(m => m.VoucherOrdersModule),
        data: {
            breadcrumb: 'TITLES.VOUCHER_ORDERS'
        }
    },
    {
        path: 'payouts',
        loadChildren: () => import('@admin-clients/cpanel/sales/feature').then(m => m.payoutsRoutes),
        data: {
            breadcrumb: 'TITLES.PAYOUTS'
        }
    },
    {
        path: 'member-orders',
        loadChildren: () => import('@admin-clients/cpanel/sales/feature').then(m => m.MemberOrdersModule),
        data: {
            breadcrumb: 'TITLES.MEMBER_ORDERS'
        }
    },
    {
        path: 'operators',
        loadChildren: () => import('@admin-clients/cpanel-configurations-operators-feature').then(r => r.OPERATORS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.OPERATORS',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'invoicing',
        loadChildren: () => import('@admin-clients/cpanel-configurations-invoicing-feature').then(m => m.InvoicingModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.INVOICING',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'insurers',
        loadChildren: () => import('@admin-clients/cpanel-configurations-insurers-feature').then(r => r.INSURERS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.INSURERS',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'webhooks',
        loadChildren: () => import('@admin-clients/cpanel-configurations-webhooks-feature').then(m => m.WEBHOOKS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.WEBHOOKS',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'visibility',
        loadChildren: () => import('@admin-clients/cpanel-configurations-visibility-feature').then(m => m.VISIBILITY_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.VISIBILITY',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'channels',
        loadChildren: () => import('@admin-clients/cpanel/channels/my-channels/feature').then(m => m.ChannelsModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MY_CHANNELS',
            roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.CNL_MGR, UserRoles.SYS_MGR]
        }
    },
    {
        path: 'sales-requests',
        loadChildren: () => import('@admin-clients/cpanel-channels-sales-requests-feature').then(m => m.SalesRequestsModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.SALES_REQUESTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.OPR_CALL,
                UserRoles.ENT_ANS,
                UserRoles.CNL_MGR
            ]
        }
    },
    {
        path: 'products-sale-requests',
        loadChildren: () => import('@admin-clients/cpanel-channels-products-sale-requests-feature')
            .then(r => r.PRODUCTS_SALE_REQUESTS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.PRODUCTS_SALE_REQUESTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.OPR_CALL,
                UserRoles.ENT_ANS,
                UserRoles.CNL_MGR
            ]
        }
    },
    {
        path: 'packs-sale-requests',
        loadChildren: () => import('@admin-clients/cpanel-channels-packs-sale-requests-feature').then(r => r.PACKS_SALE_REQUESTS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.PACKS_SALE_REQUESTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.OPR_CALL,
                UserRoles.ENT_ANS,
                UserRoles.CNL_MGR
            ]
        }
    },
    {
        path: 'vouchers',
        loadChildren: () => import('@admin-clients/cpanel-vouchers-feature').then(m => m.VouchersModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.VOUCHERS',
            roles: [UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.CNL_MGR]
        }
    },
    {
        path: 'literals',
        loadChildren: () => import('@admin-clients/cpanel-channels-literals-feature').then(m => m.LITERALS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.LITERALS',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'customers',
        loadChildren: () => import('@admin-clients/cpanel-viewers-customers-feature').then(m => m.CUSTOMERS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.CUSTOMERS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.OPR_CALL,
                UserRoles.ENT_ANS,
                UserRoles.ENT_MGR,
                UserRoles.CRM_MGR,
                UserRoles.CRM_DLIST,
                UserRoles.CNL_SAC
            ]
        }
    },
    {
        path: 'subscription-lists',
        loadChildren: () => import('@admin-clients/cpanel-viewers-subscriptions-feature').then(m => m.SubscriptionListsModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.SUBSCRIPTION_LISTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_CALL,
                UserRoles.OPR_ANS,
                UserRoles.ENT_ANS,
                UserRoles.ENT_MGR,
                UserRoles.CRM_MGR,
                UserRoles.CRM_DLIST
            ]
        }
    },
    {
        path: 'buyers',
        loadChildren: () => import('@admin-clients/cpanel-viewers-buyers-feature').then(m => m.BuyersModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.BUYERS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_CALL,
                UserRoles.OPR_ANS,
                UserRoles.ENT_ANS,
                UserRoles.ENT_MGR,
                UserRoles.EVN_MGR,
                UserRoles.CNL_MGR,
                UserRoles.CRM_MGR,
                UserRoles.CRM_DLIST,
                UserRoles.CNL_SAC
            ]
        }
    },
    {
        path: 'ticket-passbook',
        loadChildren: () => import('@admin-clients/cpanel-promoters-tickets-passbook-feature').then(m => m.TicketsPassbookModule),
        data: {
            breadcrumb: 'TITLES.TICKET_PASSBOOK_TEMPLATES'
        }
    },
    {
        path: 'tours',
        loadChildren: () => import('@admin-clients/cpanel/promoters/tours/feature').then(m => m.TOURS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.GROUPS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.EVN_MGR,
                UserRoles.OPR_ANS,
                UserRoles.ENT_ANS
            ]
        }
    },
    {
        path: 'venues',
        loadChildren: () => import('@admin-clients/cpanel-venues-my-venues-feature').then(m => m.VenuesModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MY_VENUES',
            roles: [
                UserRoles.REC_MGR,
                UserRoles.EVN_MGR,
                UserRoles.ENT_ANS,
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS
            ]
        }
    },
    {
        path: 'venue-templates',
        loadChildren: () => import('@admin-clients/cpanel-venues-venue-templates-feature').then(m => m.VENUE_TEMPLATES_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.VENUE_CONFIGS',
            roles: [UserRoles.REC_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS]
        }
    },
    {
        path: 'notifications',
        loadChildren: () => import('@admin-clients/cpanel/notifications/feature').then(m => m.myNotificationsRoutes),
        data: {
            breadcrumb: 'TITLES.NOTIFICATIONS.MY_EMAIL_NOTIFICATIONS_EMAILS'
        }
    },
    {
        path: 'notifications-settings',
        loadChildren: () => import('@admin-clients/cpanel/notifications/feature').then(m => m.notificationsSettingsRoutes),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.NOTIFICATIONS.MY_EMAIL_NOTIFICATIONS_EMAILS',
            roles: [UserRoles.OPR_MGR]
        }
    },
    {
        path: 'b2b-clients',
        loadChildren: () => import('@admin-clients/cpanel/b2b/feature').then(m => m.B2bClientsModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.B2B_CLIENTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'entity-b2b-conditions',
        loadChildren: () => import('@admin-clients/cpanel/b2b/feature').then(m => m.entityB2bConditionsRoutes
        ),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.ENTITY_B2B_CONDITIONS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'b2b-publishings',
        loadChildren: () => import('@admin-clients/cpanel/b2b/feature').then(m => m.B2bPublishingsModule),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.B2B_PUBLISHINGS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.OPR_ANS,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'bi-entities',
        loadChildren: () => import('@admin-clients/cpanel/bi/feature').then(m => m.BI_ENTITIES_LICENSES_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.ENTITIES_LICENSES',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'bi-mobile',
        loadChildren: () => import('@admin-clients/cpanel/bi/feature').then(m => m.BI_MOBILE_LICENSES_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MOBILE_LICENSES',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'bi-impersonation',
        loadChildren: () => import('@admin-clients/cpanel/bi/feature').then(m => m.BI_IMPERSONATIONS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.IMPERSONATION',
            roles: [UserRoles.SYS_MGR]
        }
    },
    {
        path: 'bi-reports',
        loadChildren: () => import('@admin-clients/cpanel/bi/feature').then(m => m.BI_REPORTS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MY_BI_REPORTS',
            roles: [UserRoles.BI_USR]
        }
    },
    {
        path: 'superset-bi-reports',
        loadChildren: () => import('@admin-clients/cpanel-bi-superset-feature').then(m => m.BI_SUPERSET_REPORTS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.SUPERSET_BI_REPORTS',
            roles: [UserRoles.BI_USR]
        }
    },
    {
        path: 'bi-users',
        loadChildren: () => import('@admin-clients/cpanel/bi/feature').then(m => m.BI_USERS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.BI_USERS',
            roles: [UserRoles.BI_USR]
        }
    },
    {
        path: 'dashboard',
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'Dashboard',
            roles: [UserRoles.BI_USR, UserRoles.SYS_MGR, UserRoles.SYS_ANS]
        },
        children: [
            {
                path: '',
                loadComponent: () => import('@admin-clients/cpanel-bi-superset-feature')
                    .then(m => m.SupersetDashboardComponent)
            },
            {
                path: ':dashboardId',
                loadComponent: () => import('@admin-clients/cpanel-bi-superset-feature')
                    .then(m => m.SupersetDashboardComponent)
            }
        ]
    },
    {
        path: 'bi-subscriptions',
        loadChildren: () => import('@admin-clients/cpanel/bi/feature').then(m => m.BI_SUBSCRIPTIONS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MY_BI_SUBSCRIPTIONS',
            roles: [UserRoles.BI_USR]
        }
    },
    {
        path: 'products',
        loadChildren: () => import('@admin-clients/cpanel-products-my-products-feature').then(m => m.PRODUCTS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MY_PRODUCTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'delivery-points',
        loadChildren: () => import('@admin-clients/cpanel/products/delivery-points/feature').then(m => m.PRODUCTS_DELIVERY_POINTS_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.PRODUCTS_DELIVERY_POINTS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.ENT_MGR,
                UserRoles.ENT_ANS,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: 'fever-zone',
        loadChildren: () => import('@admin-clients/cpanel-fever-feature').then(m => m.FEVER_ZONE_ROUTES),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.FEVER_ZONE_REPORTING',
            roles: [UserRoles.FV_REPORTING]
        }
    },
    {
        path: 'packs',
        loadChildren: () => import('@admin-clients/cpanel/packs/my-packs/feature').then(m => m.routes),
        canActivate: [roleGuard],
        data: {
            breadcrumb: 'TITLES.MY_PACKS',
            roles: [
                UserRoles.OPR_MGR,
                UserRoles.EVN_MGR
            ]
        }
    },
    {
        path: '**',
        loadComponent: () => null,
        canActivate: [entityRoleGuard]
    }
];

export const routes: Routes = [
    {
        path: 'admin',
        redirectTo: route => {
            // Support of old links with admin prefix
            const router = inject(Router);
            const path = route.url.slice(1).map(u => u.path).join('/');
            const url = router.createUrlTree([path], { queryParams: route.queryParams });
            return url;
        }
    },
    {
        path: 'login',
        loadChildren: () => import('@admin-clients/shared/feature/login').then(m => m.routes),
        providers: [provideLoginBackgroundurl(() => getLoginBackgroundUrlByEnvironment())]
    },
    {
        path: '',
        canMatch: [authCanActivateGuard],
        loadComponent: () => NavComponent,
        children: protectedRoutes
    },
    {
        path: '**',
        loadComponent: () => null,
        canActivate: [entityRoleGuard]
    }
];
