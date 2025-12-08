import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { provideTranslateService } from '@ngx-translate/core';
import { I18nService } from './i18n.service';
import { translateHttpLoaderFactory } from './translate-http-loader-factory';

const CPANEL_DATEPICKER_FORMATS = {
    parse: {
        dateInput: 'L'
    },
    display: {
        dateInput: 'L',
        dateA11yLabel: 'L',
        monthYearLabel: 'MMM YYYY',
        monthYearA11yLabel: 'MMMM YYYY'
    }
};

@NgModule({
    providers: [
        {
            provide: ENVIRONMENT_TOKEN,
            useValue: {
                production: false,
                hjid: '',
                env: 'pre',
                branch: 'default',
                uaTrackingId: 'UA-16191346-22',
                wsHost: `wss://mgmt-stream.oneboxtds.net:443`,
                pingdomCode: 'pa-5f719b95e24f920015000044',
                captchaSiteKey: '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI'
            }
        },
        {
            provide: DateAdapter,
            useClass: MomentDateAdapter,
            deps: [MAT_DATE_LOCALE]
        },
        {
            provide: MAT_DATE_FORMATS,
            useValue: CPANEL_DATEPICKER_FORMATS
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideTranslateService({
            loader: translateHttpLoaderFactory()
        })
    ]
})
export class StorybookI18nModule {
    constructor(i18n: I18nService) {
        i18n.setTimezone('Europe/Berlin');
        i18n.setLocale('en-US');
    }
}
