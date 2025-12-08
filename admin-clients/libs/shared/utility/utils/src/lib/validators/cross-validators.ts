import { AbstractControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

export function joinCrossValidations(controlsToValidate: AbstractControl[], takeUntilObs: Observable<void>): void {
    const semaphore = { red: false };
    controlsToValidate.forEach(control => setCrossValidation(control, semaphore, controlsToValidate, takeUntilObs));
}

function setCrossValidation(
    targetControl: AbstractControl, semaphore: { red: boolean }, controlsToValidate: AbstractControl[], takeUntilObs: Observable<void>
): void {
    if (targetControl) {
        targetControl.valueChanges
            .pipe(filter(() => !semaphore.red), takeUntil(takeUntilObs))
            .subscribe(() => propagueValidation(targetControl, semaphore, controlsToValidate));
    }
}

function propagueValidation(targetControl: AbstractControl, semaphore: { red: boolean }, controlsToValidate: AbstractControl[]): void {
    semaphore.red = true;
    controlsToValidate.forEach(control => {
        if (control && control !== targetControl) {
            control.updateValueAndValidity();
        }
    });
    semaphore.red = false;
}
