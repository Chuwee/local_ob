import { Pipe, PipeTransform } from '@angular/core';
import { Event } from './event-pipes.model';

@Pipe({
    name: 'isSgaEvent',
    pure: true,
    standalone: true
})
export class IsSgaEventPipe implements PipeTransform {

    transform(event: Event): boolean {
        return event?.additional_config?.inventory_provider === 'SGA';
    }

}
