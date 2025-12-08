import { Pipe, PipeTransform } from '@angular/core';
import { Channel, channelVoucherWebTypes } from '../models/_index';

@Pipe({
    name: 'isWebChannelVouchers',
    pure: true,
    standalone: true
})
export class IsWebChannelVouchersPipe implements PipeTransform {

    transform(channel: Channel): boolean {
        return channelVoucherWebTypes.includes(channel.type);
    }

}
