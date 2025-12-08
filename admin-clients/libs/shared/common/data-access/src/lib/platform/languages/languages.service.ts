import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize, take } from 'rxjs/operators';
import { LanguagesApi } from './api/languages.api';
import { Language } from './model/language.model';
import { LanguagesState } from './state/languages.state';

@Injectable({
    providedIn: 'root'
})
export class LanguagesService {

    constructor(
        private _languagesApi: LanguagesApi,
        private _languageState: LanguagesState
    ) {
    }

    loadLanguages(platform = false): void { // set platform as true to only retrieve cpanel
        this._languageState.getLanguages$().pipe(take(1))
            .subscribe(availableLanguages => {
                const req: { platform?: boolean } = {};
                if (platform) {
                    req.platform = true;
                }
                if (!availableLanguages) {
                    this._languageState.setLanguagesInProgress$(true);
                    this._languagesApi.getLanguages(req)
                        .pipe(finalize(() => this._languageState.setLanguagesInProgress$(false)))
                        .subscribe(languages => this._languageState.setLanguages(languages));
                }
            });
    }

    clearLanguages(): void {
        this._languageState.setLanguages(null);
    }

    getLanguages$(): Observable<Language[]> {
        return this._languageState.getLanguages$();
    }

    isLanguagesInProgress$(): Observable<boolean> {
        return this._languageState.isLanguagesInProgress$();
    }

}
