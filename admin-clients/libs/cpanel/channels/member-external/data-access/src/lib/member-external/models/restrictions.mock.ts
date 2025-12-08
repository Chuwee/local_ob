export const restrictionsStructure = [
    {
        fields: [
            {
                id: 'MINIMUM_MAXIMUM',
                type: 'STRING',
                container: 'SINGLE',
                source: 'MINIMUM_MAXIMUM'
            },
            {
                id: 'LIMIT_TICKETS_NUMBER',
                type: 'INTEGER',
                container: 'SINGLE'
            },
            {
                id: 'LIMIT_ROLES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'ROLE_ID'
            }
        ],
        restriction_type: 'LIMIT_TYPE'
    },
    {
        fields: [
            {
                id: 'RATIO_CONDITION_TICKETS_NUMBER',
                type: 'INTEGER',
                container: 'SINGLE'
            },
            {
                id: 'RATIO_ALLOWED_TICKETS_NUMBER',
                type: 'INTEGER',
                container: 'SINGLE'
            },
            {
                id: 'RATIO_ALLOWED_ROLES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'ROLE_ID'
            },
            {
                id: 'RATIO_CONDITION_ROLES',
                type: 'INTEGER',
                container: 'LIST',
                source: 'ROLE_ID'
            }
        ],
        restriction_type: 'RATIO_TYPE'
    }
];
