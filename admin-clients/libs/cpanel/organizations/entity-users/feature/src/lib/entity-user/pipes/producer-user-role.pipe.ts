import { EntityUserRole, EntityUserRoles } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isProducerUser',
    pure: true,
    standalone: true
})
export class IsProducerUserPipe implements PipeTransform {

    transform(role: EntityUserRole): boolean {
        return role.code === EntityUserRoles.PRD_ANS;
    }
}
