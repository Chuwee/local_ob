import { Pipe, PipeTransform } from '@angular/core';
import { Channel } from '../models/_index';

@Pipe({
    name: 'isExternalWhitelabelType',
    pure: true,
    standalone: true
})
export class IsExternalWhitelabelPipe implements PipeTransform {

    transform(channel: Channel): boolean {
        return channel.whitelabel_type === 'EXTERNAL';
    }

}
