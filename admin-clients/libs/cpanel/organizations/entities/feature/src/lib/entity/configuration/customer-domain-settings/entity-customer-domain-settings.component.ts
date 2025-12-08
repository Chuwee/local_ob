import { DomainSettingsComponent } from '@admin-clients/cpanel-shared-feature-domain-settings';
import { AdminChannelsService } from '@admin-clients/cpanel/migration/channels/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { first, map } from 'rxjs';

@Component({
    standalone: true,
    selector: 'app-entity-customer-domain-settings',
    template: `
        <app-domain-settings [initData]="$domainSettings()" [loading]="$loading()" (dataChange)="updateDomainSettings($event)"/>
    `,
    imports: [DomainSettingsComponent],
    providers: [AdminChannelsService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCustomerDomainSettingsComponent implements OnInit, OnDestroy {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly #$entityId = toSignal(this.#entitiesSrv.getEntity$().pipe(map(entity => entity?.id)));
    readonly $loading = toSignal(this.#entitiesSrv.customerDomainSettings.loading$());
    readonly $domainSettings = toSignal(this.#entitiesSrv.customerDomainSettings.get$().pipe(first(Boolean)));

    ngOnInit(): void {
        this.#entitiesSrv.customerDomainSettings.load(this.#$entityId());
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.customerDomainSettings.clear();
    }

    updateDomainSettings(data: Partial<DomainSettings>): void {
        this.#entitiesSrv.customerDomainSettings.upsert(this.#$entityId(), data)
            .subscribe(() => this.#ephemeralMsgSrv.showSaveSuccess());
    }
}
