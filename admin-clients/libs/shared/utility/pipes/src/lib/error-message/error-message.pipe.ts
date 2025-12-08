import { FORM_CONTROL_ERRORS } from '@OneboxTM/feature-form-control-errors';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { AbstractControl, StatusChangeEvent, TouchedChangeEvent } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';

@Pipe({
    standalone: true,
    name: 'obErrorMessage$'
})
export class ErrorMessage$Pipe implements PipeTransform {
    readonly #formErrors = inject(FORM_CONTROL_ERRORS);
    readonly #translate = inject(TranslateService);

    transform(ctrl: AbstractControl): Observable<string> {
        return ctrl.events
            .pipe(
                filter(event => event instanceof StatusChangeEvent || event instanceof TouchedChangeEvent),
                startWith({ source: ctrl }),
                map(event => {
                    if (event.source.status !== 'INVALID' || !event.source.touched) return;
                    const errorMessage = this.#formErrors.getErrorMessage(ctrl.errors);
                    const errorParams = this.#formErrors.getErrorParameters(ctrl.errors);
                    return errorMessage ? this.#translate.instant(errorMessage, errorParams) : null;
                })
            );
    }
}
