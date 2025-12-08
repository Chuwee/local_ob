import {
    ProductChannelSaleRequestStatus, ProductChannelStatusIndicator, ProductStatus
} from '@admin-clients/cpanel/products/my-products/data-access';

export function getSaleStatusIndicator(
    saleRequestStatus: ProductChannelSaleRequestStatus, productStatus: ProductStatus
): ProductChannelStatusIndicator {
    let indicator: ProductChannelStatusIndicator;
    switch (saleRequestStatus) {
        case 'PENDING':
            indicator = 'pending';
            break;
        case 'REJECTED':
            indicator = 'rejected';
            break;
        case 'ACCEPTED':
            indicator = productStatus === ProductStatus.active ? 'success' : 'neutral';
            break;
        case 'PENDING_REQUEST':
        default:
            indicator = 'neutral';
    }
    return indicator;
}
