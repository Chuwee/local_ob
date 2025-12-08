import { Pipe, PipeTransform } from '@angular/core';
import { Channel, ChannelType } from '../models/_index';

@Pipe({
    name: 'isMembersChannel',
    pure: true,
    standalone: true
})
export class IsMembersChannelPipe implements PipeTransform {

    transform(channel: Channel): boolean {
        return ChannelType.members === channel.type;
    }

}
