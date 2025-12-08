import { Pipe, PipeTransform } from '@angular/core';
import { Channel, channelAfterPromotionTypes } from '../models/_index';

@Pipe({
    name: 'channelAfterPromotion',
    pure: true,
    standalone: true
})
export class ChannelAfterPromotionPipe implements PipeTransform {

    transform(channel: Channel): boolean {
        return channelAfterPromotionTypes.includes(channel.type);
    }

}
