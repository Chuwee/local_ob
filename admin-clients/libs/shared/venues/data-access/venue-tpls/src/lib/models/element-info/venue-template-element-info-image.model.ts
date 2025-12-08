import { ContentImage } from '@admin-clients/shared/data-access/models';

export interface VenueTemplateElementInfoImage extends Partial<ContentImage<'SLIDER' | 'HIGHLIGHTED'>> {
    thumbnail?: string;
    position?: number;
}
