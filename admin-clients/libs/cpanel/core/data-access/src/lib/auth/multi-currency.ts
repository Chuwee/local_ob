import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationState } from './state/authentication.state';
import { User } from './user.model';

//TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
export const isMultiCurrency$ = (): Observable<boolean> => {
    const state = inject(AuthenticationState);
    return state.loggedUser.getValue$()
        .pipe(map(user => user?.use_multicurrency));
};

/** @deprecated Use AuthenticationService getLoggedUser user.currency instead*/
export const userCurrency$ = (): Observable<User['currency']> => {
    const state = inject(AuthenticationState);
    return state.loggedUser.getValue$()
        .pipe(map(user => user?.currency));
};
