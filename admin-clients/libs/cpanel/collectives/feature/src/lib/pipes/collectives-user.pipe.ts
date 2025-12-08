import { Collective, CollectiveValidationMethod } from '@admin-clients/cpanel/collectives/data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isUserCollective',
    pure: true,
    standalone: true
})
export class IsUserCollectivePipe implements PipeTransform {

    constructor() { }

    transform(collective: Collective): boolean {
        return CollectiveValidationMethod.user === collective.validation_method;
    }

}
