import { Entity } from '@admin-clients/shared/common/data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isB2bEntity',
    pure: true,
    standalone: true
})
export class IsB2bEntityPipe implements PipeTransform {
    constructor() { }

    transform(entity: Entity): boolean {
        return entity?.settings.enable_B2B;
    }
}
