import { Collective, CollectiveValidationMethod } from '@admin-clients/cpanel/collectives/data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isUserPassCollective',
    pure: true,
    standalone: true
})
export class IsUserPassCollectivePipe implements PipeTransform {

    constructor() { }

    transform(collective: Collective): boolean {
        return CollectiveValidationMethod.userPassword === collective.validation_method;
    }

}
