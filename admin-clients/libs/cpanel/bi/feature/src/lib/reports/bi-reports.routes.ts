import { BiSubmit, biSubmit } from '@admin-clients/cpanel-bi-utility-utils';
import { InjectionToken } from '@angular/core';
import { Routes } from '@angular/router';
import { BiReportsListComponent } from './list/bi-reports-list.component';

export const BI_SUBMIT = new InjectionToken<BiSubmit>('BI_SUBMIT');

export const BI_REPORTS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            {
                provide: BI_SUBMIT,
                useValue: biSubmit
            }
        ],
        children: [
            {
                path: '',
                component: BiReportsListComponent
            },
            {
                path: ':biReportId',
                loadChildren: () => import('./details/bi-report-details.routes').then(r => r.BI_REPORT_ROUTES),
                data: {
                    breadcrumb: 'BI.REPORTS.DETAILS.TITLE'
                }
            }
        ]
    }
];