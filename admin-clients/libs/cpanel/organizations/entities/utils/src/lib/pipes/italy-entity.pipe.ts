import { Entity, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { Injectable, Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isItalianEntity',
    pure: true,
    standalone: true
})
@Injectable({ providedIn: 'root' })
export class IsItalianEntityPipe implements PipeTransform {
    constructor() { }

    transform(entity: Entity): boolean {
        return entity?.inventory_providers?.includes(ExternalInventoryProviders.italianCompliance);
    }
}
