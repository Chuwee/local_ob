import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { NavTabMenuElement } from '@admin-clients/shared/common/ui/components';
import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { map } from 'rxjs';

enum Paths {
    subscriptionModes = 'subscription-modes',
    capacities = 'capacities',
    roles = 'roles',
    periodicities = 'periodicities'
}

const links = [
    { path: Paths.capacities, key: 'CHANNELS.MEMBER_EXTERNAL.CAPACITIES.TITLE' },
    { path: Paths.roles, key: 'CHANNELS.MEMBER_EXTERNAL.ROLES.TITLE' },
    { path: Paths.periodicities, key: 'CHANNELS.MEMBER_EXTERNAL.PERIODICITIES.TITLE' },
    { path: Paths.subscriptionModes, key: 'CHANNELS.MEMBER_EXTERNAL.SUBSCRIPTION_MODES.TITLE' }
];

interface Link {
    path: Paths;
    key: string;
    disabled?: boolean;
}

@Component({
    selector: 'app-channel-member-external-settings',
    templateUrl: './member-external-settings.component.html',
    styleUrls: ['./member-external-settings.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberExternalSettingsComponent {
    readonly #memberExternalService = inject(ChannelMemberExternalService);

    readonly links$ = this.#memberExternalService.channelCapacities.get$().pipe(
        map(channelCapacities => {
            if (channelCapacities && !channelCapacities.length) {
                return links.map(link => link.path !== Paths.capacities ? { ...link, disabled: true } : link);
            }
            return links;
        }),
        map((links: Link[]) => links.map(link => (
            {
                param: link.path,
                label: link.key,
                disabled: link.disabled,
                tooltip: {
                    text: 'CHANNELS.MEMBER_EXTERNAL.CAPACITIES_REQUIRED',
                    disabled: !link.disabled
                }
            } as NavTabMenuElement)
        ))
    );
}

