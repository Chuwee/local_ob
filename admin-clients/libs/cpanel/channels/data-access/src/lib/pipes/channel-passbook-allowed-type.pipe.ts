import { Pipe, PipeTransform } from '@angular/core';
import { ChannelType, channelWebTypes, Channel } from '../models/_index';

@Pipe({
    name: 'isChannelPassbookAllowed',
    pure: true,
    standalone: true
})
export class IsChannelPassbookAllowed implements PipeTransform {

    transform(channel: Channel): boolean {
        return channelWebTypes.includes(channel.type) || channel.type === ChannelType.members;
    }

}
