import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { Language } from '../model/language.model';

@Injectable({ providedIn: 'root' })
export class LanguagesState {
    private _languages = new BaseStateProp<Language[]>();
    readonly setLanguages = this._languages.setValueFunction();
    readonly getLanguages$ = this._languages.getValueFunction();
    readonly isLanguagesInProgress$ = this._languages.getInProgressFunction();
    readonly setLanguagesInProgress$ = this._languages.setInProgressFunction();
}
