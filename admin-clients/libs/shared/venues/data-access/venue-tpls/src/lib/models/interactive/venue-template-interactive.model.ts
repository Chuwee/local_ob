export interface VenueTemplateInteractive {
    enabled: boolean;
    multimedia_content_code: string;
    external_minimap_id: string;
    external_plugins?: {
        id: number;
        name: string;
        type: string;
        enabled: boolean;
    }[];
}

