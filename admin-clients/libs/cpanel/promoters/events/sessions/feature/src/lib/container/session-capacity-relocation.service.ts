import { AuthenticationService, User, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class SessionCapacityRelocationService {
    readonly #$isRelocating = new BehaviorSubject(false);
    readonly #RELOCATION_ROLES = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];

    canRelocate(user: User): boolean {
        return AuthenticationService.isSomeRoleInUserRoles(user, this.#RELOCATION_ROLES);
    }

    isRelocating(): Observable<boolean> {
        return this.#$isRelocating.asObservable();
    }

    setIsRelocating(isRelocating: boolean): void {
        this.#$isRelocating.next(isRelocating);
    }
}
