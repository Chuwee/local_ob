import { Routes } from '@angular/router';
import { BiReportPromptsComponent } from '../prompts/bi-report-prompts.component';
import { biReportDetailsResolver } from './bi-report-details-resolver';
import { BiReportDetailsComponent } from './bi-report-details.component';

export const BI_REPORT_ROUTES: Routes = [
    {
        path: '',
        component: BiReportDetailsComponent,
        resolve: {
            biReportDetailsData: biReportDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'prompts',
                pathMatch: 'full'
            },
            {
                path: 'prompts',
                component: BiReportPromptsComponent
            }
        ]
    }
];
