import { DynamicConfigurations } from './dynamic-configuration.model';

export const structure: DynamicConfigurations = [
    {
        implementation: 'ChangeSeatCapacityPeriodicityValidator',
        type: 'VALIDATION',
        fields: [
            {
                id: 'ALLOWED_CAPACITIES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'CAPACITY_ID'
            },
            {
                id: 'ALLOWED_ROLES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'ROLE_ID'
            },
            {
                id: 'EXCLUDED_PERIODICITIES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'PERIODICITY_ID'
            },
            {
                id: 'COMMENT_ID',
                type: 'INTEGER',
                container: 'SINGLE'
            },
            {
                id: 'COMMENT_CONTENT',
                type: 'STRING',
                container: 'SINGLE'
            },
            {
                id: 'PERMISSION_WHITE_LIST',
                type: 'INTEGER',
                container: 'LIST'
            },
            {
                id: 'PERMISSION_BLACK_LIST',
                type: 'INTEGER',
                container: 'LIST'
            }
        ],
        operation_name: 'CHANGE_SEAT_VALIDATOR',
        order_type: 'CHANGE_SEAT'
    },
    {
        implementation: 'BuySeatRolePermissionValidator',
        type: 'VALIDATION',
        fields: [
            {
                id: 'MEMBER_ROLE_RELATIONS',
                type: 'INTEGER',
                container: 'MAP',
                target: 'ROLE_ID'
            },
            {
                id: 'PAYED_TERMS_EXCLUSIONS',
                type: 'INTEGER',
                container: 'LIST',
                source: 'TERM_ID'
            },
            {
                id: 'PERMISSION_WHITE_LIST',
                type: 'INTEGER',
                container: 'LIST'
            },
            {
                id: 'PERMISSION_BLACK_LIST',
                type: 'INTEGER',
                container: 'LIST'
            }
        ],
        operation_name: 'BUY_SEAT_VALIDATOR',
        order_type: 'BUY_SEAT'
    },
    {
        implementation: 'RenewalPermissionsValidator',
        type: 'VALIDATION',
        fields: [
            {
                id: 'PERMISSION_WHITE_LIST',
                type: 'INTEGER',
                container: 'LIST'
            },
            {
                id: 'PERMISSION_BLACK_LIST',
                type: 'INTEGER',
                container: 'LIST'
            }
        ],
        operation_name: 'RENEWAL_VALIDATOR',
        order_type: 'RENEWAL'
    },
    {
        implementation: 'RenewalMultiplePaymentsPermissionsValidator',
        type: 'VALIDATION',
        fields: [
            {
                id: 'PERMISSION_WHITE_LIST',
                type: 'INTEGER',
                container: 'LIST'
            },
            {
                id: 'PERMISSION_BLACK_LIST',
                type: 'INTEGER',
                container: 'LIST'
            }
        ],
        operation_name: 'RENEWAL_VALIDATOR',
        order_type: 'RENEWAL'
    },
    {
        implementation: 'SubscriptionModePeriodicityInferer',
        type: 'INFERER',
        fields: [
            {
                id: 'LIGA_SUBSCRIPTION_MODES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'PERIODICITY_ID'
            },
            {
                id: 'CHAMPIONS_SUBSCRIPTION_MODES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'PERIODICITY_ID'
            }
        ],
        operation_name: 'SUBSCRIPTION_MODE_INFERER',
        order_type: 'CHANGE_SEAT'
    },
    {
        implementation: 'NewSeatPartnerRolesRelationCalculator',
        type: 'INFERER',
        fields: [
            {
                id: 'MEMBER_ROLE_RELATIONS',
                type: 'INTEGER',
                container: 'MAP',
                target: 'ROLE_ID'
            }
        ],
        operation_name: 'ROLE_INFERER',
        order_type: 'CHANGE_SEAT'
    },
    {
        implementation: 'NewSeatPeriodicityTermPriceValladolidCalculator',
        type: 'PRICE_CALCULATOR',
        fields: [
            {
                id: 'PERIODICITY_TERM_RELATIONS',
                type: 'INTEGER',
                container: 'MAP',
                source: 'PERIODICITY_ID',
                target: 'TERM_ID'
            }
        ],
        operation_name: 'NEW_SEAT_PRICE',
        order_type: 'BUY_SEAT'
    },
    {
        implementation: 'NewSeatPeriodicityTermPriceCalculator',
        type: 'PRICE_CALCULATOR',
        fields: [
            {
                id: 'PERIODICITY_TERM_RELATIONS',
                type: 'INTEGER',
                container: 'MAP',
                source: 'PERIODICITY_ID',
                target: 'TERM_ID'
            }
        ],
        operation_name: 'NEW_SEAT_PRICE',
        order_type: 'BUY_SEAT'
    },
    {
        implementation: 'NewSeatPeriodicityPriceCalculator',
        type: 'PRICE_CALCULATOR',
        fields: [
            {
                id: 'NUMBER_OF_TERMS',
                type: 'INTEGER',
                container: 'SINGLE'
            },
            {
                id: 'PERIODICITY_TERM_RELATIONS',
                type: 'INTEGER',
                container: 'MAP',
                source: 'PERIODICITY_ID',
                target: 'TERM_ID'
            }
        ],
        operation_name: 'NEW_SEAT_PRICE',
        order_type: 'BUY_SEAT'
    },
    {
        implementation: 'PreviousSeatCapacityPeriodicityPriceCalculator',
        type: 'PRICE_CALCULATOR',
        fields: [
            {
                id: 'ALLOWED_CAPACITIES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'CAPACITY_ID'
            },
            {
                id: 'EXCLUDED_PERIODICITIES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'PERIODICITY_ID'
            }
        ],
        operation_name: 'PREVIOUS_SEAT_PRICE',
        order_type: 'CHANGE_SEAT'
    },
    {
        implementation: 'ChangeSeatCapacityPeriodicityValidator',
        type: 'PRICE_CALCULATOR',
        fields: [
            {
                id: 'ALLOWED_CAPACITIES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'CAPACITY_ID'
            },
            {
                id: 'ALLOWED_ROLES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'ROLE_ID'
            },
            {
                id: 'EXCLUDED_PERIODICITIES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'PERIODICITY_ID'
            },
            {
                id: 'PERMISSION_WHITE_LIST',
                type: 'INTEGER',
                container: 'LIST'
            },
            {
                id: 'PERMISSION_BLACK_LIST',
                type: 'INTEGER',
                container: 'LIST'
            }
        ],
        operation_name: 'PREVIOUS_SEAT_PRICE',
        order_type: 'CHANGE_SEAT'
    }
];
