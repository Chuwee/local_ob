import { DomainSettingsComponent } from '@admin-clients/cpanel-shared-feature-domain-settings';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { AdminChannelsService } from '@admin-clients/cpanel/migration/channels/data-access';
import { DomainConfiguration, DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { first, map } from 'rxjs';

@Component({
    selector: 'app-admin-channel-subdomain',
    template: `
        <app-domain-settings [initData]="$domainSettings()" [canConfigure]="true"
        [loading]="$loading()" (configurationChange)="updateConfiguration($event)" (dataChange)="updateSettings($event)"/>
    `,
    imports: [DomainSettingsComponent],
    providers: [AdminChannelsService, ChannelsService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminChannelSubdomainComponent implements OnInit {
    readonly #adminChannelSrv = inject(AdminChannelsService);
    readonly #channelSrv = inject(ChannelsService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly #$channelId = toSignal(this.#channelSrv.getChannel$().pipe(map(channel => channel.id)));
    readonly $loading = toSignal(this.#adminChannelSrv.channelSubdomainSettings.loading$());
    readonly $domainSettings = toSignal(this.#adminChannelSrv.channelSubdomainSettings.get$().pipe(first(Boolean)));
    readonly $domainConfig = toSignal(this.#adminChannelSrv.channelDomainConfiguration.get$());

    ngOnInit(): void {
        this.#adminChannelSrv.channelSubdomainSettings.load(this.#$channelId());
    }

    updateSettings(data: Partial<DomainSettings>): void {
        this.#adminChannelSrv.channelSubdomainSettings.upsert$(this.#$channelId(), data)
            .subscribe(() => {
                this.#ephemeralMsgSrv.showSaveSuccess();
                this.#adminChannelSrv.channelSubdomainSettings.load(this.#$channelId());
            });
    }

    updateConfiguration(e: { domain: string; configuration: DomainConfiguration }): void {
        this.#adminChannelSrv.channelDomainConfiguration
            .update$(e.domain, e.configuration)
            .subscribe(() => {
                this.#ephemeralMsgSrv.showSaveSuccess();
                this.#adminChannelSrv.channelDomainConfiguration.load(e.domain);
            });
    }

}
