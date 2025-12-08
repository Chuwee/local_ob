export interface ChannelAuthVendorsSso {
    allowed: boolean;
    vendors: string[];
}

export interface ChannelAuthVendorsUserData {
    allowed: boolean;
    mandatory_login: boolean;
    editable_data: boolean;
    vendors: string[];
}
