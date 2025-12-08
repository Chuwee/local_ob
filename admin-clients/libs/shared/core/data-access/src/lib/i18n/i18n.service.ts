/* eslint-disable @typescript-eslint/naming-convention */
import { Injectable } from '@angular/core';
import { DateAdapter } from '@angular/material/core';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import '@angular/common/locales/global/es';
import '@angular/common/locales/global/eu';
import '@angular/common/locales/global/en';
import '@angular/common/locales/global/ca';
import '@angular/common/locales/global/ko';
import '@angular/common/locales/global/en-GB';
import '@angular/common/locales/global/fr';
import '@angular/common/locales/global/it';
import '@angular/common/locales/global/pt';
import 'moment/locale/es';
import 'moment/locale/eu';
import 'moment/locale/ca';
import 'moment/locale/ko';
import 'moment/locale/en-gb';
import 'moment/locale/fr';
import 'moment/locale/it';
import 'moment/locale/pt';
import { Observable } from 'rxjs';
import { getCurrencySymbol } from '@angular/common';

const SUPPORTED_LANGUAGES = {
    'es-ES': {},
    'en-US': {},
    'ca-ES': {},
    'eu-ES': {},
    'ko-KR': { format: '1.0-0' },
    'en-GB': {},
    'fr-FR': {},
    'it-IT': {},
    'pt-BR': {}
};
const DEFAULT_LANG = SUPPORTED_LANGUAGES[navigator.language] ? navigator.language : 'en-US';

//const DEFAULT_LANG_PREFIX = 'en';

@Injectable({
    providedIn: 'root'
})
export class I18nService {
    private _locale: string;
    private _timezone: string;
    private _isSetDefaultLang = false;

    constructor(
        private _translate: TranslateService,
        private _dateAdapter: DateAdapter<moment.Moment>
    ) { }

    setLocale(lang = DEFAULT_LANG): Observable<unknown> {
        if (!this._isSetDefaultLang) {
            this._isSetDefaultLang = true;
            this._translate.setFallbackLang(lang);
        }
        const prefixLang = this._locale = lang.split('-')[0];
        // this.lazyLocaleInitialization(prefixLang);
        this._dateAdapter.setLocale(lang);

        moment.locale(prefixLang); // backup value
        moment.locale(lang); // if fullLocale is not available, fallback to backup language

        // if async lang loading problems persists, see this link for an alternative solution:
        // https://github.com/ngx-translate/core/issues/1086#issuecomment-508779160
        return this._translate.use(lang);
    }

    getLocale(): string {
        return this._locale;
    }

    getLocaleFormat(): string {
        return SUPPORTED_LANGUAGES[this._translate.getFallbackLang()]?.format;
    }

    getTimezone(): string {
        return this._timezone;
    }

    setTimezone(value: string): void {
        this._timezone = value;
        moment.tz.setDefault(value);
    }

    getSupportedLanguages(): string[] {
        return Array.from(Object.keys(SUPPORTED_LANGUAGES));
    }

    //TODO: translate CURRENCIES.${code} should be from a currencies document, like countries, etc
    getCurrencyFullTranslation(code: string): string {
        return `${code} - ${getCurrencySymbol(code, 'narrow', this._locale)} ( ${this._translate.instant(`CURRENCIES.${code}`)} )`;
    }

    getCurrencyPartialTranslation(code: string): string {
        return `${code} - ${getCurrencySymbol(code, 'narrow', this._locale)}`;
    }

    /**
     * Languages allowed:
     * <ul>
     *  <li>en (default)</li>
     *  <li>es</li>
     *  <li>ca</li>
     * </ul>
     * @param prefixLang The prefix of the language to be setted
     */
    /*private lazyLocaleInitialization(prefixLang: string): void {
        if (DEFAULT_LANG_PREFIX !== prefixLang) {
            import(
                /!* webpackInclude: /(ca|es)\.js$/ *!/
                `@angular/common/locales/${prefixLang}`
            ).then(module => registerLocaleData(module.default));
        }
    }*/
}
