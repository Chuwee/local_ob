export type ShiConfiguration = {
    sales: SalesConfiguration;
};

export type PatchShiConfiguration = {
    sales: PatchSalesConfiguration;
};

export type SalesConfiguration = {
    export: ExportSalesConfiguration;
    retry_confirm_errors: string[];
    retry_fulfill_errors: string[];
};

export type PatchSalesConfiguration = Partial<SalesConfiguration>;

export type ExportSalesConfiguration = {
    delivery: SalesConfigurationDelivery;
};

export type SalesConfigurationDelivery = {
    enabled: boolean;
    emails: string[];
};
