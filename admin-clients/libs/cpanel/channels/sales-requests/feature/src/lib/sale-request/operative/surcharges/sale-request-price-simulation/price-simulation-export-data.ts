import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataPriceSimulation: FieldDataGroup[] = [
    {
        fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.VENUE_CONFIG',
        field: 'venue_config',
        isDefault: true,
        fields: [
            {
                field: 'venue_config_id',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.VENUE_CONFIG_ID',
                isDefault: true
            },
            {
                field: 'venue_config_name',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.VENUE_CONFIG_NAME',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE',
        field: 'rate',
        isDefault: true,
        fields: [
            {
                field: 'rate_id',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_ID',
                isDefault: true
            },
            {
                field: 'rate_name',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_NAME',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE',
        field: 'rate_price_type',
        isDefault: true,
        fields: [
            {
                field: 'rate_price_type_id',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_ID',
                isDefault: true
            },
            {
                field: 'rate_price_type_name',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_NAME',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION',
        field: 'rate_price_type_simulation_price',
        isDefault: true,
        fields: [
            {
                field: 'rate_price_type_simulation_price_base',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_BASE',
                isDefault: true
            },
            {
                field: 'rate_price_type_simulation_price_total',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_TOTAL',
                isDefault: true
            },
            {
                field: 'rate_price_type_simulation_price_surcharge_value',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_SURCHARGE_VALUE',
                isDefault: true
            },
            {
                field: 'rate_price_type_simulation_price_surcharge_type',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_SURCHARGE_TYPE',
                isDefault: true
            },
            {
                field: 'rate_price_type_simulation_price_promotion_id',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_PROMOTION_ID',
                isDefault: true
            },
            {
                field: 'rate_price_type_simulation_price_promotion_name',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_PROMOTION_NAME',
                isDefault: true
            },
            {
                field: 'rate_price_type_simulation_price_promotion_type',
                fieldKey: 'SALE_REQUEST.PRICE_SIMULATION.EXPORT.RATE_PRICE_TYPE_SIMULATION_PROMOTION_TYPE',
                isDefault: true
            }
        ]
    }
];

