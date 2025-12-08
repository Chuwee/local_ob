import { FORM_CONTROL_ERRORS } from '@OneboxTM/feature-form-control-errors';
import { FormErrors } from '@admin-clients/cpanel/core/data-access';
import { provideMaterialSettings } from '@admin-clients/shared/common/ui/ob-material';
import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { provideApplicationLocationStrategy } from '@admin-clients/shared/utility/utils';
import { importProvidersFrom } from '@angular/core';
import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { AngularFireModule } from '@angular/fire/compat';
import { AngularFireAnalyticsModule, CONFIG as FIRE_ANALYTICS_CONFIG, ScreenTrackingService } from '@angular/fire/compat/analytics';
import { AngularFirePerformanceModule, PerformanceMonitoringService } from '@angular/fire/compat/performance';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MomentDateAdapter, provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PreloadAllModules, provideRouter, RouteReuseStrategy, withPreloading } from '@angular/router';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';
import { IonicStorageModule } from '@ionic/storage-angular';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { Angulartics2Module } from 'angulartics2';
import { CPANEL_DATEPICKER_FORMATS } from '../../../cpanel-client/src/app/core/date-picker-formats';
import { APP_CONSTANTS } from './app-constants';
import { routes } from './app-routing.module';
import { APP_INITIALIZERS } from './app.initializers';
import { HTTP_INTERCEPTORS_LIST } from './app.interceptors';
import { TrackingService } from './core/services/tracking.service';
import { AuthModule } from './modules/auth/auth.module';
import { AuthService } from './modules/auth/services/auth.service';
import { FiltersModule } from './modules/filters/filters.module';
import { GlobalSearchModule } from './modules/global-search/global-search.module';
import { ProfileModule } from './modules/profile/profile.module';
import { TabsPageModule } from './modules/tabs/tabs.module';
import { HomeModule } from './pages/home/home.module';

// TODO: revisar root providers
export const providers = [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    provideIonicAngular(({ mode: 'md' })),
    provideFirebaseApp(() => initializeApp(window.__environment.firebase)),
    provideRouter(routes, withPreloading(PreloadAllModules)),
    provideApplicationLocationStrategy(),
    provideMaterialSettings(),
    provideMomentDateAdapter(CPANEL_DATEPICKER_FORMATS),
    provideTranslateService({
        loader: provideTranslateHttpLoader({
            prefix: './assets/i18n/',
            suffix: '.json'
        })
    }),
    ...APP_INITIALIZERS,
    ...HTTP_INTERCEPTORS_LIST,
    ...APP_CONSTANTS,
    DateTimePipe,
    TrackingService,
    ScreenTrackingService,
    PerformanceMonitoringService,
    {
        provide: DateAdapter,
        useClass: MomentDateAdapter,
        deps: [MAT_DATE_LOCALE]
    },
    {
        provide: AUTHENTICATION_SERVICE,
        useExisting: AuthService
    },
    {
        provide: FORM_CONTROL_ERRORS,
        useClass: FormErrors
    },
    {
        provide: FIRE_ANALYTICS_CONFIG,
        useValue: {
            send_page_view: true
        }
    },
    importProvidersFrom([
        BrowserModule,
        Angulartics2Module.forRoot(),
        IonicStorageModule.forRoot(),
        AuthModule,
        TabsPageModule,
        HomeModule,
        ProfileModule,
        FiltersModule,
        GlobalSearchModule,
        AngularFireModule.initializeApp(window.__environment.firebase),
        AngularFireAnalyticsModule,
        AngularFirePerformanceModule,
        BrowserAnimationsModule
    ])
];
