import { CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, delay, map, switchMap } from 'rxjs/operators';

export class ProductClientIdValidator {
    static createValidator(customersSrv: CustomersService, customerId: string, productId: number, entityId: string): AsyncValidatorFn {
        return (control: AbstractControl): Observable<ValidationErrors> =>
            of(control.value).pipe(
                delay(500),
                switchMap(value =>
                    customersSrv.checkIfClientIdExists(value, customerId, productId, entityId)
                        .pipe(
                            map(alreadyExists => alreadyExists ? { clientIdAlreadyExists: true } : null),
                            catchError(err => of({ error: err }))
                        )
                )
            );
    }
}
