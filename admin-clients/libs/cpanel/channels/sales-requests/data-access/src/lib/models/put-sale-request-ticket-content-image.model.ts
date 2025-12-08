import { ContentImage } from '@admin-clients/shared/data-access/models';
import { SaleRequestTicketContentImageType } from './sale-request-ticket-content-image-type.enum';

export interface PutSaleRequestTicketContentImage extends ContentImage<SaleRequestTicketContentImageType> {
}
