export type ChannelWhitelabelSettings = {
    promotions: {
        locations: ChannelPromotionsLocations[];
        application_config: ChannelPromotionsApplicationConfig;
    };
    venue_map: {
        navigation_mode: ChannelNavigationModes;
        show_available_tickets: boolean;
        allow_price_range_filter: boolean;
        show_images_carousel: boolean;
        enabled_automatic_selection: boolean;
        preselected_items?: number;
        show_compacted_view_list?: boolean;
        force_side_panel_view_list?: boolean;
    };
    thank_you_page: {
        show_purchase_conditions?: boolean;
        modules?: ChannelWhitelabelModule[];
    };
    cart: {
        allow_keep_buying: boolean;
    };
    checkout: {
        checkout_flow: CheckoutFlowModes;
    };
    resend_tickets: {
        enabled: boolean;
    };
};

export type PutChannelWhitelabelSettings = Partial<ChannelWhitelabelSettings>;

export enum ChannelPromotionsLocations {
    seatSelectionDialog = 'SEAT_SELECTION_DIALOG',
    seatSelectionRequiredDialog = 'SEAT_SELECTION_REQUIRED_DIALOG'
}

export enum ChannelNavigationModes {
    views = 'VIEWS',
    priceZones = 'PRICE_ZONES'
}

export enum ChannelPromotionsApplicationConfig {
    auto = 'AUTO',
    default = 'DEFAULT'
}

export type CheckoutFlowModes = 'ONE_STEP' | 'TWO_STEP';

export type ChannelWhitelabelModule = {
    text_block_id: number;
    enabled: boolean;
    visible: boolean;
    type?: 'MAIN' | 'SMARTBOOKING';
};
