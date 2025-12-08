import { Pipe, PipeTransform } from '@angular/core';
import { Channel, ChannelType } from '../models/_index';

@Pipe({
    name: 'isBoxOffice',
    pure: true,
    standalone: true
})
export class IsBoxOfficePipe implements PipeTransform {

    transform(channel: Channel): boolean {
        return ChannelType.boxOffice === channel.type;
    }

}
