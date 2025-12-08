import { Injectable, Pipe, PipeTransform } from '@angular/core';
import { Event } from './event-pipes.model';

@Pipe({
    name: 'isItalianComplianceEvent',
    pure: true,
    standalone: true
})
@Injectable({ providedIn: 'root' })
export class IsItalianComplianceEventPipe implements PipeTransform {
    transform(event: Event): boolean {
        return event?.additional_config?.inventory_provider === 'ITALIAN_COMPLIANCE';
    }
}
