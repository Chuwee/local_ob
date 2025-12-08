import { channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { SaleRequest } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isSaleRequestWebChannel',
    pure: true,
    standalone: false
})
export class IsSaleRequestWebChannelPipe implements PipeTransform {

    constructor() { }

    transform(saleRequest: SaleRequest): boolean {
        return channelWebTypes.includes(saleRequest.channel.type);
    }

}
