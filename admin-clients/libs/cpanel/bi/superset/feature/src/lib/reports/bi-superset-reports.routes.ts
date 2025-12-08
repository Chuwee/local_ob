import { BiSupersetSubmit, biSupersetSubmit } from '@admin-clients/cpanel-bi-utility-utils';
import { InjectionToken } from '@angular/core';
import { Routes } from '@angular/router';
import { BiSupersetReportsListComponent } from './list/bi-superset-reports-list.component';

export const BI_SUPERSET_SUBMIT = new InjectionToken<BiSupersetSubmit>('BI_SUPERSET_SUBMIT');

export const BI_SUPERSET_REPORTS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            {
                provide: BI_SUPERSET_SUBMIT,
                useValue: biSupersetSubmit
            }
        ],
        component: BiSupersetReportsListComponent
    }
];