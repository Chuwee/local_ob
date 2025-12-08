import { Pipe, PipeTransform } from '@angular/core';
import { Event } from './event-pipes.model';

@Pipe({
    name: 'isSessionPackEvent',
    pure: true,
    standalone: true
})
export class IsSessionPackEventPipe implements PipeTransform {

    transform(event: Event): boolean {
        return event?.settings?.session_pack !== 'DISABLED';
    }
}
