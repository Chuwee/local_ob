import { Pipe, PipeTransform } from '@angular/core';
import { Event } from './event-pipes.model';

@Pipe({
    name: 'isActivityEvent',
    pure: true,
    standalone: true
})
export class IsActivityEventPipe implements PipeTransform {

    transform(event: Event): boolean {
        return event?.type === 'ACTIVITY';
    }

}
