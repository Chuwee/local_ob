import { VenueTemplateElementInfoImage } from './venue-template-element-info-image.model';

export interface VenueTemplateElementInfoDefaultInfo {
    name?: Record<string, string>;
    description?: Record<string, string>;
    badge?: {
        text: Record<string, string>;
        text_color: string;
        background_color: string;
    };
    feature_list?: Record<string, FeatureElement[]>;
    // eslint-disable-next-line @typescript-eslint/naming-convention
    config_3D?: {
        enabled: boolean;
        codes?: string[];
    };
    image_settings?: Record<VenueTemplateElementInfoImage['type'],
        {
            enabled: boolean;
            images?: Record<string, VenueTemplateElementInfoImage[]>;
        }>;
    restriction?: {
        enabled: boolean;
        texts: Record<string, RestrictionElement>;
    };
    templates_zones_ids?: number[];
}

export interface FeatureElement {
    url?: string;
    type: FeatureType;
    action?: FeatureAction;
    text: string;
}

export enum FeatureType {
    link = 'LINK',
    text = 'TEXT'
}

export enum FeatureAction {
    modal = 'MODAL',
    newTab = 'NEWTAB',
    currentTab = 'CURRENTTAB'
}
export interface RestrictionElement {
    description?: string;
    agreement: RestrictionCheck[];
}

export interface RestrictionCheck {
    text: string;
    position: number;
}
