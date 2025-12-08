import { B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, delay, map, switchMap } from 'rxjs/operators';

export class B2bClientTaxIdValidator {
    static createValidator(b2bService: B2bService, entityId?: number, currentB2bClientId?: number): AsyncValidatorFn {
        return (control: AbstractControl): Observable<ValidationErrors> =>
            of(control.value).pipe(
                delay(500),
                switchMap(value =>
                    b2bService.checkIfTaxIdExists(value, entityId, currentB2bClientId)
                        .pipe(
                            map(alreadyExists => alreadyExists ? { taxIdAlreadyExists: true } : null),
                            catchError(err => of({ error: err }))
                        )
                )
            );
    }
}
