import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProducerDetailsComponent } from './details/producer-details.component';
import { ProducerGeneralDataComponent } from './general-data/producer-general-data.component';
import { producerDetailsResolver } from './producer-details-resolver';

export const routes: Routes = [
    {
        path: '',
        component: ProducerDetailsComponent,
        resolve: {
            producer: producerDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: ProducerGeneralDataComponent,
                canDeactivate: [unsavedChangesGuard()],
                data: {
                    breadcrumb: 'PRODUCER.GENERAL_DATA'
                }
            }
        ]
    }
];