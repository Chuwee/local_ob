import { BaseContentImage } from '@admin-clients/shared/data-access/models';
import { TicketTemplateImageType } from './ticket-template-image-type.enum';

export interface PostTicketTemplateImage extends BaseContentImage<TicketTemplateImageType> { }

export interface GetTicketTemplateImage extends Omit<PostTicketTemplateImage, 'image'> {
    image_url: string;
}
