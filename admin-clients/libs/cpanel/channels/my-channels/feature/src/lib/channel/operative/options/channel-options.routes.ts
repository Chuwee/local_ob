import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { ChannelOptionsComponent } from './channel-options.component';

export const routes = [
    {
        path: '',
        component: ChannelOptionsComponent,
        canDeactivate: [unsavedChangesGuard()]
    }
];
