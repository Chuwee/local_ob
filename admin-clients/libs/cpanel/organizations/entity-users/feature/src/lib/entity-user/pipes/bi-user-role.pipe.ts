import { EntityUserRole, EntityUserRoles } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'isBiUser',
    pure: true,
    standalone: true
})
export class IsBiUserPipe implements PipeTransform {

    transform(role: EntityUserRole): boolean {
        return role.code === EntityUserRoles.BI_USR;
    }
}
