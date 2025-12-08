import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseStatePropGroup } from './base-state-prop-group';

/** @deprecated Use StateProperty instead*/
export class BaseStateProp<T> {
    private _value = new BaseStatePropGroup<T>();
    private _inProgress = new BaseStatePropGroup<boolean>();
    private _error = new BaseStatePropGroup<HttpErrorResponse>();

    constructor(initValue: T = null) {
        if (initValue !== null) {
            this._value.getSetterFunction()(initValue);
        }
    }

    setValueFunction(): (value: T) => void { return this._value.getSetterFunction(); }

    getValueFunction(): () => Observable<T> { return this._value.getGetterFunction(); }

    setInProgressFunction(): (value: boolean) => void { return this._inProgress.getSetterFunction(); }

    getInProgressFunction(): () => Observable<boolean> { return this._inProgress.getGetterFunction(); }

    setErrorFunction(): (value: HttpErrorResponse) => void { return this._error.getSetterFunction(); }

    getErrorFunction(): () => Observable<HttpErrorResponse> { return this._error.getGetterFunction(); }

}
