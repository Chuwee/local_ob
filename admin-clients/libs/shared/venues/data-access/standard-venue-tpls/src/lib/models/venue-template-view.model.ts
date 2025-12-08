/* eslint-disable @typescript-eslint/naming-convention */
import { VenueTemplateViewLink } from './venue-template-view-link.model';

export interface VenueTemplateView {
    id: number;
    name: string;
    code: string;
    url?: string;
    root: boolean;
    links?: VenueTemplateViewLink[];
    aggregated_view?: boolean;
    display_3D?: boolean;
    orientation?: string;
    vip?: boolean;
}
