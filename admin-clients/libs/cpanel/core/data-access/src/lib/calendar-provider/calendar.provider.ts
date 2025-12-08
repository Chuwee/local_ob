import { EnvironmentProviders, importProvidersFrom, Provider } from '@angular/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { CalendarDateFormatter, CalendarModule, CalendarMomentDateFormatter, DateAdapter, MOMENT } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/moment';
import moment from 'moment';

export function momentAdapterFactory(): DateAdapter {
    return adapterFactory(moment);
}

export function provideCalendarSettings(): Provider | EnvironmentProviders[] {
    return [
        {
            provide: MOMENT,
            useValue: moment
        },
        {
            provide: DateAdapter,
            useClass: MomentDateAdapter,
            deps: [MAT_DATE_LOCALE]
        },
        importProvidersFrom([
            CalendarModule.forRoot(
                {
                    provide: DateAdapter,
                    useFactory: momentAdapterFactory
                },
                {
                    dateFormatter: {
                        provide: CalendarDateFormatter,
                        useClass: CalendarMomentDateFormatter
                    }
                }
            )
        ])
    ];
}
