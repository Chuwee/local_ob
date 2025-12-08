export interface Event {
    additional_config?: {
        inventory_provider?: 'AVET' | 'SGA' | 'ONEBOX' | 'SEETICKETS' | 'ITALIAN_COMPLIANCE';
    };
    type: 'NORMAL' | 'AVET' | 'ACTIVITY' | 'THEME_PARK' | 'SEASON_TICKET';
    settings: {
        session_pack: 'DISABLED' | 'RESTRICTED' | 'UNRESTRICTED';
    };
}
