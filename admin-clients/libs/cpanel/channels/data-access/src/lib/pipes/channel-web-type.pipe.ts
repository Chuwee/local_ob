import { Pipe, PipeTransform } from '@angular/core';
import { Channel, channelWebTypes } from '../models/_index';

@Pipe({
    name: 'isWebChannel',
    pure: true,
    standalone: true
})
export class IsWebChannelPipe implements PipeTransform {

    transform(channel: Channel): boolean {
        return channelWebTypes.includes(channel?.type);
    }

}
