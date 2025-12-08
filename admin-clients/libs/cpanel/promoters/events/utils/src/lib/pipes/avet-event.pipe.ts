import { Pipe, PipeTransform } from '@angular/core';
import { Event } from './event-pipes.model';

@Pipe({
    name: 'isAvetEvent',
    pure: true,
    standalone: true
})
export class IsAvetEventPipe implements PipeTransform {

    transform(event: Event): boolean {
        return event?.type === 'AVET';
    }

}
