import { Pipe, PipeTransform } from '@angular/core';
import { Channel, ChannelType } from '../models/_index';

@Pipe({
    name: 'isWebB2bChannel',
    pure: true,
    standalone: true
})
export class IsWebB2bPipe implements PipeTransform {

    transform(channel: Pick<Channel, 'type'>): boolean {
        return channel?.type === ChannelType.webB2B;
    }

}
